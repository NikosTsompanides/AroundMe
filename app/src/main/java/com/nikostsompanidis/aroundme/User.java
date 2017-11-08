package com.nikostsompanidis.aroundme;

import java.util.ArrayList;

/**
 * Created by Nikos Tsompanidis on 2/11/2017.
 */

public class User {

    private String name,email,password ;
    private int user_id,favoritePlaces;


    public User(String name, String email, String password, int user_id,int favoritePlaces) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.user_id = user_id;
        this.favoritePlaces = favoritePlaces;
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

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getFavoritePlaces() {
        return favoritePlaces;
    }

    public void setFavoritePlaces(int favoritePlaces) {
        this.favoritePlaces = favoritePlaces;
    }
}
