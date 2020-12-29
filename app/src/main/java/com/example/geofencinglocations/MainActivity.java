package com.example.geofencinglocations;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import im.delight.android.location.SimpleLocation;

public class MainActivity extends AppCompatActivity {
    public  GeofencingClient geofencingClient;
    public  GeofenceHelper geofenceHelper;
    public  String GEOFENCE_ID = "SOME_GEOFENCE_ID";
    private SimpleLocation location;
    public static int MainHours=0;
    public static int MainMinutes=0;
    public static boolean hasExit=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button on= findViewById(R.id.on);
        // construct a new instance of SimpleLocation
        location = new SimpleLocation(this);

        // if we can't access the location yet
        if (!location.hasLocationEnabled()) {
            // ask the user to enable location access
            SimpleLocation.openSettings(this);
        }
        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);
        final double latitude = location.getLatitude();
        final double longitude = location.getLongitude();
        LatLng myLatlng= new LatLng(latitude,longitude);
        on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGeofence(myLatlng,200);
                Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }
        });
        if(hasExit==true)
        {
            Toast.makeText(MainActivity.this, "Has Exited...", Toast.LENGTH_SHORT).show();
            geofencingClient = LocationServices.getGeofencingClient(this);
            geofenceHelper = new GeofenceHelper(this);
            final double latitude2 = location.getLatitude();
            final double longitude2 = location.getLongitude();
            LatLng myLatlng2= new LatLng(latitude,longitude);
            addGeofence(myLatlng2,200);
        }
        else
            {
                Toast.makeText(MainActivity.this, "Else Part...", Toast.LENGTH_SHORT).show();
            }
    }


    private void addGeofence(LatLng latLng, float radius) {
        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER
                | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofenceRequest = geofenceHelper.getGeofenceRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

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

    @Override
    protected void onResume() {
        super.onResume();
        location.beginUpdates();
    }

    @Override
    protected void onPause() {
        location.endUpdates();
        super.onPause();
    }
}