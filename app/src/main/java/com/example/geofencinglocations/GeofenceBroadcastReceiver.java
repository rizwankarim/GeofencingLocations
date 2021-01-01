package com.example.geofencinglocations;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.geofencinglocations.api.Api;
import com.example.geofencinglocations.api.RequestHandler;
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


import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;


public class GeofenceBroadcastReceiver extends BroadcastReceiver {

   public static String StartTime="";
   String EndTime="";
   public GeofencingClient geofencingClient;
   public  GeofenceHelper geofenceHelper;
   public  String GEOFENCE_ID = "SOME_GEOFENCE_ID";
   int min;
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //Toast.makeText(context,"Geofence triggered...",Toast.LENGTH_SHORT).show();

        geofencingClient = LocationServices.getGeofencingClient(context);
        geofenceHelper = new GeofenceHelper(context);

        NotificationHelper notificationHelper= new NotificationHelper(context);

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if(geofencingEvent.hasError()){
            Log.d("GeofenceBroadcastReceiv",":onReceive error receiving geofencing event..");
        }

        List<Geofence>geofenceList= geofencingEvent.getTriggeringGeofences();
        for (Geofence geofence : geofenceList){
            Log.d("GeofenceBroadcastReceiv",":onReceive:"+ geofence.getRequestId());
        }
        int transitionType= geofencingEvent.getGeofenceTransition();

        switch (transitionType){
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Toast.makeText(context,"Entering on selected zone",Toast.LENGTH_SHORT).show();
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:00"));
                Date currentLocalTime = cal.getTime();
                DateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                date.setTimeZone(TimeZone.getTimeZone("GMT+5:00"));
                String localTimeNow = date.format(currentLocalTime);
                StartTime=localTimeNow;
                notificationHelper.sendHighPriorityNotification("Entry","Entering on selected zone at "+StartTime, MapsActivity.class);

                //String time=Integer.toString(hours)+":"+Integer.toString(min);
                //notificationHelper.sendHighPriorityNotification("Notify : ","Time Is "+time, MapsActivity.class);
                break;

            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Toast.makeText(context,"In the selected zone",Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("Dwell","In the selected zone", MapsActivity.class);
                break;

            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Toast.makeText(context,"Exit from the selected zone",Toast.LENGTH_SHORT).show();
                Calendar cal2 = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:00"));
                Date currentLocalTime2 = cal2.getTime();
                DateFormat date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                date2.setTimeZone(TimeZone.getTimeZone("GMT+5:00"));
                String localTimeLater = date2.format(currentLocalTime2);
                EndTime= localTimeLater;
                notificationHelper.sendHighPriorityNotification("Exit","Exit from the selected zone at "+localTimeLater, MapsActivity.class);
                int hours=getHours(StartTime,EndTime);

                Toast.makeText(context, StartTime, Toast.LENGTH_SHORT).show();
                min= getMinutes(context,StartTime,EndTime);
                //getCurrentLocation(context);
                checkCondition(context,min);
                break;
        }
    }
    public int getHours(String d1,String d2)
    {
        int hours=0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date1  = simpleDateFormat.parse(d1);
            Date date2 = simpleDateFormat.parse(d2);
            long difference = date2.getTime() - date1.getTime();
            int days = (int) (difference / (1000*60*60*24));
            hours= (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
            int min = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);
            hours = (hours < 0 ? -hours : hours);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return hours;
    }

    public int getMinutes(Context context,String d1,String d2)
    {
        int myMin=0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+5:00"));
        try {
            Date date1  = simpleDateFormat.parse(d1);
            Date date2 = simpleDateFormat.parse(d2);
            long difference = date2.getTime() - date1.getTime();
            int days = (int) (difference / (1000*60*60*24));
            int hours= (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
            int min = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);
            hours = (hours < 0 ? -hours : hours);
            myMin=min;
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("Error ",e.getMessage());
        }
        return myMin;
    }

    private void addGeofence(Context context,LatLng latLng, float radius) {
        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER
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
                        Log.d("MainActivity","onSuccess..");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage= geofenceHelper.getErrorCode(e);
                        Log.d("MainActivity","onFailure:" + errorMessage);
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
                            double current_lat=locationResult.getLocations().get(locationindex).getLatitude();
                            double current_long=locationResult.getLocations().get(locationindex).getLongitude();
                            LatLng myLatlng= new LatLng(current_lat,current_long);
                            Log.d("Location", String.valueOf(current_lat) + "," + String.valueOf(current_long));
                            addGeofence(context,myLatlng,100);
                             //   MapsActivity.mMap.clear();
                            //  MapsActivity.addCircle(myLatlng,100);
                        }
                    }
                }, Looper.getMainLooper());
    }

    public void checkCondition(Context context,int myMin){
        Toast.makeText(context, "Min : "+Integer.toString(myMin), Toast.LENGTH_SHORT).show();
          if(myMin < 1){
                    Toast.makeText(context, "No nearby...", Toast.LENGTH_SHORT).show();
                    getCurrentLocation(context);
                    //addGeofence(context,myLatlng,200);
                }
                else{
                    Toast.makeText(context, "Nearby Success...", Toast.LENGTH_SHORT).show();
                    getCurrentLocation(context);
                    //addGeofence(context,myLatlng,50);
              showNearby("GuzFS0EjtBSwuRXBuRfhFN8ZSfm1");
                }


    }

    private void showNearby(String userId) {
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_READ_LIST+userId, null, CODE_GET_REQUEST);
        request.execute();
    }

    private class PerformNetworkRequest  extends AsyncTask<Void, Void, String> {

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
