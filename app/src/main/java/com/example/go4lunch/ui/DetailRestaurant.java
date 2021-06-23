package com.example.go4lunch.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.fragments.JsonParser;
import com.example.go4lunch.fragments.MainFragment;
import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.RepositoryUser;
import com.example.go4lunch.repository.RepositoryWorkmates;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

public class DetailRestaurant extends AppCompatActivity implements RecyclerVIewAdapter.UpdateWorkmatesListener {
    private PlacesClient placesClient;
    private TextView name, address;
    private LinearLayout callButton, website;
    private ImageView picRestaurant;
    private RecyclerView recyclerView;
    private UserViewModel userViewModel;
    private RecyclerVIewAdapter adapter;
    private RepositoryUser repositoryUser = new RepositoryUser();
    private RepositoryWorkmates repositoryWorkmates = new RepositoryWorkmates();
    private Executor executor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_restaurant);
        this.configureToolbar();

        Intent intent = getIntent();
        String response = intent.getStringExtra("restaurantId");
        Toast.makeText(this, response, Toast.LENGTH_SHORT).show();

        Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);

        initElement();
        // Create a new Places client instance.
        placesClient = Places.createClient(this);
        initTentative(response);


    }

    private void initElement() {
        name = findViewById(R.id.textview_name_restaurant);
        address = findViewById(R.id.address_restaurant_detail);
        callButton = findViewById(R.id.call_buttondetail);
        website = findViewById(R.id.web_buttondetail);
        picRestaurant = findViewById(R.id.imageview_pic_restaurant);
        adapter = new RecyclerVIewAdapter(this, this);
        recyclerView = findViewById(R.id.recyclerview_restaurant);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userViewModel = new UserViewModel(repositoryUser, repositoryWorkmates, executor);
        userViewModel.getAllUsers().observe(this, this:: getAllUsers);

    }

    private void getAllUsers(List<User> userList) {
        adapter.updateWorkmateList(userList);
    }


    private void initTentative(String placeId) {

        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
                Place.Field.ADDRESS, Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI,
                Place.Field.PHOTO_METADATAS);

        // Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields)
                .build();

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
            final PhotoMetadata photoMetadata = metadata.get(0);

            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .build();

            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                picRestaurant.setImageBitmap(bitmap);
            }).addOnFailureListener((exception) -> {
                if (exception instanceof  ApiException){
                    final ApiException apiException = (ApiException) exception;
                    Log.e("TAG", "Place not found: " + exception.getMessage());
                    final int statusCode = apiException.getStatusCode();
                }
            });

            name.setText(place.getName());
            address.setText(place.getAddress());
            String phone = "tel:" + place.getPhoneNumber();
            initListeners(phone, place.getWebsiteUri());
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
                // Handle error with given status code.
                Log.e("TAG", "Place not found: " + exception.getMessage());
            }
        });
    }

    private void initListeners(String phoneNumber, Uri websiteUri) {
        callButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(phoneNumber));
            startActivity(intent);

        });


        website.setOnClickListener(v -> {
            if (websiteUri == null || websiteUri.toString() == "") {
                Toast.makeText(DetailRestaurant.this, websiteUri.toString(), Toast.LENGTH_SHORT).show();
            } else {
                Intent intentWeb = new Intent(Intent.ACTION_VIEW).setData(websiteUri);
                startActivity(intentWeb);
            }
        });

    }

    private void configureToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_id);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public void onUpdateWorkmate(User user) {

    }
}