package com.example.go4lunch.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.model.PlaceData;
import com.example.go4lunch.model.User;
import com.example.go4lunch.tool.Tool;
import com.example.go4lunch.ui.DetailRestaurant;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class RecyclerViewAdapterListRestaurant extends RecyclerView.Adapter<RecyclerViewAdapterListRestaurant.ViewHolder> {


    private final Context context;
    private List<PlaceData> dataList = new ArrayList<>();
    private LatLng myPosition;
    private List<User> mUserList;

    public RecyclerViewAdapterListRestaurant(Context context) {
        this.context = context;
    }

    public void updateRestaurantList(List<PlaceData> dataList, LatLng myPosition, List<User> mUserList) {
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

        holder.name.setText(dataList.get(position).getPlace().getName());
        holder.address.setText(dataList.get(position).getPlace().getAddress());
        String distance = "";
        if (myPosition != null)
            distance = Tool.calculusDistanceBetweenPoints(Objects.requireNonNull(myPosition), Objects.requireNonNull(dataList.get(position).getPlace().getLatLng())) + "m";
        holder.distance.setText(distance);
        holder.picture.setImageBitmap(dataList.get(position).getBitmap());


        double rating;

        if (dataList.get(position).getPlace().getRating() != null) {
            rating = dataList.get(position).getPlace().getRating();
        }
        else {
            rating = 0;
        }

        if (0 < rating && rating < 1.6) {
            holder.ratingBar.setRating(1);
        } else if (rating < 3.2) {
            holder.ratingBar.setRating(2);
        } else if (rating == 0) {
            holder.ratingBar.setVisibility(View.GONE);
        } else if (rating >=3.2)
            holder.ratingBar.setRating(3);

        if (dataList.get(position).getPlace().getOpeningHours() != null) {
            Objects.requireNonNull(dataList.get(position).getPlace().getOpeningHours()).getWeekdayText();
            String day = new SimpleDateFormat("EEEE", Locale.getDefault()).format(new Date());
            int getDay = 0;

            switch (day){
                case "Tuesday":
                    getDay = 1;
                    break;
                case "Wednesday":
                    getDay = 2;
                    break;
                case "Thursday":
                    getDay = 3;
                    break;
                case "Friday":
                    getDay = 4;
                    break;
                case "Saturday":
                    getDay = 5;
                    break;
                case "Sunday":
                    getDay = 6;
                    break;
            }
            holder.schedules.setText(Objects.requireNonNull(dataList.get(position).getPlace().getOpeningHours()).getWeekdayText().get(getDay));
        }


        int personGoingToRestaurant = Tool.checkExistingLunch(mUserList, dataList.get(position).getPlace().getId());

        String personGoingToRestaurantString = "(" + personGoingToRestaurant + ")";

        if (personGoingToRestaurant != 0)
            holder.persons.setText(personGoingToRestaurantString);
        else {
            holder.persons.setVisibility(View.INVISIBLE);
        }

        holder.parentLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailRestaurant.class);
            intent.putExtra("restaurantId", dataList.get(position).getPlace().getId());
            intent.putExtra("parent", false);
            context.startActivity(intent);
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
