package com.example.go4lunch.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.model.User;
import com.example.go4lunch.myInterface.OnButtonClickedListener;
import com.example.go4lunch.tool.Tool;
import com.example.go4lunch.ui.MainActivity;
import com.example.go4lunch.viewmodel.UserViewModel;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class MyReservationRestaurant extends Fragment implements View.OnClickListener {

    private OnButtonClickedListener mCallback;
    private UserViewModel userViewModel;
    private ImageView picRestaurant, price1, price2, price3, favoriteImageView;
    private TextView restaurantName, restaurantAddress, nothingText;
    private PlacesClient placesClient;
    private LinearLayout callButton, website, mLike;
    private int price;
    private double rating;
    private ScrollView container;
    private Button buttonCancel;
    private boolean favorite = false;
    private String restaurantId;
    private String keyRestaurant = null;
    private RatingBar ratingBar;
    String favoriteAddString;

    public MyReservationRestaurant() {
    }

    public static MyReservationRestaurant newInstance() {
        return (new MyReservationRestaurant());
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_my_reservation_restaurant, container, false);
        initElements(result);
        return result;
    }

    private void initElements(View result) {


        Places.initialize(Objects.requireNonNull(getContext()), BuildConfig.MAPS_API_KEY);
        placesClient = Places.createClient(Objects.requireNonNull(getActivity()));
        userViewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);
        userViewModel.getUserObject(getCurrentUser().getUid()).observe(this, this::myUser);
        picRestaurant = result.findViewById(R.id.imageview_pic_restaurant_mylunch);
        restaurantName = result.findViewById(R.id.textview_name_restaurant_mylunch);
        restaurantAddress = result.findViewById(R.id.address_restaurant_mylunch);
        price1 = result.findViewById(R.id.pricy1);
        price2 = result.findViewById(R.id.pricy2);
        price3 = result.findViewById(R.id.pricy3);
        ratingBar = result.findViewById(R.id.ratingBar_mylunch);
        this.container = result.findViewById(R.id.scrollView2);
        nothingText = result.findViewById(R.id.noreservation_textview);
        callButton = result.findViewById(R.id.call_buttondetail_mylunch);
        website = result.findViewById(R.id.web_buttondetail_mylunch);
        mLike = result.findViewById(R.id.like_buttondetail_mylunch);
        favoriteImageView = result.findViewById(R.id.icon_like_imageview);
        buttonCancel = result.findViewById(R.id.button_myreservation);

        buttonCancel.setOnClickListener(this);
    }

    private void myUser(User user) {
        if (Tool.checkIfDateExist(user)) {
            restaurantId = user.getDateLunch().get(Tool.giveDependingDate()).getRestaurantId();
            String restaurantNameString = user.getDateLunch().get(Tool.giveDependingDate()).getRestaurantName();
            initTentative(restaurantId);
            Toast.makeText(getActivity(), restaurantId, Toast.LENGTH_SHORT).show();
            favoriteAddString = restaurantId + "/" + restaurantNameString;
            favorite = Tool.checkFavorite(user.getFavorite(), favoriteAddString);
            updateUiFavorite();
        } else
            hideElements();
    }

    private void updateUiFavorite() {
        if (favorite) {
            favoriteImageView.setImageResource(R.drawable.favorite_true_icons);
        } else {
            favoriteImageView.setImageResource(R.drawable.favorite_false_icons);
        }
    }

    private void hideElements() {
        nothingText.setVisibility(View.VISIBLE);
        this.container.setVisibility(View.GONE);
        buttonCancel.setEnabled(false);
        buttonCancel.setBackgroundColor(ContextCompat.getColor(Objects.requireNonNull(getActivity()), R.color.colorLightGray));
    }

    private void initTentative(String placeId) {

        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
                Place.Field.ADDRESS, Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI,
                Place.Field.PHOTO_METADATAS, Place.Field.TYPES, Place.Field.RATING, Place.Field.PRICE_LEVEL);

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

            if (place.getPriceLevel() != null)
                price = place.getPriceLevel();
            else price = 0;
            if (place.getRating() != null)
                rating = place.getRating();
            else rating = 0;

            switch (price) {
                case 0:
                    price3.setVisibility(View.GONE);
                    price2.setVisibility(View.GONE);
                    price1.setVisibility(View.GONE);
                    break;
                case 1:
                    price3.setVisibility(View.GONE);
                    price2.setVisibility(View.GONE);
                    break;
                case 2:
                    price3.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }

            if (0 < rating && rating < 1.6) {
                ratingBar.setRating(1);
            } else if (rating < 3.2) {
                ratingBar.setRating(2);
            } else if (rating == 0) {
                ratingBar.setVisibility(View.GONE);
            } else if (rating >= 3.2)
                ratingBar.setRating(3);

            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                picRestaurant.setImageBitmap(bitmap);
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    Log.e("TAG", "Place not found: " + exception.getMessage());
                }
            });

            if (place.getName() != null) {
                restaurantName.setText(place.getName());
                keyRestaurant = restaurantId + "/" + place.getName();
            }

            if (place.getAddress() != null)
                restaurantAddress.setText(place.getAddress());
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
                Toast.makeText(getActivity(), getString(R.string.no_number_phone), Toast.LENGTH_SHORT).show();
        });

        website.setOnClickListener(v -> {
            if (websiteUri == null || websiteUri.toString().equals("")) {
                Toast.makeText(getActivity(), getString(R.string.no_website), Toast.LENGTH_SHORT).show();
            } else {
                Intent intentWeb = new Intent(Intent.ACTION_VIEW).setData(websiteUri);
                startActivity(intentWeb);
            }
        });

        mLike.setOnClickListener(v -> {
            if (!favorite) {
                favorite = true;
                updateUiFavorite();
                userViewModel.createFavoriteList(Objects.requireNonNull(getCurrentUser()).getUid(), keyRestaurant);
                Toast.makeText(getActivity(), getString(R.string.add_place_favorite), Toast.LENGTH_SHORT).show();
            } else {
                favorite = false;
                updateUiFavorite();
                userViewModel.deleteFavoriteFromList(Objects.requireNonNull(getCurrentUser()).getUid(), keyRestaurant);
                Toast.makeText(getActivity(), getString(R.string.delete_from_favorite), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.createCallbackToParentActivity();
    }

    @Override
    public void onClick(View v) {
        mCallback.onButtonClickedReservation(v, container, nothingText);
    }

    private void createCallbackToParentActivity() {
        try {
            //Parent activity will automatically subscribe to callback
            mCallback = (OnButtonClickedListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString() + " must implement OnButtonClickedListener");
        }
    }

    @Nullable
    protected FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }
}