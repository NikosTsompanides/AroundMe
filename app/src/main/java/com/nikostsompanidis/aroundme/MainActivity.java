package com.nikostsompanidis.aroundme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
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
import android.view.Window;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private RecyclerView horizontal_food_recycler_view, horizontal_coffee_recycler_view, horizontal_drinks_recycler_view, horizontal_arts_recycler_view, horizontal_outdoors_recycler_view, horizontal_top_picks_recycler_view;
    private ArrayList<Venue> foodShops = new ArrayList<>();
    private ArrayList<Venue> coffeeShops = new ArrayList<>();
    private ArrayList<Venue> bars = new ArrayList<>();
    private ArrayList<Venue> arts = new ArrayList<>();
    private ArrayList<Venue> topPicks = new ArrayList<>();
    private ArrayList<Venue> outdoors = new ArrayList<>();
    private HorizontalAdapter foodAdapter, coffeeAdapter, drinksAdapter, artsAdapetr, topPicksAdapter, outdoorsAdapter;
    private int rating, chekInsCount, distance;
    private String name, address, phone, image;
    private double lati, lngi;
    private boolean isOpen;
    private String venueId;

    private double longitude, latitude;
    private ProgressBar progressBar2;
    private FirebaseDatabase mFireBaseDatabase;
    private DatabaseReference mDatabaseReference;

    private FirebaseAuth mFireBaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser user;
    private TextView topPicksTextView,foodTextView,coffeeTextView,barsTextView,artsTextView,outdoorsTextView;
    private ImageView usrImageView;
    private Button button;
    private CardView search;
    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        topPicksTextView=findViewById(R.id.topPicksTextView);
        foodTextView=findViewById(R.id.BreakfastTextView);
        coffeeTextView=findViewById(R.id.dinnerTextView);
        barsTextView=findViewById(R.id.barsTextView);
        artsTextView=findViewById(R.id.artsTextView);
        outdoorsTextView=findViewById(R.id.outdoorsTextView);
        usrImageView=findViewById(R.id.usrImageView);
        search=findViewById(R.id.searchCardView);
        button= findViewById(R.id.searchButton);


        mFireBaseDatabase=FirebaseDatabase.getInstance("https://aroundme-11c29.firebaseio.com/");
        mDatabaseReference=mFireBaseDatabase.getReference().child("users");
        mFireBaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
            }
        };
        mFireBaseAuth.addAuthStateListener(mAuthStateListener);


        if(user!=null)
            Glide.with(this)
                .load(user.getPhotoUrl())
                .into(usrImageView);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            latitude = extras.getDouble("lat");
            longitude = extras.getDouble("lng");
        } else
            Toast.makeText(this, "Can't Find Any Location", Toast.LENGTH_SHORT).show();


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


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 11);
        }


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,SearchActivity.class);
                i.putExtra("lng",longitude);
                i.putExtra("lat",latitude);
                startActivity(i);
            }
        });

        foodShops = getVenues("food");
        coffeeShops = getVenues("coffee");
        bars = getVenues("drinks");
        arts = getVenues("arts");
        topPicks = getVenues("topPicks");
        outdoors = getVenues("outdoors");

        if(!topPicks.isEmpty()){
            topPicksTextView.setVisibility(View.VISIBLE);
            horizontal_top_picks_recycler_view = (RecyclerView) findViewById(R.id.horizontal_top_picks_recycler_view);
            horizontal_top_picks_recycler_view.addOnItemTouchListener(new RecyclerItemClickListener(getBaseContext(), horizontal_top_picks_recycler_view, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    goToVenueDetailsActivity(position, topPicks);
                }

                @Override
                public void onLongItemClick(View view, int position) {
                    // do whatever
                }
            }));
            topPicksAdapter = new HorizontalAdapter(topPicks);
            layoutManager(horizontal_top_picks_recycler_view, topPicksAdapter);

        }

        if(!foodShops.isEmpty()){
            foodTextView.setVisibility(View.VISIBLE);
            horizontal_food_recycler_view = (RecyclerView) findViewById(R.id.horizontal_food_recycler_view);
            horizontal_food_recycler_view.addOnItemTouchListener(new RecyclerItemClickListener(getBaseContext(), horizontal_food_recycler_view, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    goToVenueDetailsActivity(position, foodShops);
                }

                @Override
                public void onLongItemClick(View view, int position) {
                    // do whatever
                }
            }));

            foodAdapter = new HorizontalAdapter(foodShops);
            layoutManager(horizontal_food_recycler_view, foodAdapter);

        }


        if(!coffeeShops.isEmpty()){
            coffeeTextView.setVisibility(View.VISIBLE);
            horizontal_coffee_recycler_view = (RecyclerView) findViewById(R.id.horizontal_coffee_recycler_view);
            horizontal_coffee_recycler_view.addOnItemTouchListener(new RecyclerItemClickListener(getBaseContext(), horizontal_coffee_recycler_view, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    goToVenueDetailsActivity(position, coffeeShops);
                }

                @Override
                public void onLongItemClick(View view, int position) {
                    // do whatever
                }
            }));
            coffeeAdapter = new HorizontalAdapter(coffeeShops);
            layoutManager(horizontal_coffee_recycler_view, coffeeAdapter);

        }


        if(!bars.isEmpty()){
            barsTextView.setVisibility(View.VISIBLE);
            horizontal_drinks_recycler_view = (RecyclerView) findViewById(R.id.horizontal_drinks_recycler_view);
            horizontal_drinks_recycler_view.addOnItemTouchListener(new RecyclerItemClickListener(getBaseContext(), horizontal_drinks_recycler_view, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                    goToVenueDetailsActivity(position, bars);
                }

                @Override
                public void onLongItemClick(View view, int position) {
                    // do whatever
                }
            }));
            drinksAdapter = new HorizontalAdapter(bars);
            layoutManager(horizontal_drinks_recycler_view, drinksAdapter);

        }


        if(!arts.isEmpty()){
            artsTextView.setVisibility(View.VISIBLE);
            horizontal_arts_recycler_view = (RecyclerView) findViewById(R.id.horizontal_arts_recycler_view);
            horizontal_arts_recycler_view.addOnItemTouchListener(new RecyclerItemClickListener(getBaseContext(), horizontal_arts_recycler_view, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                    goToVenueDetailsActivity(position, arts);

                }

                @Override
                public void onLongItemClick(View view, int position) {
                    // do whatever
                }
            }));
            artsAdapetr = new HorizontalAdapter(arts);
            layoutManager(horizontal_arts_recycler_view, artsAdapetr);

        }


        if(!outdoors.isEmpty()){
            outdoorsTextView.setVisibility(View.VISIBLE);
            horizontal_outdoors_recycler_view = (RecyclerView) findViewById(R.id.horizontal_outdoors_recycler_view);
            horizontal_outdoors_recycler_view.addOnItemTouchListener(new RecyclerItemClickListener(getBaseContext(), horizontal_outdoors_recycler_view, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    goToVenueDetailsActivity(position, outdoors);
                }

                @Override
                public void onLongItemClick(View view, int position) {
                    // do whatever
                }
            }));
            outdoorsAdapter = new HorizontalAdapter(outdoors);
            layoutManager(horizontal_outdoors_recycler_view, outdoorsAdapter);

        }





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
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            i.putExtra("lng",longitude);
            i.putExtra("lat",latitude);
            startActivity(i);

            return true;
        } else if (id == R.id.nav_dashboard) {

            if(user !=null){
                Intent in = new Intent(MainActivity.this,UserDashboardActivity.class);
                in.putExtra("lat", latitude);
                in.putExtra("lng", longitude);
                in.putExtra("name",user.getDisplayName());
                in.putExtra("img", String.valueOf(user.getPhotoUrl()));
                startActivity(in);
                return true;
            }else{
                //Toast.makeText(MainActivity.this,"You must log in to access the user dashboard",Toast.LENGTH_LONG).show();
                Intent nt = new Intent(getApplicationContext(), LoginActivity.class);
                nt.putExtra("lng",longitude);
                nt.putExtra("lat",latitude);
                startActivity(nt);
                return true;
            }
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
                    Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                    i.putExtra("lat", latitude);
                    i.putExtra("lng", longitude);
                    startActivity(i);
                    return true;
                case R.id.navigation_dashboard:
                   if(user !=null){
                       Intent in = new Intent(MainActivity.this,UserDashboardActivity.class);
                       in.putExtra("lat", latitude);
                       in.putExtra("lng", longitude);
                       in.putExtra("name",user.getDisplayName());
                       in.putExtra("img", String.valueOf(user.getPhotoUrl()));
                       startActivity(in);
                       return true;
                   }else{
                       //Toast.makeText(MainActivity.this,"You must log in to access the user dashboard",Toast.LENGTH_LONG).show();
                       Intent nt = new Intent(getApplicationContext(), LoginActivity.class);
                       nt.putExtra("lng",longitude);
                       nt.putExtra("lat",latitude);
                       startActivity(nt);
                       return true;
                   }

            }
            return false;
        }

    };


    public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {
        private List<Venue> horizontalList;

        private View mView;


        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView shopTextView, addressTextView, isOpenTextView;
            public RatingBar ratingBar;
            public ImageView image;

            public MyViewHolder(View view) {
                super(view);
                mView = view;
                shopTextView = (TextView) mView.findViewById(R.id.shopNameTextView);
                addressTextView = (TextView) mView.findViewById(R.id.addressTextVie);
                isOpenTextView = (TextView) mView.findViewById(R.id.isOpenTextView);
                ratingBar = (RatingBar) mView.findViewById(R.id.ratingBar);
                image = (ImageView) mView.findViewById(R.id.itemImageView);
                progressBar2 = (ProgressBar) mView.findViewById(R.id.progressBar2);

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
            Glide.with(MainActivity.this)
                    .load(horizontalList.get(position).getImage())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            progressBar2.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBar2.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(holder.image);
            holder.addressTextView.setText(horizontalList.get(position).getAddress());
            boolean isOpen = horizontalList.get(position).isOpen();
            if (isOpen)
                holder.isOpenTextView.setText("Open");
            else
                holder.isOpenTextView.setText("Close");
            holder.ratingBar.setRating(horizontalList.get(position).getRating());

        }

        @Override
        public int getItemCount() {
            return horizontalList.size();
        }


        public void clear() {
            // TODO Auto-generated method stub
            horizontalList.clear();

        }

        private void addItem(Venue venue) {
            horizontalList.add(venue);
            this.notifyDataSetChanged();
        }


    }

    public class FetchVenuesTask extends AsyncTask<String, Void, ArrayList<Venue>> {

        private ArrayList<Venue> dataList = new ArrayList<>();
        private HorizontalAdapter adapter = new HorizontalAdapter(dataList);
        private String section;

        @Override
        protected ArrayList<Venue> doInBackground(String... params) {

            return fetchStoresData(this.section);
        }

        public FetchVenuesTask(String section) {
            this.section = section;
        }

        protected void onPostExecute(ArrayList<Venue> venues) {
            if (venues != null) {
                adapter.clear();

                for (Venue vn : venues)
                    adapter.addItem(vn);

            }
        }

        private ArrayList<Venue> fetchStoresData(String section) {


            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String jsonStr = null;

            try {

                URL url = new URL("https://api.foursquare.com/v2/venues/explore?v=20171123&ll=" + latitude + "," + longitude + "&section=" + section + "&radius=2000&limit=10&venuePhotos=1&client_id=VG2QOOJOVR1ALCMP5DBG2QDT3G31U3WJELPPZWUAZP21SFZC&client_secret=SIHMHQV5YEKERQWDP3G5UKWY22RDZ1DOQCKW2STQKYAGDLNA");

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

    public void goToVenueDetailsActivity(int position, ArrayList<Venue> list) {
        address = list.get(position).getAddress();
        rating = list.get(position).getRating();
        lati = list.get(position).getLat();
        lngi = list.get(position).getLng();
        name = list.get(position).getName();
        chekInsCount = list.get(position).getChekInsCount();

        isOpen = list.get(position).isOpen();
        phone = list.get(position).getPhone();
        distance = list.get(position).getDistance();
        image = list.get(position).getImage();
        venueId=list.get(position).getVenueId();

        Intent i = new Intent(getBaseContext(), DetailsActivity.class);
        i.putExtra("name", name);
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

    public void layoutManager(RecyclerView recyclerView, HorizontalAdapter adapter) {

        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManagaer);
        recyclerView.setAdapter(adapter);

    }

    public ArrayList<Venue> getVenues(String section) {
        ArrayList<Venue> list = new ArrayList<>();
        FetchVenuesTask task = new FetchVenuesTask(section);
        try {
            list = task.execute().get();
            return list;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return list;
    }


}


