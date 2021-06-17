package com.example.go4lunch.model;

import java.util.ArrayList;

public class User {

    private String uid;
    private String firstName;
    private String lastName;
    private String urlPicture;
    private ArrayList<Lunch> lunchList;

    public ArrayList<Lunch> getLunchList() {
        return lunchList;
    }

    public void setLunchList(ArrayList<Lunch> lunchList) {
        this.lunchList = lunchList;
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
