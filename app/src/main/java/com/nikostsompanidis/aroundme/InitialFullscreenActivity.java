package com.nikostsompanidis.aroundme;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.saeid.fabloading.LoadingView;


public class InitialFullscreenActivity extends AppCompatActivity {

    String cityName;
    String stateName;
    String countryName;

    Double latitude=0.0 ;
    Double longitude=0.0;

    List<Address> addresses = new ArrayList<Address>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if( netInfo != null && netInfo.isConnectedOrConnecting()){
            // loadingView.startAnimation();
            GPSTracker  gps = new GPSTracker(InitialFullscreenActivity.this);

            // Check if GPS enabled
            if(gps.canGetLocation()) {

                latitude = gps.getLatitude();
                longitude = gps.getLongitude();

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(!addresses.isEmpty()) {
                    cityName = addresses.get(0).getAddressLine(0);
                    stateName = addresses.get(0).getAddressLine(1);
                    countryName = addresses.get(0).getAddressLine(2);
                }

                if(!latitude.isNaN() && !longitude.isNaN()){

                    Intent i = new Intent(InitialFullscreenActivity.this,MainActivity.class);
                    i.putExtra("lat",latitude);
                    i.putExtra("lng",longitude);
                    startActivity(i);

                }

            } else {
                // Can't get location.
                // GPS or network is not enabled.
                // Ask user to enable GPS/network in settings.
                gps.showSettingsAlert();
            }

        }else{
            Toast.makeText(this,"There is no internet connection !!! ",Toast.LENGTH_LONG).show();
        }

    }
}
