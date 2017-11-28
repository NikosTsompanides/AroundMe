package com.nikostsompanidis.aroundme;

import java.util.ArrayList;

/**
 * Created by Nikos Tsompanidis on 2/11/2017.
 */

public class User {

    private String name,email,password ;
    private String user_id;
    private ArrayList<Venue> favoritePlaces = new ArrayList<>();


    public User(String name, String email, String user_id) {
        this.name = name;
        this.email = email;
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public ArrayList<Venue> getFavoritePlaces() {
        return favoritePlaces;
    }

    public void setFavoritePlaces(ArrayList<Venue> favoritePlaces) {
        this.favoritePlaces = favoritePlaces;
    }
}
