package com.example.go4lunch.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.model.Lunch;
import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.RepositoryPlaces;
import com.example.go4lunch.repository.RepositoryUser;
import com.example.go4lunch.repository.RepositoryWorkmates;
import com.example.go4lunch.tool.Tool;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;

public class DetailRestaurant extends AppCompatActivity implements RecyclerVIewAdapter.UpdateWorkmatesListener {
    private PlacesClient placesClient;
    private TextView name, address;
    private LinearLayout callButton, website, mLike;
    private ImageView picRestaurant, star1, star2, star3, favoriteImageView;
    private RecyclerView recyclerView;
    private UserViewModel userViewModel;
    private RecyclerVIewAdapter adapter;
    private RepositoryUser repositoryUser = new RepositoryUser();
    private RepositoryPlaces repositoryPlaces = new RepositoryPlaces();
    private RepositoryWorkmates repositoryWorkmates = new RepositoryWorkmates();
    private Executor executor;
    private String response = null;
    private boolean checked = false, favorite = false;
    private Lunch lunch;
    private String restaurantName = null, restaurantType = null;
    private FloatingActionButton addLunchButton;
    private User currentUser;
    double rating;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_restaurant);
        this.configureToolbar();

        Intent intent = getIntent();
        response = intent.getStringExtra("restaurantId");
        Toast.makeText(this, response, Toast.LENGTH_SHORT).show();

        Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);

        initElement();
        placesClient = Places.createClient(this);
        initTentative(response);


    }

    private void initElement() {
        name = findViewById(R.id.textview_name_restaurant);
        address = findViewById(R.id.address_restaurant);
        callButton = findViewById(R.id.call_buttondetail);
        website = findViewById(R.id.web_buttondetail);
        picRestaurant = findViewById(R.id.imageview_pic_restaurant);
        addLunchButton = findViewById(R.id.floatingActionButton_detail);
        adapter = new RecyclerVIewAdapter(this, this);
        recyclerView = findViewById(R.id.recyclerview_restaurant);
        star1 = findViewById(R.id.star1);
        star2 = findViewById(R.id.star2);
        star3 = findViewById(R.id.star3);
        mLike = findViewById(R.id.like_buttondetail);
        favoriteImageView = findViewById(R.id.icon_like_imageview);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userViewModel = new UserViewModel(repositoryUser, repositoryWorkmates, executor, repositoryPlaces);
        userViewModel.getAllUsersFromRestaurant(response).observe(this, this::getAllUsers);
    }

    private void getMyUser(User user) {
        currentUser = user;
        if (currentUser.getDateLunch() != null) {
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            if (currentUser.getDateLunch().get(date) != null) {
                if (currentUser.getDateLunch().get(date).getRestaurantId() != null) {
                    if (currentUser.getDateLunch().get(date).getRestaurantId().equals(response)) {
                        checked = true;
                        updateFloatingButton();
                    }
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        userViewModel.getUserObject(getCurrentUser().getUid()).observe(this, this::getMyUser);
        userViewModel.getFavoriteList(getCurrentUser().getUid()).observe(this, this::checkFavoriteList);
    }

    private void checkFavoriteList(List<String> list) {
        favorite = Tool.checkFavorite(list, response);
        updateUiFavorite();
    }


    private void getAllUsers(List<User> userList) {
        adapter.updateWorkmateList(userList);
    }


    private void initTentative(String placeId) {

        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
                Place.Field.ADDRESS, Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI,
                Place.Field.PHOTO_METADATAS, Place.Field.TYPES, Place.Field.RATING);

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
                if (exception instanceof ApiException) {
                    final ApiException apiException = (ApiException) exception;
                    Log.e("TAG", "Place not found: " + exception.getMessage());
                    final int statusCode = apiException.getStatusCode();
                }
            });

            if (place.getRating() != null)
                rating = place.getRating();
            else {
                rating = 0;
            }

            if (0 < rating && rating < 1.6) {
                star3.setVisibility(View.GONE);
                star2.setVisibility(View.GONE);
            } else if (rating < 3.2) {
                star3.setVisibility(View.GONE);
            } else if (rating == 0){
                star3.setVisibility(View.GONE);
                star2.setVisibility(View.GONE);
                star1.setVisibility(View.GONE);
            }

            if (place.getName() != null)
                name.setText(place.getName());
            if (place.getAddress() != null)
                address.setText(place.getAddress());

            restaurantName = place.getName();

            restaurantType = place.getTypes().get(0).toString();
            String phone;
            if (place.getPhoneNumber() != null)
                phone = "tel:" + place.getPhoneNumber();
            else
                phone = null;
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
            if (phoneNumber != null) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(phoneNumber));
                startActivity(intent);
            } else
                Toast.makeText(this, "It seems that there is no number phone yet", Toast.LENGTH_SHORT).show();
        });

        website.setOnClickListener(v -> {
            if (websiteUri == null || websiteUri.toString() == "") {
                Toast.makeText(this, "It seems that there is no website yet", Toast.LENGTH_SHORT).show();
            } else {
                Intent intentWeb = new Intent(Intent.ACTION_VIEW).setData(websiteUri);
                startActivity(intentWeb);
            }
        });

        mLike.setOnClickListener(v -> {
            if (!favorite) {
                favorite = true;
                updateUiFavorite();
                userViewModel.createFavoriteList(getCurrentUser().getUid(), response);
            } else {
                favorite = false;
                updateUiFavorite();
                userViewModel.deleteFavoriteFromList(getCurrentUser().getUid(), response);
            }
        });
    }

    private void updateUiFavorite() {
        if (favorite) {
            favoriteImageView.setImageResource(R.drawable.star_icons_rating);
        } else {
            favoriteImageView.setImageResource(R.drawable.stars_icons_dark);
        }
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


    public void addRestaurantLunch(View view) {
        if (!checked) {
            this.userViewModel.createLunch(getCurrentUser().getUid(), response, restaurantName, restaurantType);
            userViewModel.getAllUsersFromRestaurant(response).observe(this, this::getAllUsers);
            checked = true;
        } else {
            this.userViewModel.deleteLunch(getCurrentUser().getUid());
            userViewModel.getAllUsersFromRestaurant(response).observe(this, this::getAllUsers);
            checked = false;
        }
        updateFloatingButton();

    }

    public void updateFloatingButton() {
        if (checked)
            addLunchButton.setImageResource(R.drawable.checked_restaurant_icons);
        else
            addLunchButton.setImageResource(R.drawable.choose_restaurant_icons);
    }


    @Nullable
    protected FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }
}