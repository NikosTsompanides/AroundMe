package com.nikostsompanidis.aroundme;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.media.Image;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
import java.util.jar.Attributes;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<Venue>places= new ArrayList<>();

    double latitude=0 ;
    double longitude=0;
    private FirebaseDatabase mFireBaseDatabase;
    private DatabaseReference mDatabaseReference;

    private FirebaseAuth mFireBaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser user;

    private RatingBar ratingBar;
    private TextView name, Address, Phone, Distance;
    private ImageView imgView;

    private int rating, chekInsCount, distance;
    private String Name, address, phone, image;
    private double lati, lngi;
    private boolean isOpen;
    private String venueId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and
        // get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFireBaseDatabase=FirebaseDatabase.getInstance("https://aroundme-11c29.firebaseio.com/");
        mDatabaseReference=mFireBaseDatabase.getReference().child("users");
        mFireBaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
            }
        };

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().getItem(1).setChecked(true);


        SharedPreferences prefs = getSharedPreferences(InitialFullscreenActivity.MY_PREFS_NAME, MODE_PRIVATE);
        latitude=prefs.getFloat("lat",0);
        longitude=prefs.getFloat("lng",0);


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
            mMap.addMarker(new MarkerOptions().position(new LatLng(vn.getLat(),vn.getLng()))).setTag(vn);

            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                // Use default InfoWindow frame
                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                // Defines the contents of the InfoWindow
                @Override
                public View getInfoContents(Marker arg0) {
                    Venue currentVenue = (Venue) arg0.getTag();

                    // Getting view from the layout file info_window_layout
                    View view = getLayoutInflater().inflate(R.layout.info_window, null);
                    ratingBar=view.findViewById(R.id.ratingBar2);
                    Address = (TextView)  view.findViewById(R.id.addressTextView);
                    Phone = (TextView)  view.findViewById(R.id.phoneTextView);
                    name=view.findViewById(R.id.nameTextView);
                    if(currentVenue!=null){

                        name.setText(currentVenue.getName());
                        ratingBar.setRating(currentVenue.getRating());
                        Address.setText(""+currentVenue.getAddress());
                        if(currentVenue.getPhone().isEmpty()){
                            Phone.setText("Oups! The phone number is Missing :(");
                            Phone.setTextSize(8);
                        }
                        else
                            Phone.setText(""+currentVenue.getPhone());

                    }

                    return view;

                }
            });

        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12.0f));
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Venue currentVenue = (Venue) marker.getTag();
                address = currentVenue.getAddress();
                rating =currentVenue.getRating();
                lati =currentVenue.getLat();
                lngi = currentVenue.getLng();
                Name = currentVenue.getName();
                chekInsCount =currentVenue.getChekInsCount();

                isOpen = currentVenue.isOpen();
                phone = currentVenue.getPhone();
                distance =currentVenue.getDistance();
                image =currentVenue.getImage();
                venueId=currentVenue.getVenueId();

                Intent i = new Intent(getBaseContext(), DetailsActivity.class);
                i.putExtra("name", Name);
                i.putExtra("checkInCount", chekInsCount);
                i.putExtra("isOpen", isOpen);
                i.putExtra("rating", rating);
                i.putExtra("address", address);
                i.putExtra("phone", phone);
                i.putExtra("lat", lati);
                i.putExtra("lng", lngi);
                i.putExtra("distance", distance);
                i.putExtra("image", image);
                i.putExtra("id",venueId);
                startActivity(i);
            }
        });

        mMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
            @Override
            public void onInfoWindowLongClick(Marker marker) {
                marker.hideInfoWindow();
            }
        });
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
                    if(user !=null){
                        Intent in = new Intent(MapsActivity.this,UserDashboardActivity.class);
                        in.putExtra("name",user.getDisplayName());
                        in.putExtra("img", String.valueOf(user.getPhotoUrl()));
                        startActivity(in);
                        return true;
                    }else{
                        //Toast.makeText(MainActivity.this,"You must log in to access the user dashboard",Toast.LENGTH_LONG).show();
                        Intent nt = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(nt);
                        return true;
                    }
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

                URL url = new URL("https://api.foursquare.com/v2/venues/explore?v=20171123&ll="+latitude+","+longitude+"&venuePhotos=1&client_id=VG2QOOJOVR1ALCMP5DBG2QDT3G31U3WJELPPZWUAZP21SFZC&client_secret=SIHMHQV5YEKERQWDP3G5UKWY22RDZ1DOQCKW2STQKYAGDLNA");

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
