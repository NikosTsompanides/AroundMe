package com.nikostsompanidis.aroundme;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;

    private RecyclerView horizontal_branches_recycler_view,horizontal_dinner_recycler_view;
    private ArrayList<String> horizontalList;
    private ArrayList<Venue> breakfastShops= new ArrayList<>();
    private HorizontalAdapter horizontalAdapter;

    List<Address> addresses = null;

    String cityName;
    String stateName;
    String countryName;

    public MainActivity() {
    }

    double latitude=0 ;
    double longitude=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        GPSTracker  gps = new GPSTracker(MainActivity.this);

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

        } else {
            // Can't get location.
            // GPS or network is not enabled.
            // Ask user to enable GPS/network in settings.
            gps.showSettingsAlert();
        }




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        navigation.getMenu().getItem(0).setChecked(true);


        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions(this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },11);
        }

        FetchVenuesTask task = new FetchVenuesTask();
        task.execute();

        horizontal_branches_recycler_view= (RecyclerView) findViewById(R.id.horizontal_recycler_view);
        horizontal_dinner_recycler_view= (RecyclerView) findViewById(R.id.horizontal_dinner_recycler_view);



        horizontalAdapter=new HorizontalAdapter(breakfastShops);

        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        horizontal_branches_recycler_view.setLayoutManager(horizontalLayoutManagaer);

        horizontal_branches_recycler_view.setAdapter(horizontalAdapter);

        LinearLayoutManager horizontalLayoutManagerDinner
                = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);

        horizontal_dinner_recycler_view.setLayoutManager(horizontalLayoutManagerDinner);

        horizontal_dinner_recycler_view.setAdapter(horizontalAdapter);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_user) {
            Intent i = new Intent(getApplicationContext(),LoginActivity.class);
            i.putExtra("cityName",cityName);
            i.putExtra("stateName",stateName);
            i.putExtra("countryName",countryName);
            startActivity(i);

            return true;
        } else if (id == R.id.nav_dashboard) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_rate) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_search:
                    return true;
                case R.id.navigation_map:
                    Intent i = new Intent(getApplicationContext(),MapsActivity.class);
                    i.putExtra("lat",latitude);
                    i.putExtra("lng",longitude);
                    startActivity(i);
                    return true;
                case R.id.navigation_dashboard:
                    Log.w("Message","Dashboard");
                    return true;
            }
            return false;
        }

    };


    public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {

        private List<Venue> horizontalList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView shopTextView,addressTextView,isOpenTextView;
            public RatingBar ratingBar;

            public MyViewHolder(View view) {
                super(view);
                shopTextView = (TextView) view.findViewById(R.id.shopNameTextView);
                addressTextView = (TextView) view.findViewById(R.id.addressTextVie);
                isOpenTextView = (TextView) view.findViewById(R.id.isOpenTextView);
                ratingBar=(RatingBar)view.findViewById(R.id.ratingBar);

            }
        }


        public HorizontalAdapter(List<Venue> horizontalList) {
            this.horizontalList = horizontalList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.horizontal_recycle_item, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.shopTextView.setText(horizontalList.get(position).getName());
            holder.addressTextView.setText(horizontalList.get(position).getAddress());
            boolean isOpen= horizontalList.get(position).isOpen();
            if(isOpen)
                holder.isOpenTextView.setText("Open");
            else
                holder.isOpenTextView.setText("Close");

        }

        @Override
        public int getItemCount() {
            return horizontalList.size();
        }
    }


    public class FetchVenuesTask extends AsyncTask<String, Void, ArrayList<Venue>> {

        @Override
        protected  ArrayList<Venue> doInBackground(String... params) {

            return fetchBreakfastStoresData();
        }


        @Override
        protected void onPostExecute(ArrayList<Venue> venues) {
            if(venues != null){
                horizontalAdapter.clear();
                for(Venue vn : venues){
                    horizontalAdapter.add(vn);
                }
            }
        }


        private ArrayList<Venue> fetchBreakfastStoresData() {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                //MODIFIED FOR CITY OF THESSALONIKI, GREECE
                URL url = new URL("https://api.foursquare.com/v2/venues/explore?v=20161016&ll="+latitude+"%2C%20"+longitude+"&query=breakfast&radius=3000&limit=10&client_id=VG2QOOJOVR1ALCMP5DBG2QDT3G31U3WJELPPZWUAZP21SFZC&client_secret=SIHMHQV5YEKERQWDP3G5UKWY22RDZ1DOQCKW2STQKYAGDLNA");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                }
                forecastJsonStr = buffer.toString();

                breakfastShops=VenueJsonParser.getBreakfastShopsfromJson(forecastJsonStr);

                return breakfastShops;


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

            return breakfastShops;
        }
    }


}
