package com.nikostsompanidis.aroundme;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.sql.Timestamp;

/**
 * Created by Nikos Tsompanidis on 10/11/2017.
 */

public class Tip implements Parcelable,Comparable {

    private String text,imageUrl;
    private int upvotes,downvotes;
    private Timestamp createdAt;
    private FoursquareUser user;

    public Tip(String text, String imageUrl, int upvotes, int downvotes, Timestamp createdAt,FoursquareUser user) {
        this.text = text;
        this.imageUrl = imageUrl;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.createdAt = createdAt;
        this.user =user;
    }

    public Tip(String text, int upvotes, int downvotes, Timestamp createdAt,FoursquareUser user) {
        this.text = text;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.createdAt = createdAt;
        this.user=user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    public int getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(int downvotes) {
        this.downvotes = downvotes;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public FoursquareUser getUser() {
        return user;
    }

    public void setUser(FoursquareUser user) {
        this.user = user;
    }

    public Tip(Parcel in) {
        super();
        readFromParcel(in);
    }

    public static final Parcelable.Creator<Tip> CREATOR = new Parcelable.Creator<Tip>() {
        public Tip createFromParcel(Parcel in) {
            return new Tip(in);
        }

        public Tip[] newArray(int size) {

            return new Tip[size];
        }

    };

    public void readFromParcel(Parcel in) {
        text = in.readString();
        imageUrl = in.readString();
        upvotes = in.readInt();
        downvotes = in.readInt();
        createdAt= new Timestamp(in.readLong());
        user = new FoursquareUser(in.readString(),in.readString(),in.readString());

    }
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeString(imageUrl);
        dest.writeInt(upvotes);
        dest.writeInt(downvotes);
        dest.writeLong(createdAt.getNanos());
        //dest.writeTypedObject(FoursquareUser,user);
    }

    @Override
    public int compareTo(@NonNull Object o) {
        Tip tip =(Tip) o;

        if(this.getUpvotes()<tip.getUpvotes())
            return 1;

        else
            return -1;

    }
}
