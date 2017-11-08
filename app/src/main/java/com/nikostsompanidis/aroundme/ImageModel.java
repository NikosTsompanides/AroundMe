package com.nikostsompanidis.aroundme;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nikos Tsompanidis on 6/11/2017.
 */

public class ImageModel implements Parcelable {

    private String name, url;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
