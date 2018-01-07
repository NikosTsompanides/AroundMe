package com.nikostsompanidis.aroundme;

import android.app.SearchManager;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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
import java.util.concurrent.ExecutionException;

public class SearchActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<Venue> venuesArrayList = new ArrayList<>();
    private SearchAdapter adapter;
    private double latitude,longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            longitude=extras.getDouble("lng");
            latitude=extras.getDouble("lat");
        }

        FetchVenuesTask task = new  FetchVenuesTask();
        try {
            venuesArrayList=task.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        listView=(ListView) findViewById(R.id.listView);


        adapter = new SearchAdapter(SearchActivity.this, R.layout.search_item, venuesArrayList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               // Toast.makeText(SearchActivity.this, (String)parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
                Venue vn = (Venue) parent.getItemAtPosition(position);
                Intent i = new Intent(getBaseContext(), DetailsActivity.class);
                i.putExtra("name", vn.getName());
                i.putExtra("checkInCount", vn.getChekInsCount());
                i.putExtra("isOpen", vn.isOpen());
                i.putExtra("rating", vn.getRating());
                i.putExtra("address", vn.getAddress());
                i.putExtra("phone", vn.getPhone());
                i.putExtra("lat", vn.getLat());
                i.putExtra("lng", vn.getLng());
                i.putExtra("distance", vn.getDistance());
                i.putExtra("image", vn.getImage());
                i.putExtra("id",vn.getVenueId());
                startActivity(i);
            }
        });

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        MenuItem searchMenuItem = menu.findItem( R.id.action_search ); // get my MenuItem with placeholder submenu
        searchMenuItem.expandActionView();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    adapter.filter("");
                    listView.clearTextFilter();
                } else {
                    adapter.filter(newText);
                }
                return true;
            }
        });

        return true;
    }

    public class FetchVenuesTask extends AsyncTask<Void, Void, ArrayList<Venue>> {

        private ArrayList<Venue> dataList = new ArrayList<>();


        @Override
        protected ArrayList<Venue> doInBackground(Void... params) {

            return fetchStoresData();
        }

        public FetchVenuesTask(){
        }

        protected void onPostExecute(ArrayList<Venue> venues) {
           venuesArrayList.addAll(venues);
           Log.i("length",""+venues.size());
           for(Venue vn : venues){
               Log.i("venues",vn.getName());
           }
        }

        private ArrayList<Venue> fetchStoresData() {


            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String jsonStr = null;

            try {


                URL url = new URL("https://api.foursquare.com/v2/venues/explore?v=20171123&near=" + latitude + "," + longitude +"&venuePhotos=1&client_id=VG2QOOJOVR1ALCMP5DBG2QDT3G31U3WJELPPZWUAZP21SFZC&client_secret=SIHMHQV5YEKERQWDP3G5UKWY22RDZ1DOQCKW2STQKYAGDLNA");

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

                dataList = VenueJsonParser.getDatafromJson(jsonStr);

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
