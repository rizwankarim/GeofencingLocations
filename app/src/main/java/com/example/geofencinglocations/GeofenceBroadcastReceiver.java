package com.example.geofencinglocations;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.geofencinglocations.api.Api;
import com.example.geofencinglocations.api.RequestHandler;
import com.example.geofencinglocations.models.Example;
import com.example.geofencinglocations.models.nearbyPlace;
import com.example.geofencinglocations.models.place;
import com.example.geofencinglocations.retrofit.ApiClient;
import com.example.geofencinglocations.retrofit.ApiInterface;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    public static String StartTime = "";
    public static String EndTime = "";
    public GeofencingClient geofencingClient;
    public GeofenceHelper geofenceHelper;
    public String GEOFENCE_ID = "SOME_GEOFENCE_ID";
    int min;
    public place myPlace;
    public LatLng myLatlng;
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    List<nearbyPlace> nearbyDetails;
    String address;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //Toast.makeText(context,"Geofence triggered...",Toast.LENGTH_SHORT).show();

        geofencingClient = LocationServices.getGeofencingClient(context);
        geofenceHelper = new GeofenceHelper(context);
        NotificationHelper notificationHelper = new NotificationHelper(context);
        //myLatlng = new LatLng(24.436632, 67.636622);
        myLatlng = new LatLng(0.0, 0.0);

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.d("GeofenceBroadcastReceiv", ":onReceive error receiving geofencing event..");
        }

        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        for (Geofence geofence : geofenceList) {
            Log.d("GeofenceBroadcastReceiv", ":onReceive:" + geofence.getRequestId());
        }
        int transitionType = geofencingEvent.getGeofenceTransition();

        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Toast.makeText(context, "Entered the selected zone", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("Entry", "Entering on selected zone at " + StartTime, MainActivity.class);
                StartTime = getTimeOnTransaction();
                getNearByDetails(context,myLatlng,"AIzaSyDazjxsJFdohTwZllHdMsacB4P9luVjqyE");
                break;

            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Toast.makeText(context, "In the selected zone", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("Dwell", "In the selected zone", MainActivity.class);
                break;

            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Toast.makeText(context, "Exit from the selected zone", Toast.LENGTH_SHORT).show();
                ArrayList<LatLng> currentLocationsList = getArrayList(context, "locList");
                Toast.makeText(context, "size " + Integer.toString(currentLocationsList.size()), Toast.LENGTH_SHORT).show();
                if (currentLocationsList.size() > 1)
                {
                    int size = currentLocationsList.size();

                    LatLng previous=currentLocationsList.get(size - 2);
                    LatLng current=currentLocationsList.get(size - 1);


                    String prev_Address = getAddress(context, currentLocationsList.get(size - 2));
                    String current_Address = getAddress(context, currentLocationsList.get(size - 1));

                    Log.d("Previous Location", prev_Address);
                    Log.d("Break", "-------------------");
                    Log.d("Current Location", current_Address);

                }
                else {

                }
                EndTime = getTimeOnTransaction();
                min = getMinutes(context, StartTime, EndTime);
                notificationHelper.sendHighPriorityNotification("Exit",
                        "Exit from the selected zone after " + EndTime + ", " + min +" minutes.", MainActivity.class);
                checkCondition(context, min);
                break;
        }
    }

    public void checkCondition(Context context, int myMin) {
        if (myMin < 3) {
            Toast.makeText(context, "No nearby...", Toast.LENGTH_SHORT).show();
            getCurrentLocation(context);

        } else {
            getNearByDetails(context,myLatlng,"@string/google_maps_key");
            Toast.makeText(context, "Nearby Success...", Toast.LENGTH_SHORT).show();
            getCurrentLocation(context);
        }
    }

    public int getMinutes(Context context, String d1, String d2) {
        int myMin = 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+5:00"));
        try {
            Date date1 = simpleDateFormat.parse(d1);
            Date date2 = simpleDateFormat.parse(d2);
            long difference = date2.getTime() - date1.getTime();
            int days = (int) (difference / (1000 * 60 * 60 * 24));
            int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
            int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
            hours = (hours < 0 ? -hours : hours);
            myMin = min;
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("Error ", e.getMessage());
        }
        return myMin;
    }

    private void addGeofence(Context context, double lat, double lon, float radius) {
        LatLng obj = new LatLng(lat, lon);
        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, obj, radius, Geofence.GEOFENCE_TRANSITION_ENTER
                | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofenceRequest = geofenceHelper.getGeofenceRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        geofencingClient.addGeofences(geofenceRequest, pendingIntent).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("MainActivity", "onSuccess..");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorCode(e);
                        Log.d("MainActivity", "onFailure:" + errorMessage);
                    }
                });
    }

    private void getCurrentLocation(Context context) {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(context)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(context)
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int locationindex = locationResult.getLocations().size() - 1;
                            double current_lat = locationResult.getLocations().get(locationindex).getLatitude();
                            double current_long = locationResult.getLocations().get(locationindex).getLongitude();
                            myLatlng = new LatLng(current_lat, current_long);
                            addGeofence(context,myLatlng.latitude,myLatlng.longitude,200);
                            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

                            if (!sharedPrefs.contains("locList"))
                            {
                                Log.d("Dont Exists", "creating");
                                ArrayList<LatLng> latlongList = new ArrayList<>();
                                latlongList.add(myLatlng);
                                saveArrayList(context, latlongList, "locList");
                            }
                            else
                                {
                                Log.d("Exists", "updating");
                                ArrayList<LatLng> latlongList = getArrayList(context, "locList");
                                latlongList.add(myLatlng);
                                saveArrayList(context, latlongList, "locList");
                                }

                            Log.d("Location", String.valueOf(current_lat) + "," + String.valueOf(current_long));
                        }
                    }
                }, Looper.getMainLooper());
    }

    public void saveArrayList(Context context, ArrayList<LatLng> list, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();

    }

    public ArrayList<LatLng> getArrayList(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<LatLng>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public String getAddress(Context context, LatLng latLng) {

        Geocoder geocoder;
        List<Address> addresses = new ArrayList<>();
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            Toast.makeText(context, "loc getting 2", Toast.LENGTH_SHORT).show();
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            String time = formatter.format(date).toString();
            Toast.makeText(context, "Got loc 2", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    public void getNearByDetails(Context context, LatLng latLng, String api_key) {
        String location = latLng.latitude + "," + latLng.longitude;
        //myPlace = getGeocodingDetails(context, latLng.latitude, latLng.longitude);
        nearbyDetails = new ArrayList<>();
        final ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Example> call = apiInterface.getDetails(location, 100, api_key);
        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                if (response.isSuccessful()) {
                    try {
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        Date date = new Date();
                        String time=formatter.format(date).toString();
                        for (int i = 0; i < response.body().getResults().size(); i++) {
                            String placeName = response.body().getResults().get(i).getName();
                            Double lat = response.body().getResults().get(i).getGeometry().getLocation().getLat();
                            Double lng = response.body().getResults().get(i).getGeometry().getLocation().getLng();
                            List<String> placeType = response.body().getResults().get(i).getTypes();
                            nearbyPlace nearby = new nearbyPlace(placeName, lat, lng, placeType, "add");
                            nearbyDetails.add(nearby);
                            saveDataInDatabase(context,"GuzFS0EjtBSwuRXBuRfhFN8ZSfm1",
                                    placeName,
                                    "myPlace.getPlaceAddress()",
                                    lat,lng,
                                    placeType.toString(),"pending",time
                            );
                        }
                        Toast.makeText(context, "Data saved successfully", Toast.LENGTH_SHORT).show();

                    } catch (Exception er) {
                        Log.d("showPLace err ", er.getMessage());

                    }
                    Log.d("showPLace success", response.body().toString());

                } else {
                    Log.d("Else Response: ", response.message());
                }
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                Log.d("Failure: ", t.getMessage());
            }
        });

    }

    private void saveDataInDatabase(Context context,String userId, String placeName, String placeAddress, Double lat, Double lng, String type, String status, String time) {
        Toast.makeText(context, "Entered on Database", Toast.LENGTH_SHORT).show();
        HashMap<String, String> params = new HashMap<>();
        params.put("userId",userId);
        params.put("placeLatitude",String.valueOf(lat));
        params.put("placeLongitude",String.valueOf(lng));
        params.put("placeAddress",placeAddress);
        params.put("placeName",placeName);
        params.put("placeType",type);
        params.put("visitStatus",status);
        params.put("placeTime",time);

        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_CREATE_LIST, params, CODE_POST_REQUEST);
        request.execute();
    }

    private place getGeocodingDetails(Context context, double Latitude, double Longitude) {
        Geocoder geocoder;
        place completeDetails = null;
        List<Address> addresses = new ArrayList<>();
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(Latitude, Longitude, 1);
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            String time = formatter.format(date).toString();

            completeDetails = new place("GuzFS0EjtBSwuRXBuRfhFN8ZSfm1", Latitude, Longitude, address, "pending", time);
            Log.d("LOCATION_DETAILS", completeDetails.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return completeDetails;
    }

    public String getTimeOnTransaction(){
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:00"));
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date.setTimeZone(TimeZone.getTimeZone("GMT+5:00"));
        String localTimeNow = date.format(currentLocalTime);
        return localTimeNow;
    }

    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {

        //the url where we need to send the request
        String url;
        //the parameters
        HashMap<String, String> params;
        //the request code to define whether it is a GET or POST
        int requestCode;

        //constructor to initialize values
        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        //when the task started displaying a progressbar
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        //this method will give the response from the request
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    //refreshing the herolist after every operation
                    //so we get an updated list
                    //we will create this method right now it is commented
                    //because we haven't created it yet
                    // refreshHistoryList(object.getJSONArray("lists"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //the network operation will be performed in background
        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            if (requestCode == CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);

            if (requestCode == CODE_GET_REQUEST)
                return requestHandler.sendGetRequest(url);

            return null;
        }
    }
}
