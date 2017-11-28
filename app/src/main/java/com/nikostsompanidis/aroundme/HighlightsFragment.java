package com.nikostsompanidis.aroundme;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class HighlightsFragment extends Fragment  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private int rating, chekInsCount, distance;
    private String address, phone;
    private double lat, lng;
    private boolean isOpen;
    private GoogleMap mMap;
    private RatingBar ratingBar;
    private TextView checkInsCount, Address, Phone, IsOpen, Distance;
    private SupportMapFragment mapFragment;

    MapView mMapView;
    private GoogleMap googleMap;


    public HighlightsFragment() {
        // Required empty public constructor
    }

    public static HighlightsFragment newInstance(String param1, String param2) {
        HighlightsFragment fragment = new HighlightsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            rating = getArguments().getInt("rating");
            chekInsCount = getArguments().getInt("checkInCount");
            distance = getArguments().getInt("distance");
            address = getArguments().getString("address");
            phone = getArguments().getString("phone");
            lat = getArguments().getDouble("lat");
            lng = getArguments().getDouble("lng");
            isOpen = getArguments().getBoolean("isOpen");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_highlights, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                googleMap.setMyLocationEnabled(true);

                // For dropping a marker at a point on the Map
                LatLng marker = new LatLng(lat, lng);
                mMap.addMarker(new MarkerOptions().position(marker));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(marker).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        return rootView;

    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){


        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar2);
        checkInsCount = (TextView) view.findViewById(R.id.checkInTextView);
        Address = (TextView)  view.findViewById(R.id.addressTextView);
        Phone = (TextView)  view.findViewById(R.id.phoneTextView);
        IsOpen = (TextView)  view.findViewById(R.id.IsOpenTextView);
        Distance=(TextView)  view.findViewById(R.id.distanceTextView);


        ratingBar.setRating(rating);
        checkInsCount.setText("People Visits: "+chekInsCount);
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

    }

}
