package com.example.go4lunch.model.placeModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResultPlaces {

    @SerializedName("geometry")
    @Expose
    private GeometryPlace geometry;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("place_id")
    @Expose
    private String placeId;

    @Override
    public String toString() {
        return "ResultPlaces{" +
                "geometry=" + geometry +
                ", name='" + name + '\'' +
                ", placeId='" + placeId + '\'' +
                '}';
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public GeometryPlace getGeometry() {
        return geometry;
    }

    public void setGeometry(GeometryPlace geometry) {
        this.geometry = geometry;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
