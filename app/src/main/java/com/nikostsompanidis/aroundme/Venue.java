package com.nikostsompanidis.aroundme;


import java.util.ArrayList;

/**
 * Created by Nikos Tsompanidis on 25/10/2017.
 */

public class Venue {

    private long venueId;
    private int rating,chekInsCount;
    private ArrayList<Double> hours;
    private String Name,Description,url,address,image,phone;
    private ArrayList<String> categories,photos;
    private ArrayList<Integer> stats;
    private boolean isOpen;
    private int distance;
    private double lat,lng;


    public Venue(long venueId, double lat, double lng, int rating, int chekInsCount, ArrayList<Double> hours, String name, String description, String url, String address, String image, int priceTier, String priceMessage, String priceCurrency, ArrayList<String> categories, ArrayList<String> photos, ArrayList<Integer> stats, boolean isOpen) {
        this.venueId = venueId;
        this.lat = lat;
        this.lng = lng;
        this.rating = rating;
        this.chekInsCount = chekInsCount;
        this.hours = hours;
        Name = name;
        Description = description;
        this.url = url;
        this.address = address;
        this.image = image;
        this.categories = categories;
        this.photos = photos;
        this.stats = stats;
        this.isOpen = isOpen;
    }

    public Venue(int rating, String name, String address, boolean isOpen, String image) {
        this.rating = rating;
        Name = name;
        this.address = address;
        this.isOpen = isOpen;
        this.image=image;
    }

    public Venue(int rating, String name, String address, boolean isOpen) {
        this.rating = rating;
        Name = name;
        this.address = address;
        this.isOpen = isOpen;
    }

    public Venue(double lat, double lng, int rating, int chekInsCount, String name, String address,boolean isOpen,String phone,int distance,String image) {
        this.lat = lat;
        this.lng = lng;
        this.rating = rating;
        this.chekInsCount = chekInsCount;
        Name = name;
        this.address = address;
        this.isOpen = isOpen;
        this.distance=distance;
        this.phone=phone;
        this.image=image;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
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

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public int getChekInsCount() {
        return chekInsCount;
    }

    public String getImage() {
        return image;
    }



    public void setLat(long lat) {
        this.lat = lat;
    }

    public void setLng(long lng) {
        this.lng = lng;
    }

    public void setChekInsCount(int chekInsCount) {
        this.chekInsCount = chekInsCount;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


}
