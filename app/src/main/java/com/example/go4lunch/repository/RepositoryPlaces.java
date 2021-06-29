package com.example.go4lunch.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.api.PlaceHelper;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;

public class RepositoryPlaces {

    private final PlaceHelper placeHelper;

    public RepositoryPlaces() {
        this.placeHelper = new PlaceHelper();
    }

    public void getPlacesIds(LatLng latLng){
        placeHelper.initUrlMapsNearby(latLng);
    }

    public MutableLiveData<List<HashMap<String, String>>> getHashMapsIds(){
        return PlaceHelper.ParserTask.sendListToFragment();
    }


}
