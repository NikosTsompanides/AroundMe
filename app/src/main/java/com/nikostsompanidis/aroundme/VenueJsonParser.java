package com.nikostsompanidis.aroundme;

/**
 * Created by Nikos Tsompanidis on 25/10/2017.
 */
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VenueJsonParser {

    public static ArrayList<Venue> getDatafromJson(String jsonString){
        ArrayList<Venue> results = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject response = jsonObject.getJSONObject("response");
            JSONArray groups = response.getJSONArray("groups");
            JSONObject zero = groups.getJSONObject(0);
            JSONArray items = zero.getJSONArray("items");


            Venue ven = null;

            for(int i =0; i< items.length(); i++){
                String name,address,image,phone,prefix,suffix;
                int rating,checkInCount,distance;
                boolean isOpen;
                double lat,lng;
                String venueId;


                JSONObject currentShop = items.optJSONObject(i);
                JSONObject venue = currentShop.optJSONObject("venue");
                name = venue.optString("name");
                address=venue.optJSONObject("location").optString("address","Oups ! The address is missing :( !");
                isOpen=venue.getJSONObject("hours").getBoolean("isOpen");
                rating=venue.optInt("rating");
                phone=venue.optJSONObject("contact").optString("formattedPhone");
                lat=venue.optJSONObject("location").optDouble("lat",2.0);
                lng=venue.optJSONObject("location").optDouble("lng",2);
                checkInCount=venue.optJSONObject("stats").optInt("checkinsCount");
                distance=venue.optJSONObject("location").optInt("distance");
                prefix=venue.optJSONObject("photos").optJSONArray("groups").optJSONObject(0).optJSONArray("items").optJSONObject(0).optString("prefix");
                suffix=venue.optJSONObject("photos").optJSONArray("groups").optJSONObject(0).optJSONArray("items").optJSONObject(0).optString("suffix");
                image=prefix+"original"+suffix;
                venueId=venue.optString("id");
                ven = new Venue(venueId,lat,lng,rating,checkInCount,name,address,isOpen,phone,distance,image);
                results.add(ven);
            }

        } catch (JSONException e) {
            Log.e("VenueJsonParser", e.getMessage());
        }

        return results;
    }

    public static ArrayList<String> getPhotosFromJson(String jsonString){


        ArrayList<String> photos = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject response = jsonObject.getJSONObject("response");
            JSONObject photosObj = response.getJSONObject("photos");
            JSONArray items = photosObj.getJSONArray("items");

            for(int i =0; i< items.length(); i++){
                String prefix,suffix,image;


                JSONObject currentPhoto = items.optJSONObject(i);
                prefix=currentPhoto.optString("prefix");
                suffix=currentPhoto.optString("suffix");
                image=prefix+"original"+suffix;
                photos.add(image);
            }

        } catch (JSONException e) {
            Log.e("VenueJsonParser", e.getMessage());
        }

        return photos;

    }

}