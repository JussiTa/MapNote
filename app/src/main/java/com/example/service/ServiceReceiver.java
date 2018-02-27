package com.example.service;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.gui.MapsActivity;

/**
 * Created by jussi on 10/3/17.
 */

public class ServiceReceiver extends BroadcastReceiver {

    private Bundle bundle;
    private double lat;
    MapsActivity activity;
    private double lot;
    private String note;
    private NoticeActivity na;


    public ServiceReceiver(){
        super();
    }

    public ServiceReceiver(MapsActivity activity){
        this.activity = activity;

    }



    @Override
    public void onReceive(Context context, Intent intent) {
        bundle =intent.getExtras();

            Boolean uf;
            bundle = intent.getExtras();
            lat = bundle.getDouble("LAT");
            lot= bundle.getDouble("LON");
            uf = bundle.getBoolean("BL");
            note = bundle.getString("NOTE");


            //if information changed start notice
            if(uf)
                na.showNotice(note);
            //updated ma and loaction

            activity.handleNewLocation(lat,lot);

        }




}
