package com.nikostsompanidis.aroundme;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.InputStream;
import java.net.URL;

public class VenueDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private int rating,chekInsCount,priceTier,distance;
    private String name,address,phone,image;
    private double lat,lng;
    private boolean isOpen;
    private GoogleMap mMap;
    private RatingBar ratingBar;
    private TextView checkInsCount,Name,Address,Phone,IsOpen,Distance;
    private ImageView Image ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.likeFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Added to your favorite places ! ", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        ratingBar = (RatingBar) findViewById(R.id.ratingBar2);
        checkInsCount = (TextView) findViewById(R.id.checkInTextView);
        Name = (TextView) findViewById(R.id.shopNameDetailsTextView);
        Address = (TextView) findViewById(R.id.addressTextView);
        Phone = (TextView) findViewById(R.id.phoneTextView);
        IsOpen = (TextView) findViewById(R.id.IsOpenTextView);
        Distance=(TextView) findViewById(R.id.distanceTextView);
        Image = (ImageView)findViewById(R.id.detailsImageView) ;




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
        }

        toolbar.setTitle("Details: "+Name);


        Image.setBackground(this.LoadImageFromWebOperations(image));

        ratingBar.setRating(rating);
        checkInsCount.setText("People Visits: "+chekInsCount);
        Name.setText(name);
        Address.setText(""+address);
        if(phone.isEmpty()){
            Phone.setText("Oups! The phone number is Missing :(");
            Phone.setTextSize(8);

        }
        else
            Phone.setText(""+phone);
        if(isOpen){
            IsOpen.setText("Open");
            IsOpen.setTextColor(Color.parseColor("#00E676"));
        }
        else{
            IsOpen.setText("Close");
            IsOpen.setTextColor(Color.parseColor("#FF3D00"));
        }

        Distance.setText(Distance.getText()+""+(double)distance/1000+" km");
        Image.setImageDrawable(this.LoadImageFromWebOperations(image));



    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng marker = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(marker).title(name.toString()));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 14.0f));

    }

    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

}
