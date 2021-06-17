package com.example.go4lunch.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Lunch {

    private Date timestamp;
    private String restaurantId, userId;

    public Lunch(String restaurantId, String userId) {
        this.restaurantId = restaurantId;
        this.userId = userId;
    }

    public Lunch() {
    }

    @ServerTimestamp
    public Date getTimestampCreated() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
