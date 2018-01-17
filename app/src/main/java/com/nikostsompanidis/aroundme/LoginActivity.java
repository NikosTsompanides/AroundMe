package com.nikostsompanidis.aroundme;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    double latitude, longitude;
    private FirebaseAuth mFireBaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private static final int RC_SIGN_IN =1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);


        SharedPreferences prefs = getSharedPreferences(InitialFullscreenActivity.MY_PREFS_NAME, MODE_PRIVATE);
        latitude=prefs.getFloat("lat",0);
        longitude=prefs.getFloat("lng",0);

        mFirebaseDatabase=FirebaseDatabase.getInstance("https://aroundme-11c29.firebaseio.com/");
        mDatabaseReference=mFirebaseDatabase.getReference().child("users");

        mFireBaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user !=null){
                    mDatabaseReference.child(user.getUid()).setValue(new User(user.getDisplayName(),user.getEmail(),user.getUid()));
                    Intent i = new Intent(LoginActivity.this,UserDashboardActivity.class);
                    i.putExtra("lat", latitude);
                    i.putExtra("lng", longitude);
                    i.putExtra("name",user.getDisplayName());
                    i.putExtra("img", String.valueOf(user.getPhotoUrl()));
                    startActivity(i);

                }else{
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(
                                            Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);

                }
            }
        };
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK){
                Toast.makeText(this,"Signed in",Toast.LENGTH_LONG).show();
            }
            else if(resultCode==RESULT_CANCELED){
                Toast.makeText(this,"Sign in cancelled",Toast.LENGTH_LONG).show();
                this.finish();

            }
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        mFireBaseAuth.addAuthStateListener(mAuthStateListener);

    }

    @Override
    public void onPause() {
        super.onPause();
        mFireBaseAuth.removeAuthStateListener(mAuthStateListener);
    }

}