package com.example.go4lunch.repository;


import androidx.lifecycle.LiveData;

import com.example.go4lunch.api.PlaceHelper;
import com.example.go4lunch.model.PlaceData;
import com.example.go4lunch.model.placeModel.ResultPlaces;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
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

    public LiveData<List<ResultPlaces>> getHashMapsIds() {
        return placeHelper.sendListToFragment();
    }

    public LiveData<List<PlaceData>> sendIdToFetchPlace(List<HashMap<String, String>> hashMaps, PlacesClient placesClient) {
        return placeHelper.sendIdToFetchPlace(hashMaps, placesClient);
    }

    public LiveData<Place> fetchThePlacePlace(String placeId, PlacesClient placesClient){
        return placeHelper.fetchThePlacePlace(placeId, placesClient);
    }


}
