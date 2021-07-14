package com.example.go4lunch.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.api.interfaceApi.PlacesListInterfaceApi;
import com.example.go4lunch.model.PlaceData;
import com.example.go4lunch.model.placeModel.ListFeed;
import com.example.go4lunch.model.placeModel.ResultPlaces;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlaceHelper {


    private static volatile PlaceHelper instance;
    public static MutableLiveData<List<ResultPlaces>> hashMapsFinal = new MutableLiveData<List<ResultPlaces>>();

    public PlaceHelper() {
    }

    public static PlaceHelper getInstance() {
        PlaceHelper result = instance;
        if (result != null) {
            return result;
        }
        synchronized (PlaceHelper.class) {
            if (instance == null) {
                instance = new PlaceHelper();
            }
            return instance;
        }
    }

    private MutableLiveData<List<AutocompletePrediction>> listPredictionPlaces = new MutableLiveData<>();

    public LiveData<List<AutocompletePrediction>> startSearch(String query, PlacesClient placesClient, AutocompleteSessionToken token) {
        RectangularBounds LAT_LNG_BOUNDS = RectangularBounds.newInstance(
                new LatLng(-33.880490, 151.184363),
                new LatLng(-33.858754, 151.229596));

        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setCountries("US", "FR")
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setSessionToken(token)
                .setQuery(query)
                .build();
        List<AutocompletePrediction> list = new ArrayList<>();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            list.clear();
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                list.add(prediction);
                Log.i("TAG", prediction.getPlaceId());
                Log.i("TAG", prediction.getPrimaryText(null).toString());
            }
            listPredictionPlaces.setValue(list);
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e("TAG", "Place not found: " + apiException.getStatusCode());
            }
        });
        return listPredictionPlaces;
    }

    public void initUrlMapsNearby(LatLng myPosition) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PlacesListInterfaceApi placesListInterfaceApi = retrofit.create(PlacesListInterfaceApi.class);
        Call<ListFeed> call = placesListInterfaceApi.getData(myPosition.latitude + "," + myPosition.longitude,
                "1500", "restaurant",
                BuildConfig.MAPS_API_KEY);

        call.enqueue(new Callback<ListFeed>() {
            @Override
            public void onResponse(Call<ListFeed> call, Response<ListFeed> response) {
                ListFeed listFeed = response.body();
                hashMapsFinal.setValue(listFeed.getResults());
            }

            @Override
            public void onFailure(Call<ListFeed> call, Throwable t) {

            }
        });
    }

    private List<PlaceData> placeFetchedListNotMutable = new ArrayList<>();
    private final MutableLiveData<List<PlaceData>> placeFetchedList = new MutableLiveData<>();
    private int count;

    public LiveData<List<ResultPlaces>> sendListToFragment() {
        return hashMapsFinal;
    }

    public LiveData<List<PlaceData>> sendIdToFetchPlace(List<HashMap<String, String>> hashMaps, PlacesClient placesClient) {
        placeFetchedListNotMutable.clear();
        placeFetchedList.setValue(placeFetchedListNotMutable);
        count = hashMaps.size();

        for (int i = 0; i < hashMaps.size(); i++) {
            HashMap<String, String> hashMapList = hashMaps.get(i);
            String id = hashMapList.get("place_id");
            fetchThePlace(id, placesClient);
            placeFetchedList.setValue(placeFetchedListNotMutable);
        }
        return placeFetchedList;
    }

    public void fetchThePlace(String placeId, PlacesClient placesClient) {

        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
                Place.Field.ADDRESS, com.google.android.libraries.places.api.model.Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI,
                Place.Field.PHOTO_METADATAS, Place.Field.TYPES, Place.Field.RATING, Place.Field.OPENING_HOURS,
                Place.Field.UTC_OFFSET, Place.Field.LAT_LNG);

        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields)
                .build();

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();

            final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
            assert metadata != null;
            final PhotoMetadata photoMetadata = metadata.get(0);

            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .build();

            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();
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

    private MutableLiveData<Place> placeLive = new MutableLiveData<>();


    public LiveData<Place> fetchThePlacePlace(String placeId, PlacesClient placesClient) {

        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
                Place.Field.ADDRESS, com.google.android.libraries.places.api.model.Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI,
                Place.Field.PHOTO_METADATAS, Place.Field.TYPES, Place.Field.RATING, Place.Field.OPENING_HOURS,
                Place.Field.UTC_OFFSET, Place.Field.LAT_LNG);

        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields)
                .build();

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();

            placeLive.setValue(place);

            final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
            assert metadata != null;
            final PhotoMetadata photoMetadata = metadata.get(0);

            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .build();

            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    Log.e("TAG", "Place not found: " + exception.getMessage());
                    placeLive.setValue(place);
                }
            });

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                // Handle error with given status code.
                Log.e("TAG", "Place not found: " + exception.getMessage());
            }
        });
        return placeLive;
    }


}
