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

    public static ArrayList<Venue> getBreakfastShopsfromJson(String jsonString){
        ArrayList<Venue> results = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject response = jsonObject.getJSONObject("response");
            JSONArray groups = response.getJSONArray("groups");
            JSONObject zero = response.getJSONObject("0");
            JSONArray items = zero.getJSONArray("items");

            Venue ven = null;
            for(int i =0; i< items.length(); i++){
                String name,address;
                int rating;
                boolean isOpen;

                JSONObject currentShop = items.getJSONObject(i);
                JSONObject venue = currentShop.getJSONObject("venue");
                name = venue.getString("name");
                address=venue.getJSONObject("address").getString("address");
                isOpen=venue.getJSONObject("hours").getBoolean("isOpen");
                rating=venue.getInt("rating")/2;

                ven = new Venue(rating,name,address,isOpen);
                results.add(ven);
            }

        } catch (JSONException e) {
            Log.e("WeatherJsonParser", e.getMessage());
        }

        return results;
    }

}