package com.example.go4lunch.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.api.PlaceHelper;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.HashMap;
import java.util.List;

public class RepositoryPlaces {

    private static volatile RepositoryPlaces instance;
    private final PlaceHelper placeHelper;

    public static RepositoryPlaces getInstance() {
        RepositoryPlaces result = instance;
        if (result != null) {
            return result;
        }
        synchronized (RepositoryPlaces.class) {
            if (instance == null) {
                instance = new RepositoryPlaces();
            }
            return instance;
        }
    }

    public LiveData<List<AutocompletePrediction>> startSearch(String query, PlacesClient placesClient, AutocompleteSessionToken token) {
        return placeHelper.startSearch(query, placesClient, token);
    }


    public RepositoryPlaces() {
        this.placeHelper = PlaceHelper.getInstance();
    }

    public void getPlacesIds(LatLng latLng) {
        placeHelper.initUrlMapsNearby(latLng);
    }

    public LiveData<List<HashMap<String, String>>> getHashMapsIds() {
        return PlaceHelper.ParserTask.sendListToFragment();
    }


}
