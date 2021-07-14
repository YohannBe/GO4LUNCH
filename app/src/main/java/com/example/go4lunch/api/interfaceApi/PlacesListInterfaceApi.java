package com.example.go4lunch.api.interfaceApi;

import com.example.go4lunch.model.placeModel.ListFeed;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface PlacesListInterfaceApi {

    @Headers("Content-Type: application/json")
    @GET("json?")
    Call<ListFeed> getData(@Query("location") String location, @Query("radius") String radius,
                           @Query("type") String type, @Query("key") String key);
}
