package com.nikostsompanidis.aroundme;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Comment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDashboardActivity extends AppCompatActivity {

    double latitude = 0;
    double longitude = 0;
    private String name, photoUrl;
    private ImageView userImage;
    private RecyclerView favoritePlacesRecyclerView;
    private PlacesAdapter mAdapter;
    private static final int RC_PHOTO_PICKER = 2;
    private FirebaseStorage mUserPhotosFirebaseStorage;
    private StorageReference mUserPhotosStorageReference;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private FirebaseAuth mFireBaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private ArrayList<Venue> places = new ArrayList<>();
    private  FirebaseUser currentUser;

    private TextView noPlaces;

    private int rating, chekInsCount, distance;
    private String Name, address, phone, image;
    private double lati, lngi;
    private boolean isOpen;
    private String venueId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarUser);
        setSupportActionBar(toolbar);
        Bundle extras = getIntent().getExtras();
        mFireBaseAuth=FirebaseAuth.getInstance();
        currentUser = mFireBaseAuth.getCurrentUser();


        mFirebaseDatabase = FirebaseDatabase.getInstance("https://aroundme-11c29.firebaseio.com/");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthUI.getInstance().signOut(UserDashboardActivity.this);
                Toast.makeText(UserDashboardActivity.this, "You just signed out", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(UserDashboardActivity.this, MainActivity.class);
                i.putExtra("lat", latitude);
                i.putExtra("lng", longitude);
                startActivity(i);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        if (currentUser != null) {
            mDatabaseReference = mFirebaseDatabase.getReference().child("users").child(""+currentUser.getUid()).child("favoritePlaces");
            Log.i("database",""+mDatabaseReference.toString());


            ValueEventListener venueListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get Post object and use the values to update the UI
                    for(DataSnapshot placesSnapshot:dataSnapshot.getChildren()){
                        Venue venue = placesSnapshot.getValue(Venue.class);
                        places.add(venue);
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w("", "loadPost:onCancelled", databaseError.toException());
                    // ...
                }
            };
            mDatabaseReference.addValueEventListener(venueListener);
        }


        noPlaces = findViewById(R.id.noPlacesTextView);
        userImage = findViewById(R.id.userImageView);
        favoritePlacesRecyclerView = (RecyclerView) findViewById(R.id.favoritePlacesRecyclerView);



        mAdapter = new PlacesAdapter(places);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        favoritePlacesRecyclerView.setLayoutManager(mLayoutManager);
        favoritePlacesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        favoritePlacesRecyclerView.setAdapter(mAdapter);
        favoritePlacesRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,favoritePlacesRecyclerView, new RecyclerItemClickListener.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {
                address = places.get(position).getAddress();
                rating = places.get(position).getRating();
                lati = places.get(position).getLat();
                lngi = places.get(position).getLng();
                Name = places.get(position).getName();
                chekInsCount = places.get(position).getChekInsCount();

                isOpen = places.get(position).isOpen();
                phone = places.get(position).getPhone();
                distance = places.get(position).getDistance();
                image = places.get(position).getImage();
                venueId=places.get(position).getVenueId();

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

            @Override
            public void onLongItemClick(View view, int position) {
                // do whatever
            }
        }));


        mUserPhotosFirebaseStorage = FirebaseStorage.getInstance();
        mUserPhotosStorageReference = mUserPhotosFirebaseStorage.getReference().child("user_photos");
        if (extras != null) {
            latitude = extras.getDouble("lat");
            longitude = extras.getDouble("lng");
            if (extras.getString("name") != null && extras.getString("img") != null) {
                name = extras.getString("name");
                photoUrl = extras.getString("img");
                Log.i("name", name);
            }

            Glide.with(UserDashboardActivity.this)
                    .load(photoUrl)
                    .into(userImage);
            getSupportActionBar().setTitle(name);
        }



        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation3);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        navigation.getMenu().getItem(2).setChecked(true);


    }

    @Override
    public void onBackPressed() {

    }

    protected BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_search:
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.putExtra("lat", latitude);
                    i.putExtra("lng", longitude);
                    startActivity(i);
                    return true;
                case R.id.navigation_map:
                    Intent in = new Intent(getApplicationContext(), MapsActivity.class);
                    in.putExtra("lat", latitude);
                    in.putExtra("lng", longitude);
                    startActivity(in);
                    return true;
                case R.id.navigation_dashboard:
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            StorageReference photoRef = mUserPhotosStorageReference.child(selectedImage.getLastPathSegment());
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpg")
                    .setCustomMetadata("user_id", currentUser.getUid())
                    .build();
            photoRef.putFile(selectedImage).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    Glide.with(UserDashboardActivity.this)
                            .load(downloadUri)
                            .into(userImage);
                }
            });
            photoRef.updateMetadata(metadata);
            Toast.makeText(UserDashboardActivity.this, "Image has change succesfully !", Toast.LENGTH_SHORT).show();
        }
    }

    public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.MyViewHolder> {

        private List<Venue> list;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            public ImageView placeImage;
            public TextView name;
            public RatingBar rating;


            public MyViewHolder(View view) {
                super(view);
                name = (TextView) view.findViewById(R.id.nameTextView);
                placeImage = (ImageView) view.findViewById(R.id.placeImage);
                rating=(RatingBar) view.findViewById(R.id.ratingBarFavPlace);
            }
        }


        public PlacesAdapter(List<Venue> places) {
            this.list = places;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.place, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            list.get(position);
            holder.name.setText(list.get(position).getName());
            Glide.with(UserDashboardActivity.this)
                    .load(list.get(position).getImage())
                    .into(holder.placeImage);
            holder.rating.setRating(list.get(position).getRating());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public void clear() {
            // TODO Auto-generated method stub
            list.clear();

        }

        private void addItem(Venue place) {
            list.add(place);
            this.notifyDataSetChanged();
        }


    }
}
