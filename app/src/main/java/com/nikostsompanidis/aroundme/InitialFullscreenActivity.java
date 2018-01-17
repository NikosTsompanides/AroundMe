package com.nikostsompanidis.aroundme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class InitialFullscreenActivity extends AppCompatActivity {

    String cityName;
    String stateName;
    String countryName;

    Double latitude = 0.0;
    Double longitude = 0.0;

    List<Address> addresses = new ArrayList<Address>();
    private boolean hasInternetAccess;

    static final String MY_PREFS_NAME = "latlng";
    SharedPreferences.Editor editor;

    private LocationManager locationManager;
    private Location location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: Change this fucking stupid but working practise
        GPSTracker tracker = new GPSTracker(this);
        if (!tracker.canGetLocation()) {
            tracker.showSettingsAlert();
        } else {
            if(!hasInternetAccess()){
                Toast.makeText(this,"there is no internet access",Toast.LENGTH_LONG).show();
                do{
                    hasInternetAccess= hasInternetAccess();
                }while(!hasInternetAccess);
            }
            startMainActivity(tracker);
        }

    }

    public boolean hasInternetAccess(){
        NetworkInfo netInfo;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm!=null){
             netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }
        else return false;
    }

    public void startMainActivity(GPSTracker tracker){
        latitude=tracker.getLatitude();
        longitude=tracker.getLongitude();

        if(!latitude.isNaN() && !longitude.isNaN()){

            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.putFloat("lat", Float.parseFloat(""+latitude));
            editor.putFloat("lng", Float.parseFloat(""+longitude));
            editor.apply();

            Intent i = new Intent(InitialFullscreenActivity.this,MainActivity.class);
            startActivity(i);
        }
    }
}
