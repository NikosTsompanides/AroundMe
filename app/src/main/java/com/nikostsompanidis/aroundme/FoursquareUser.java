package com.nikostsompanidis.aroundme;

/**
 * Created by Nikos Tsompanidis on 10/11/2017.
 */

public class FoursquareUser {

    private String name,surname;
    private String imageUrl;


    public FoursquareUser(String name, String surname, String imageUrl) {
        this.name = name;
        this.surname = surname;
        this.imageUrl = imageUrl;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
