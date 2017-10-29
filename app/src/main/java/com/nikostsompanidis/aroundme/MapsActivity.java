package com.nikostsompanidis.aroundme;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<Venue>places= new ArrayList<>();

    double latitude=0 ;
    double longitude=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and
        // get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().getItem(1).setChecked(true);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            latitude = extras.getDouble("lat");
            longitude = extras.getDouble("lng");
        }else
            Toast.makeText(this,"Can't Find Any Location",Toast.LENGTH_SHORT);


        FetchVenuesTask placesTask = new FetchVenuesTask();
        try {
            places=placesTask.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }



    }


    @Override
    public void onBackPressed() {
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        for(final Venue vn : places){
            mMap.addMarker(new MarkerOptions().position(new LatLng(vn.getLat(),vn.getLng())).title(""+vn.getName()).snippet(""+vn.getAddress())).setDraggable(true);
            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

                @Override
                public void onMarkerDragStart(Marker marker) {
                    // TODO Auto-generated method stub

                    Intent i = new Intent(getBaseContext(),VenueDetailsActivity.class);
                    i.putExtra("name",vn.getName());
                    i.putExtra("checkInCount",vn.getChekInsCount());
                    i.putExtra("isOpen",vn.isOpen());
                    i.putExtra("rating",vn.getRating());
                    i.putExtra("address",vn.getAddress());
                    i.putExtra("phone",vn.getPhone());
                    i.putExtra("lat",vn.getLat());
                    i.putExtra("lng",vn.getLng());
                    i.putExtra("distance",vn.getDistance());
                    i.putExtra("image",vn.getImage());
                    startActivity(i);

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onMarkerDrag(Marker marker) {
                    // TODO Auto-generated method stub

                }
            });
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12.0f));

    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_search:
                    Intent i = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(i);
                    return true;
                case R.id.navigation_map:
                    return true;
                case R.id.navigation_dashboard:
                    Log.w("Message","Dashboard");
                    return true;
            }
            return false;
        }

    };


    public class FetchVenuesTask extends AsyncTask<String, Void,ArrayList<Venue>> {

        private ArrayList<Venue> dataList= new ArrayList<>();

        @Override
        protected  ArrayList<Venue> doInBackground(String... params) {

            return fetchStoresData();
        }


        protected void onPostExecute( ArrayList<Venue> pl) {

        }


        private ArrayList<Venue> fetchStoresData() {


            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String jsonStr = null;

            try {

                URL url = new URL("https://api.foursquare.com/v2/venues/explore?v=20161016&ll="+latitude+","+longitude+"&radius=10000&openNow=1&client_id=VG2QOOJOVR1ALCMP5DBG2QDT3G31U3WJELPPZWUAZP21SFZC&client_secret=SIHMHQV5YEKERQWDP3G5UKWY22RDZ1DOQCKW2STQKYAGDLNA");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                }
                jsonStr = buffer.toString();

                dataList=VenueJsonParser.getDatafromJson(jsonStr);

                return dataList;


            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            return dataList;
        }
    }

}
