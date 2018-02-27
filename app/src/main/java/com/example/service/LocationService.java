package com.example.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.IntentService;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.example.gui.MapsActivity;
import com.example.manager.HandleFiles;
import com.example.manager.TimeManager;
import com.google.android.gms.*;


import java.security.Provider;
import java.util.ArrayList;

public class LocationService extends Service implements LocationListener{

    private ArrayList<String> myStringArray = new ArrayList<>();
    private TimeManager timeManager = new TimeManager();
    private LocationManager locationManager;
    private Location location;
    private IBinder mBinder;
    private MapsActivity mapsActivity;
    private final LocalBinder lBinder = new LocalBinder();
    private String provider;
    private String TAG = "OnLocationCahnged";
    private LocationListener locationListener;


    @Override
    public void onCreate() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LocationManager.class);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 1000, 0, this);
    }


    @SuppressLint("MissingPermission")
    @Override
    public IBinder onBind(Intent intent) {
        return lBinder;
        //return mapsActivity.getBinder();
    }


    private void setLocation(Location location) {
        this.location = location;
    }


    public Location getLocation() {
        return this.location;
    }


    private void sendBroadcast() {
        Log.d("LOCATION", "New location");
        float distance;
        boolean alarmDistance = false;
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        boolean time = false;
        String data2 = null;
        for (String data : myStringArray) {
            data2 = data;

            time = timeManager.compareTime(data);
        }
        if (time) {
            distance = location.distanceTo(location);
            if (distance < 500)
                alarmDistance = true;

        }

        Intent localIntent =
                new Intent(Constants.NOTIFICATION);
        // Puts the status into the Intent
        localIntent.putExtra("LAT", lat);
        localIntent.putExtra("LON", lon);
        localIntent.putExtra("BL", alarmDistance);
        localIntent.putExtra("NOTE", data2);
        // Broadcasts the Intent to receivers in this app.
        LocalBroadcastManager.getInstance(LocationService.this).sendBroadcast(localIntent);


    }

    @Override
    public void onLocationChanged(Location location) {
        String TAG ="onLocation";

        Log.d(TAG,"Location changed");

        setLocation(location);
        sendBroadcast();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    public class LocalBinder extends Binder{


       public LocationService getService(){

            return LocationService.this;

        }

    }



    private class MyLocationListener implements LocationListener{


        @Override
        public void onLocationChanged(Location location) {
            Toast toast = Toast.makeText(getApplicationContext(),"Location changed",Toast.LENGTH_LONG);

            String TAG ="onLocation";

            Log.d(TAG,"Location changed");

            setLocation(location);
            sendBroadcast();


        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }






}




