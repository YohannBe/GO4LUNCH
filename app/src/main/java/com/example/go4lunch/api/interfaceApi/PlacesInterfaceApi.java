package com.example.go4lunch.api.interfaceApi;

import com.example.go4lunch.model.placeModel.Feed;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface PlacesInterfaceApi {

    @Headers("Content-Type: application/json")
    @GET("json?")
    Call<Feed> getData(@Query("place_id") String placeId, @Query("fields") String fields, @Query("key") String key);
}

