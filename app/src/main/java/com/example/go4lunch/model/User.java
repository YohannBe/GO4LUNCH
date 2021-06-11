package com.example.go4lunch.model;

public class User {

    private String uid;
    private String firstName;
    private String lastName;

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

    private String urlPicture;



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
