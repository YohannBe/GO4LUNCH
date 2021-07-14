package com.example.go4lunch.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.model.User;
import com.example.go4lunch.notification.LunchReminder;
import com.example.go4lunch.recyclerview.RecyclerVIewAdapterDetailRestaurant;
import com.example.go4lunch.tool.Tool;
import com.example.go4lunch.viewmodel.UserViewModel;
import com.example.go4lunch.viewmodel.WorkmateViewModel;
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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DetailRestaurant extends AppCompatActivity implements RecyclerVIewAdapterDetailRestaurant.UpdateWorkmatesListener {
    private PlacesClient placesClient;
    private TextView name, address;
    private LinearLayout callButton, website, mLike;
    private ImageView picRestaurant, favoriteImageView;
    private RecyclerVIewAdapterDetailRestaurant adapter;
    private RatingBar ratingBar;

    private UserViewModel userViewModel;

    private String response = null;
    private boolean checked = false, favorite = false;
    private String restaurantName = null, restaurantType = null, chosenAddress = null;
    private FloatingActionButton addLunchButton;
    private double rating;
    private String favoriteAddString = "";
    private boolean mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_restaurant);


        Intent intent = getIntent();
        response = intent.getStringExtra("restaurantId");
        mainFragment = intent.getBooleanExtra("parent", true);

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
        adapter = new RecyclerVIewAdapterDetailRestaurant(this, this, getCurrentUser().getUid());
        RecyclerView recyclerView = findViewById(R.id.recyclerview_restaurant);
        ratingBar = findViewById(R.id.ratingBar);
        mLike = findViewById(R.id.like_buttondetail);
        favoriteImageView = findViewById(R.id.icon_like_imageview);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userViewModel = new UserViewModel();
        userViewModel.getUserObject(Objects.requireNonNull(getCurrentUser()).getUid()).observe(this, this::getMyUser);
        WorkmateViewModel workmateViewModel = new WorkmateViewModel();

        workmateViewModel.getDocumentLunch(response).observe(this, this::getAllUsers);
    }

    private void getMyUser(User user) {
        if (Tool.checkIdDateRestaurantExist(user, response) ){
            checked = true;
            updateFloatingButton();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    private void checkFavoriteList(List<String> list) {
        favorite = Tool.checkFavorite(list, favoriteAddString);
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
            assert metadata != null;
            final PhotoMetadata photoMetadata = metadata.get(0);

            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .build();

            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                picRestaurant.setImageBitmap(bitmap);
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    Log.e("TAG", "Place not found: " + exception.getMessage());
                }
            });

            if (place.getRating() != null)
                rating = place.getRating();
            else {
                rating = 0;
            }

            if (0 < rating && rating < 1.6) {
                ratingBar.setRating(1);
            } else if (rating < 3.2) {
                ratingBar.setRating(2);
            } else if (rating == 0) {
                ratingBar.setVisibility(View.GONE);
            } else if (rating >= 3.2)
                ratingBar.setRating(3);

            if (place.getName() != null)
                name.setText(place.getName());
            if (place.getAddress() != null)
                address.setText(place.getAddress());

            chosenAddress = place.getAddress();

            restaurantName = place.getName();
            userViewModel.getFavoriteList(Objects.requireNonNull(getCurrentUser()).getUid()).observe(this, this::checkFavoriteList);
            favoriteAddString = this.response + "/" + restaurantName;

            restaurantType = Objects.requireNonNull(place.getTypes()).get(0).toString();
            String phone;
            if (place.getPhoneNumber() != null)
                phone = "tel:" + place.getPhoneNumber();
            else
                phone = null;
            initListeners(phone, place.getWebsiteUri());
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
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
                Toast.makeText(this, getString(R.string.no_number_phone), Toast.LENGTH_SHORT).show();
        });

        website.setOnClickListener(v -> {
            if (websiteUri == null || websiteUri.toString().equals("")) {
                Toast.makeText(this, getString(R.string.no_website), Toast.LENGTH_SHORT).show();
            } else {
                Intent intentWeb = new Intent(Intent.ACTION_VIEW).setData(websiteUri);
                startActivity(intentWeb);
            }
        });

        mLike.setOnClickListener(v -> {
            if (!favorite) {
                favorite = true;
                updateUiFavorite();
                userViewModel.createFavoriteList(Objects.requireNonNull(getCurrentUser()).getUid(), favoriteAddString);
                Toast.makeText(this, getString(R.string.add_place_favorite), Toast.LENGTH_SHORT).show();
            } else {
                favorite = false;
                updateUiFavorite();
                userViewModel.deleteFavoriteFromList(Objects.requireNonNull(getCurrentUser()).getUid(), favoriteAddString);
                Toast.makeText(this, getString(R.string.delete_from_favorite), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUiFavorite() {
        if (favorite) {
            favoriteImageView.setImageResource(R.drawable.favorite_true_icons);
        } else {
            favoriteImageView.setImageResource(R.drawable.favorite_false_icons);
        }
    }


    @Override
    public void onUpdateWorkmate(User user) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void addRestaurantLunch(View view) {
        if (!checked) {
            this.userViewModel.createLunch(Objects.requireNonNull(getCurrentUser()).getUid(), response, restaurantName, restaurantType, chosenAddress);


            Intent intent = new Intent(DetailRestaurant.this, LunchReminder.class);
            intent.putExtra("restaurantId", response);
            intent.putExtra("restaurantName", restaurantName);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);


            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            long timeNow = System.currentTimeMillis();

            assert alarmManager != null;
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeNow + 7000, pendingIntent);
            checked = true;
        } else {
            this.userViewModel.deleteLunch(Objects.requireNonNull(getCurrentUser()).getUid());
            checked = false;
        }
        updateFloatingButton();
    }

    public void updateFloatingButton() {
        if (checked) {
            addLunchButton.setImageResource(R.drawable.checked_restaurant_icons);
        } else {
            addLunchButton.setImageResource(R.drawable.choose_restaurant_icons);
        }
    }


    @Nullable
    protected FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }


}