package com.nikostsompanidis.aroundme;


import java.util.ArrayList;

/**
 * Created by Nikos Tsompanidis on 25/10/2017.
 */

public class Venue {

    private long venueId;
    private int rating,likes;
    private ArrayList<Double> hours;
    private String Name,Description,url,address;
    private ArrayList<String> categories,photos;
    private ArrayList<Integer> stats;
    private boolean isOpen;



    public Venue(long venueId, int rating, int likes, ArrayList<Double> hours, String name, String description, String url, ArrayList<String> categories, ArrayList<String> photos, ArrayList<Integer> stats) {
        this.venueId = venueId;
        this.rating = rating;
        this.likes = likes;
        this.hours = hours;
        Name = name;
        Description = description;
        this.url = url;
        this.categories = categories;
        this.photos = photos;
        this.stats = stats;
    }

    public Venue(int rating, String name, String address, boolean isOpen) {
        this.rating = rating;
        Name = name;
        this.address = address;
        this.isOpen = isOpen;
    }

    public long getVenueId() {
        return venueId;
    }

    public void setVenueId(long venueId) {
        this.venueId = venueId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public ArrayList<Double> getHours() {
        return hours;
    }

    public void setHours(ArrayList<Double> hours) {
        this.hours = hours;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    public ArrayList<String> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<String> photos) {
        this.photos = photos;
    }

    public ArrayList<Integer> getStats() {
        return stats;
    }

    public void setStats(ArrayList<Integer> stats) {
        this.stats = stats;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }
}
