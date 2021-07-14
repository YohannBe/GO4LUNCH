package com.example.go4lunch.model.placeModel;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Feed {

    @SerializedName("result")
    @Expose
    private RestaurantData data;

    public RestaurantData getData() {
        return data;
    }

    public void setData(RestaurantData data) {
        this.data = data;
    }

    @NonNull
    @Override
    public String toString() {
        return "feed{"+
                "data=" + data +
                "}";
    }
}
