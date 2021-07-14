package com.example.go4lunch.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.model.User;
import com.example.go4lunch.model.placeModel.ResultPlaces;
import com.example.go4lunch.tool.Tool;
import com.example.go4lunch.ui.DetailRestaurant;
import com.example.go4lunch.viewmodel.PlaceViewModel;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class RecyclerViewAdapterListRestaurant extends RecyclerView.Adapter<RecyclerViewAdapterListRestaurant.ViewHolder> {


    private final Context context;
    private List<ResultPlaces> dataList = new ArrayList<>();
    private LatLng myPosition;
    private List<User> mUserList;
    private PlaceViewModel placeViewModel;
    private PlacesClient placesClient;


    public RecyclerViewAdapterListRestaurant(Context context, PlaceViewModel placeViewModel, PlacesClient placesClient) {
        this.context = context;
        this.placeViewModel = placeViewModel;
        this.placesClient = placesClient;
    }

    public void updateRestaurantList(List<ResultPlaces> dataList, LatLng myPosition, List<User> mUserList) {
        this.myPosition = myPosition;
        this.dataList = dataList;
        this.mUserList = mUserList;
        notifyDataSetChanged();
    }

    /**
     * method responsible for inflating the view
     */
    @NonNull
    @Override
    public RecyclerViewAdapterListRestaurant.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restautant_item_layout, parent, false);
        RecyclerViewAdapterListRestaurant.ViewHolder holder = new RecyclerViewAdapterListRestaurant.ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterListRestaurant.ViewHolder holder, int position) {
        holder.parentLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailRestaurant.class);
            intent.putExtra("restaurantId", dataList.get(position).getPlaceId());
            intent.putExtra("parent", false);
            context.startActivity(intent);
        });

        /*
        Tool.buildRetrofit(dataList.get(position).getPlaceId(), holder.name, holder.picture,
                holder.ratingBar, holder.address, context, holder.schedules, myPosition, holder.distance,
                Tool.checkExistingLunch(mUserList, dataList.get(position).getPlaceId()), holder.persons);
                */

        int count = Tool.checkExistingLunch(mUserList, dataList.get(position).getPlaceId());

        String personGoingToRestaurantString = "(" + count + ")";

        if (count != 0) {
            holder.persons.setVisibility(View.VISIBLE);
            holder.persons.setText(personGoingToRestaurantString);
        }

        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
                Place.Field.ADDRESS, com.google.android.libraries.places.api.model.Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI,
                Place.Field.PHOTO_METADATAS, Place.Field.TYPES, Place.Field.RATING, Place.Field.OPENING_HOURS,
                Place.Field.UTC_OFFSET, Place.Field.LAT_LNG);

        FetchPlaceRequest request = FetchPlaceRequest.builder(dataList.get(position).getPlaceId(), placeFields)
                .build();

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();

            holder.name.setText(place.getName());
            holder.address.setText(place.getAddress());
            holder.schedules.setText(Tool.buildSentenceOpening(place, context));
            String distanceString = "";
            LatLng latLng = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
            if (myPosition != null)
                distanceString = Tool.calculusDistanceBetweenPoints(Objects.requireNonNull(myPosition), Objects.requireNonNull(latLng)) + "m";
            holder.distance.setText(distanceString);


            double rating;
            if (place.getRating() != null) {
                rating = place.getRating();
                if (0 < rating && rating < 1.6) {
                    holder.ratingBar.setRating(1);
                } else if (rating < 3.2) {
                    holder.ratingBar.setRating(3);
                } else if (rating == 0) {
                    holder.ratingBar.setVisibility(View.GONE);
                } else if (rating >= 3.2)
                    holder.ratingBar.setRating(5);
            }

            final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
            assert metadata != null;
            final PhotoMetadata photoMetadata = metadata.get(0);

            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .build();

            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                holder.picture.setImageBitmap(fetchPhotoResponse.getBitmap());
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


    @Override
    public int getItemCount() {
        if (dataList == null)
            return 0;
        else
            return dataList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView picture;
        TextView name, address, schedules, distance, persons;
        ConstraintLayout parentLayout;
        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name_restaurant_item);
            address = itemView.findViewById(R.id.address_restaurant_item);
            schedules = itemView.findViewById(R.id.schedules_restaurant_item);
            distance = itemView.findViewById(R.id.distance_restaurant_item);
            persons = itemView.findViewById(R.id.quantity_persons_restaurant_item);
            ratingBar = itemView.findViewById(R.id.ratingBar_list);
            picture = itemView.findViewById(R.id.picture_restaurant_item_actual);
            parentLayout = itemView.findViewById(R.id.restaurant_list_recyclerview_layout);
        }
    }

}
