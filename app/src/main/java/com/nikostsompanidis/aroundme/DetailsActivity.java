package com.nikostsompanidis.aroundme;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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
import java.util.concurrent.ExecutionException;

public class DetailsActivity extends AppCompatActivity  {


    private int rating,chekInsCount,distance;
    private String name,address,phone,image;
    private double lat,lng;
    private boolean isOpen;
    private String venueId;

    private ImageView Image ;


    private ArrayList<String> Photos=new ArrayList<>();
    private ArrayList<Tip> Tips=new ArrayList<>();
    private FirebaseDatabase mFireBaseDatabase;
    private DatabaseReference mDatabaseReference;

    private FirebaseAuth mFireBaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser user;

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            name = extras.getString("name");
            address = extras.getString("address");
            phone = extras.getString("phone");
            rating = extras.getInt("rating");
            chekInsCount = extras.getInt("checkInCount");
            lat = extras.getDouble("lat");
            lng = extras.getDouble("lng");
            isOpen = extras.getBoolean("isOpen");
            distance=extras.getInt("distance");
            image=extras.getString("image");
            venueId=extras.getString("id");
        }

        mFireBaseDatabase=FirebaseDatabase.getInstance("https://aroundme-11c29.firebaseio.com/");
        mDatabaseReference=mFireBaseDatabase.getReference().child("users");
        mFireBaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
            }
        };

        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user!=null){
                    mDatabaseReference.child(user.getUid()).child("favoritePlaces").push().setValue(new Venue(venueId,lat,lng,rating,chekInsCount,name,address,isOpen,phone,distance,image));
                    Toast.makeText(DetailsActivity.this,"Just added to your favorite places! You can see your favorite places on user dashboard !!",Toast.LENGTH_LONG).show();
                }else{

                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(DetailsActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(DetailsActivity.this);
                    }
                    builder.setTitle("Log In or Sign Up")
                            .setMessage("You have to log in or sign up to add a place in your favorite places. Do you want to do itn now?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                    i.putExtra("lng",lat);
                                    i.putExtra("lat",lng);
                                    startActivity(i);

                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                }


            }
        });
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().getItem(0).setChecked(false);


        Bundle highlightsBundle = new Bundle();
        Bundle tipsBundle = new Bundle();
        Bundle photosBundle = new Bundle();

        highlightsBundle.putString("address",address);
        highlightsBundle.putString("phone",phone);
        highlightsBundle.putInt("rating",rating);
        highlightsBundle.putInt("checkInCount",chekInsCount);
        highlightsBundle.putInt("distance",distance);
        highlightsBundle.putBoolean("isOpen",isOpen);
        highlightsBundle.putDouble("lat",lat);
        highlightsBundle.putDouble("lng",lng);

        FetchPhotosOfVenueTask photosTask = new FetchPhotosOfVenueTask();
        try {
            Photos = photosTask.execute(venueId).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        FetchTipsOfVenueTask tipsTask = new FetchTipsOfVenueTask();
        try {
            Tips = tipsTask.execute(venueId).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if(Tips.isEmpty())
            Log.i("empty","EMPTY");
        else
            Log.i("full","FULL");
        for(Tip t :Tips)
            Log.i("tipText",t.getText());

        photosBundle.putStringArrayList("photos",Photos);
        tipsBundle.putParcelableArrayList("tips",Tips);


        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(name);
        }


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount(),highlightsBundle,photosBundle,tipsBundle);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        Image = (ImageView)findViewById(R.id.ivParallax) ;
        Glide.with(DetailsActivity.this)
                .load(image)
                .into(Image);
        toolbar.setTitle(name);



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_search:
                    Intent i = new Intent(getApplicationContext(),MainActivity.class);
                    i.putExtra("lat",lat);
                    i.putExtra("lng",lng);
                    startActivity(i);
                    return true;
                case R.id.navigation_map:
                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    intent.putExtra("lat", lat);
                    intent.putExtra("lng", lng);
                    startActivity(intent);
                    return true;
                case R.id.navigation_dashboard:
                    if(user !=null){
                        Intent in = new Intent(DetailsActivity.this,UserDashboardActivity.class);
                        in.putExtra("lat", lat);
                        in.putExtra("lng", lng);
                        in.putExtra("name",user.getDisplayName());
                        in.putExtra("img", String.valueOf(user.getPhotoUrl()));
                        startActivity(in);
                        return true;
                    }else{
                        //Toast.makeText(MainActivity.this,"You must log in to access the user dashboard",Toast.LENGTH_LONG).show();
                        Intent nt = new Intent(getApplicationContext(), LoginActivity.class);
                        nt.putExtra("lng",lat);
                        nt.putExtra("lat",lng);
                        startActivity(nt);
                        return true;
                    }
            }
            return false;
        }

    };

    public class FetchPhotosOfVenueTask extends AsyncTask<String, Void, ArrayList<String>> {

        private ArrayList<String> photosofVenue = new ArrayList<>();


        @Override
        protected ArrayList<String> doInBackground(String... params) {

            return fetchStoresData(venueId);
        }

        protected void onPostExecute(ArrayList<String> photos) {
            if (photos != null) {
                photosofVenue.addAll(photos);
            }
        }

        private ArrayList<String> fetchStoresData(String venueId) {


            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String jsonStr = null;

            try {

                URL url = new URL("https://api.foursquare.com/v2/venues/"+venueId+"/photos?v=20161016&limit=50&client_id=VG2QOOJOVR1ALCMP5DBG2QDT3G31U3WJELPPZWUAZP21SFZC&client_secret=SIHMHQV5YEKERQWDP3G5UKWY22RDZ1DOQCKW2STQKYAGDLNA");

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

                photosofVenue = VenueJsonParser.getPhotosFromJson(jsonStr);
                Log.i("test1",jsonStr);
                return photosofVenue;


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

            return photosofVenue;
        }
    }


    public class FetchTipsOfVenueTask extends AsyncTask<String, Void, ArrayList<Tip>> {

        private ArrayList<Tip> tipsofVenue = new ArrayList<>();


        @Override
        protected ArrayList<Tip> doInBackground(String... params) {

            return fetchStoresData(venueId);
        }

        protected void onPostExecute(ArrayList<Tip> tips) {
            if (tips != null) {
                tipsofVenue.addAll(tips);
            }
        }

        private ArrayList<Tip> fetchStoresData(String venueId) {


            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String jsonStr = null;

            try {

                URL url = new URL("https://api.foursquare.com/v2/venues/"+venueId+"/tips?v=20161016&limit=30&sort=popular&client_id=VG2QOOJOVR1ALCMP5DBG2QDT3G31U3WJELPPZWUAZP21SFZC&client_secret=SIHMHQV5YEKERQWDP3G5UKWY22RDZ1DOQCKW2STQKYAGDLNA");

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

                tipsofVenue = VenueJsonParser.geTipsFromJson(jsonStr);
                Log.i("test",jsonStr);

                for(Tip t : tipsofVenue)
                    Log.i("detailsOfTip",t.getText());
                return tipsofVenue;


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

            return tipsofVenue;
        }
    }



}
