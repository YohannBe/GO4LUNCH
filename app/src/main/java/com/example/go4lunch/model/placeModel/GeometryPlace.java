package com.example.go4lunch.model.placeModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GeometryPlace {

    @SerializedName("location")
    @Expose
    private LocationPlace location;

    public LocationPlace getLocation() {
        return location;
    }

    public void setLocation(LocationPlace location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "GeometryPlace{" +
                "location=" + location +
                '}';
    }
}
