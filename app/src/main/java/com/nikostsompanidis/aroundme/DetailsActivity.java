package com.nikostsompanidis.aroundme;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.GoogleMap;


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
    private GoogleMap mMap;

    private ImageView Image ;
    private TabLayout tabLayout;
    private ProgressBar progressBar4;

    private ArrayList<String> Photos=new ArrayList<>();
    private ArrayList<Tip> Tips=new ArrayList<>();

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

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
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        progressBar4=(ProgressBar) findViewById(R.id.progressBar4);
        Glide.with(DetailsActivity.this)
                .load(image)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar4.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar4.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(Image);
        toolbar.setTitle(name);


    }


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
