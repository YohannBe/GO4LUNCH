package com.example.go4lunch.model.placeModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RestaurantData {

    @SerializedName("formatted_address")
    @Expose
    private String address;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("rating")
    @Expose
    private double rating;

    @SerializedName("geometry")
    @Expose
    private GeometryPlace geometry;

    @SerializedName("opening_hours")
    @Expose
    private HoursPlace opening_hours;

    @SerializedName("photos")
    @Expose
    private ArrayList<PhotoPlace> photos;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public GeometryPlace getGeometry() {
        return geometry;
    }

    public void setGeometry(GeometryPlace geometry) {
        this.geometry = geometry;
    }

    public HoursPlace getOpening_hours() {
        return opening_hours;
    }

    public void setOpening_hours(HoursPlace opening_hours) {
        this.opening_hours = opening_hours;
    }

    public ArrayList<PhotoPlace> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<PhotoPlace> photos) {
        this.photos = photos;
    }

    @Override
    public String toString() {
        return "RestaurantData{" +
                "address='" + address + '\'' +
                ", name='" + name + '\'' +
                ", rating='" + rating + '\'' +
                ", geometry=" + geometry +
                ", opening_hours=" + opening_hours +
                ", photos=" + photos +
                '}';
    }
}
