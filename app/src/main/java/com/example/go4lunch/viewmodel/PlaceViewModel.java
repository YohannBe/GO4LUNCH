package com.example.go4lunch.viewmodel;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.model.PlaceData;
import com.example.go4lunch.repository.RepositoryPlaces;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PlaceViewModel extends ViewModel {

    private final RepositoryPlaces repositoryPlaces;
    private final MutableLiveData<List<PlaceData>> placeFetchedList = new MutableLiveData<>();

    public PlaceViewModel() {
        this.repositoryPlaces = RepositoryPlaces.getInstance();
    }

    public void getPlacesIds(LatLng latLng) {
        repositoryPlaces.getPlacesIds(latLng);
    }

    public LiveData<List<HashMap<String, String>>> getHashMapsIds() {
        return repositoryPlaces.getHashMapsIds();
    }

    private List<PlaceData> placeFetchedListNotMutable = new ArrayList<>();
    private MutableLiveData<Boolean> tentative = new MutableLiveData<>();
    private int count;

    public void sendIdToFetchPlace(List<HashMap<String, String>> hashMaps, Context context, PlacesClient placesClient) {
        placeFetchedListNotMutable.clear();
        placeFetchedList.setValue(placeFetchedListNotMutable);

        count = hashMaps.size();
        for (int i = 0; i < hashMaps.size(); i++) {
            HashMap<String, String> hashMapList = hashMaps.get(i);
            String id = hashMapList.get("place_id");
            fetchThePlace(id, placesClient, context, i);
        }
    }

    public LiveData<List<PlaceData>> getFetchedPlaceList() {
        return placeFetchedList;
    }

    public LiveData<List<AutocompletePrediction>> startSearch(String query, PlacesClient placesClient, AutocompleteSessionToken token) {
        return repositoryPlaces.startSearch(query, placesClient, token);
    }


        public void fetchThePlace(String placeId, PlacesClient placesClient, Context context, int i) {

        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
                Place.Field.ADDRESS, com.google.android.libraries.places.api.model.Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI,
                Place.Field.PHOTO_METADATAS, Place.Field.TYPES, Place.Field.RATING, Place.Field.OPENING_HOURS,
                Place.Field.UTC_OFFSET, Place.Field.LAT_LNG);

        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields)
                .build();

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            PlaceData placeData = new PlaceData(place);


            final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
            assert metadata != null;
            final PhotoMetadata photoMetadata = metadata.get(0);

            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .build();

            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                placeData.setBitmap(bitmap);
                placeFetchedListNotMutable.add(placeData);
                placeFetchedList.setValue(placeFetchedListNotMutable);

            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    Log.e("TAG", "Place not found: " + exception.getMessage());

                }
            });

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                // Handle error with given status code.
                Log.e("TAG", "Place not found: " + exception.getMessage());
            }
        });
    }
}
