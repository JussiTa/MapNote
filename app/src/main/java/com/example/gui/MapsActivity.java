package com.example.gui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.gui.test.BuildConfig;
import com.example.gui.test.R;
import com.example.manager.HandleFiles;
import com.example.manager.TimeManager;
import com.example.service.Constants;
import com.example.service.LocationService;
import com.example.service.ServiceReceiver;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    private SupportMapFragment mMap; // Might be null if Google Play services APK is not available.
    public static final String TAG = MapsActivity.class.getSimpleName();
    private double lat;
    private double lon;
    private ArrayList<String> myStringArray;
    private Intent intent;
    private Geocoder geocoder;
    private GoogleMap gmap;
    private BroadcastReceiver receiver = null;
    private static String fileName = null;
    private MediaPlayer mediaPalyer;
    private static final String LOG_TAG = "MapsActivity";
    protected HandleFiles handleFiles;
    private TimeManager timeManager;
    private LocationManager locationManager;
    private IBinder aBinder;
    final int START_STICKY = 1;
    private Location location;
    private LocationService locationService;
    private boolean lBound;
    TextInput dialog = new TextInput();

    public MapsActivity() {
        myStringArray = new ArrayList<String>();
    }


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG)
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        timeManager = new TimeManager();
        handleFiles = new HandleFiles(myStringArray, this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //Iniliasize and start backround service
        ServiceConnection connection= new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                      locationService= ((LocationService.LocalBinder)iBinder).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        Intent notificationIntent = new Intent(this, LocationService.class);
        notificationIntent.putExtra("1","ok");

        startService(notificationIntent);
        bindService(notificationIntent,connection,BIND_AUTO_CREATE);


        final Button button = (Button) findViewById(R.id.button_dialog);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Get new locations to fill list
                showNoticeDialog();
            }
        });

        final Button button2 = (Button) findViewById(R.id.button_list);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Get new locations to fill list
                startListActivity();
            }
        });

        try {
            this.myStringArray = handleFiles.getList();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        geocoder= new Geocoder(this.getBaseContext());
        //handleNewLocation(lat, lon);

    }

    @Override
    protected void onResume() {
        super.onResume();
       //try {
       //     if (locationService.getLocation() != null) {

        //        lat = location.getLatitude();
         //       lon = location.getLongitude();
//                handleNewLocation(lat, lon);
         //   }
       // }catch (NullPointerException e){
         //   Log.e("Null location",e.getMessage());
        //}
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

  /*  public void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
            mMap.getMapAsync(this);
        }

    }
*/
    //Udated location and ointer of the map
    public void handleNewLocation(double lat, double lon) {
         this.lat=lat;
         this.lon=lon;
         LatLng latlon = new LatLng(lat,lon);
         onMapReady(gmap);
        //gmap.addMarker(new MarkerOptions().position(latlon).title("Your position"));
        //gmap.moveCamera(CameraUpdateFactory.newLatLng(latlon));
        Toast toast= Toast.makeText(getApplicationContext(),"Paikka vaihtunut",Toast.LENGTH_LONG);
        toast.show();


        //    langitudelongittudeTometers(myStringArray, location);
    }

    /*  Start listaActivity to show data of note file.
       */
    public void startListActivity() {
        Intent intent = new Intent(this, TaskListActivity.class);

        //For getting back information from listActivity saved on intent.
        startActivityForResult(intent, 1);
    }

    /*
    Start dialog for adding new note.
     */
    public void showNoticeDialog() {
        // Create an instance of the dialog fragment and show it

        dialog.setgeoCoder(geocoder);
        dialog.show(getFragmentManager(), "TextInput");


    }




    //Called from  TextInput after pressed "Add"-button
    public void onDialogPositiveClick(TextInput dialog) throws IOException {
        Intent intent;
        //Text1 sisältää muistilistan tietoa.
        String text1 = dialog.getEditText().getText().toString();
        String text2 = dialog.getTextView().getText().toString();
        StringBuilder buffer = new StringBuilder();
        //If note comes without coordinates them added from loaction
        if (text2.equals("")) {
            text2 = Double.valueOf(lat).toString() + "-" + Double.toString(lon);
        }
        //Replace coordinates comma by .hyphen.
        String temp;
        temp = text2.replace(",", "-");
        text2 = temp;
        //Appended note text and location same string.
        buffer.append(text1 + "-").append(text2).append("-");
        String text3;
        //and  timestamp added
        text3 = timeManager.getCurrentTimeStamp();
        buffer.append(text3);
        String data = buffer.toString();

        myStringArray=handleFiles.updateFile(data);
       /* intent = new Intent(this, LocationService.class);
        intent.putExtra("Paivitetty", "PAIVITETTY");
        startService(intent);*/
    }

    //Called from TextInput if cancelled adding note.
    public void onDialogNegativeClick(TextInput dialog) {
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
     /*   Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.jussi.mapnote/http/host/path")
        );*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                ArrayList<String> myStringArray3 = data.getStringArrayListExtra("jee");
                if (myStringArray3.size() != myStringArray.size() || myStringArray3.isEmpty()) {
                    try {
                       handleFiles.saveModifiedList(myStringArray3);
                        intent = new Intent(this, LocationService.class);
                        intent.putExtra("Paivitetty", "PAIVITETTY");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();  // Always call the superclass
        unregisterReceiver(receiver);
        stopService(intent);
        // Stop method tracing that the activity started during onCreate()
        android.os.Debug.stopMethodTracing();
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        Log.i("MAP","is ready");
        LatLng latlon =new LatLng(lat,lon);
        gmap.addMarker(new MarkerOptions().position(latlon).title("Your position"));
        gmap.moveCamera(CameraUpdateFactory.newLatLng(latlon));
    }

      //Repeat filename of the notes that meets approaching coordinates
    private void startPlay() {
        mediaPalyer = new MediaPlayer();
        try {
            mediaPalyer.setDataSource(fileName);
            mediaPalyer.prepare();

        } catch (IOException e) {

            Log.e(LOG_TAG, "prepare() failed");

        }

        mediaPalyer.start();
    }
    public void stopPlaying() {
        mediaPalyer.release();
        mediaPalyer = null;
    }


    private void initializeReceiver() {
        //Get to receive information from service
        receiver= new ServiceReceiver(this) ;
        registerReceiver(receiver, new IntentFilter(Constants.NOTIFICATION));

    }


    public void setTimeManager(TimeManager timeManager) {
        this.timeManager = timeManager;
    }

    public IBinder getBinder(){
      return aBinder;

    }

    private void updateLoxationonMap(Location location){

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
        gmap.clear();
        gmap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        gmap.moveCamera(CameraUpdateFactory.newLatLng(sydney));    }


    public void positiveBuotton() {
        String text1 = dialog.getEditText().getText().toString();
        String text2 = dialog.getTextView().getText().toString();
        Log.d("show",  text1);

    }
}





