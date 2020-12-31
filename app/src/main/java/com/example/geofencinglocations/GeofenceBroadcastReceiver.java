package com.example.geofencinglocations;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

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


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class GeofenceBroadcastReceiver extends BroadcastReceiver {

   String StartTime="";
   String EndTime="";
   public GeofencingClient geofencingClient;
   public  GeofenceHelper geofenceHelper;
   public  String GEOFENCE_ID = "SOME_GEOFENCE_ID";

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
                DateFormat date = new SimpleDateFormat("HH:mm a");
                date.setTimeZone(TimeZone.getTimeZone("GMT+5:00"));
                String localTimeNow = date.format(currentLocalTime);
                StartTime=localTimeNow;
                notificationHelper.sendHighPriorityNotification("Entry","Entering on selected zone at "+localTimeNow, MapsActivity.class);

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
                DateFormat date2 = new SimpleDateFormat("HH:mm a");
                // you can get seconds by adding  "...:ss" to it
                date2.setTimeZone(TimeZone.getTimeZone("GMT+5:00"));
                String localTimeLater = date2.format(currentLocalTime2);
                EndTime= localTimeLater;
                notificationHelper.sendHighPriorityNotification("Exit","Exit from the selected zone at "+localTimeLater, MapsActivity.class);
                int hours=getHours(StartTime,EndTime);
                int min=getMinutes(StartTime,EndTime);
                //getCurrentLocation(context);
                checkCondition(context,min);
                break;
        }
    }
    public int getHours(String d1,String d2)
    {
        int hours=0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
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

    public int getMinutes(String d1,String d2)
    {
        int min=0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
        try {
            Date date1  = simpleDateFormat.parse(d1);
            Date date2 = simpleDateFormat.parse(d2);
            long difference = date2.getTime() - date1.getTime();
            int days = (int) (difference / (1000*60*60*24));
            int hours= (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
            min = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);
            hours = (hours < 0 ? -hours : hours);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return min;
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
                            MapsActivity.mMap.clear();
                            MapsActivity.addCircle(myLatlng,100);
                        }
                    }
                }, Looper.getMainLooper());
    }

    public void checkCondition(Context context,int min){
          if(min < 15){
                    Toast.makeText(context, "No nearby...", Toast.LENGTH_SHORT).show();
                    getCurrentLocation(context);
                    //addGeofence(context,myLatlng,200);
                }
                else{
                    Toast.makeText(context, "Nearby Success...", Toast.LENGTH_SHORT).show();
                    getCurrentLocation(context);
                    //addGeofence(context,myLatlng,50);
                }


    }

}
