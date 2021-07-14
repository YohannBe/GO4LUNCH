package com.example.go4lunch.model.placeModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LocationPlace {

    @SerializedName("lat")
    @Expose
    private double lat;

    @SerializedName("lng")
    @Expose
    private double lng;

    @Override
    public String toString() {
        return "LocationPlace{" +
                "lat=" + lat +
                ", lng=" + lng +
                '}';
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
