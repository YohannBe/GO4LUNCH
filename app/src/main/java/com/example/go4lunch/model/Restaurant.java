package com.example.go4lunch.model;

import java.util.Date;

public class Restaurant {
    private String id, name, userId;
    private Date date;

    public Restaurant(String id, String name, String userId, Date date) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
