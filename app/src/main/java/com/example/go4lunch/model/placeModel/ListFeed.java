package com.example.go4lunch.model.placeModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListFeed {

    @SerializedName("results")
    @Expose
    private List<ResultPlaces> results = null;

    public List<ResultPlaces> getResults() {
        return results;
    }

    public void setResults(List<ResultPlaces> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "ListFeed{" +
                "results=" + results +
                '}';
    }
}
