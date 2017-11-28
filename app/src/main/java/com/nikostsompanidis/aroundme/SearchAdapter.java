package com.nikostsompanidis.aroundme;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Nikos Tsompanidis on 22/11/2017.
 */

public class SearchAdapter extends ArrayAdapter<Venue> {
    private SearchActivity activity;
    private List<Venue> venuesList;
    private List<Venue> searchList;

    public SearchAdapter(SearchActivity context, int resource, List<Venue> objects) {
        super(context, resource, objects);
        this.activity = context;
        this.venuesList = objects;
        this.searchList = new ArrayList<>();
        this.searchList.addAll(venuesList);
    }

    @Override
    public int getCount() {
        return venuesList.size();
    }

    @Override
    public Venue getItem(int position) {
        return venuesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        // If holder not exist then locate all view from UI file.
        if (convertView == null) {
            // inflate UI from XML file
            convertView = inflater.inflate(R.layout.search_item, parent, false);
            // get all UI view
            holder = new ViewHolder(convertView);
            // set tag for holder
            convertView.setTag(holder);
        } else {
            // if holder created, get tag from view
            holder = (ViewHolder) convertView.getTag();
        }

        if(getItem(position)!=null){
            holder.venueName.setText(getItem(position).getName());
            Glide.with(convertView)
                    .load(getItem(position).getImage())
                    .into(holder.imageView);
            holder.distance.setText("Distance : "+(double)getItem(position).getDistance()/1000+" km");
            holder.rating.setRating(getItem(position).getRating());
        }




        return convertView;
    }

    // Filter method
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        venuesList.clear();
        if (charText.length() == 0) {
            venuesList.addAll(searchList);
        } else {
            for (Venue s : searchList) {
                if (s.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    venuesList.add(s);
                }
            }
        }
        notifyDataSetChanged();
    }

    private class ViewHolder {
        private ImageView imageView;
        private TextView venueName,distance;
        private RatingBar rating;

        public ViewHolder(View v) {
            imageView = (ImageView) v.findViewById(R.id.image_view);
            venueName = (TextView) v.findViewById(R.id.nameTextViewSearch);
            distance=(TextView) v.findViewById(R.id.distanceSearchTextView);
            rating=(RatingBar) v.findViewById(R.id.ratingBar3);
        }
    }
}
