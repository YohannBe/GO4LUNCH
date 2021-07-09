package com.example.go4lunch.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

public class User implements Serializable {

    private String uid;
    private String firstName;
    private String lastName;
    private String urlPicture;
    private Map<String, Lunch> dateLunch;
    private ArrayList<String> favorite;

    public ArrayList<String> getFavorite() {
        return favorite;
    }

    public void setFavorite(ArrayList<String> favorite) {
        this.favorite = favorite;
    }

    public Map<String, Lunch> getDateLunch() {
        return dateLunch;
    }

    public void setDateLunch(Map<String, Lunch> dateLunch) {
        this.dateLunch = dateLunch;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }



    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(String urlPicture) {
        this.urlPicture = urlPicture;
    }

    public User(String uid, String firstName, String lastName, String urlPicture) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.urlPicture = urlPicture;
    }
    public User(){}





    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
