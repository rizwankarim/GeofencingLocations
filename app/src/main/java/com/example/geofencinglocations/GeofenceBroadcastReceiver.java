package com.example.geofencinglocations;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.sql.Struct;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {


   String StartTime="";
   String EndTime="16:00 PM";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //Toast.makeText(context,"Geofence triggered...",Toast.LENGTH_SHORT).show();

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
                MainActivity.hasExit=false;
                Toast.makeText(context,"Entering on selected zone",Toast.LENGTH_SHORT).show();
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:00"));
                Date currentLocalTime = cal.getTime();
                DateFormat date = new SimpleDateFormat("HH:mm a");
// you can get seconds by adding  "...:ss" to it
                date.setTimeZone(TimeZone.getTimeZone("GMT+5:00"));

                String localTimeNow = date.format(currentLocalTime);
                StartTime=localTimeNow;
                notificationHelper.sendHighPriorityNotification("Entry","Entering on selected zone at "+localTimeNow, MapsActivity.class);
                int hours=getHours(StartTime,EndTime);
                int min=getMinutes(StartTime,EndTime);
                String time=Integer.toString(hours)+":"+Integer.toString(min);
                notificationHelper.sendHighPriorityNotification("Notify : ","Time Is "+time, MapsActivity.class);

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
                int hours2=getHours(StartTime,EndTime);
                int min2=getMinutes(StartTime,EndTime);
                MainActivity.MainHours=hours2;
                MainActivity.MainMinutes=min2;
                MainActivity.hasExit=true;
                String localTimeLater = date2.format(currentLocalTime2);
                notificationHelper.sendHighPriorityNotification("Exit","Exit from the selected zone at "+localTimeLater, MapsActivity.class);
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

}
