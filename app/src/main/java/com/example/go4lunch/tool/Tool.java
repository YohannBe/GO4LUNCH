package com.example.go4lunch.tool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;

import androidx.core.content.ContextCompat;

import com.example.go4lunch.R;
import com.example.go4lunch.model.User;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Tool {

    public static String transformDateToString() {
        return null;
    }

    public static boolean checkFavorite(List<String> list, String restaurantId) {
        return list.contains(restaurantId);
    }

    public static BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static MarkerOptions createMarker(HashMap<String, String> hashMapList, boolean filled, Context context) {

        LatLng latLng = new LatLng(Double.parseDouble(hashMapList.get("lat")),
                Double.parseDouble(hashMapList.get("lng")));
        String name = hashMapList.get("name");

        MarkerOptions options = new MarkerOptions();

        options.position(latLng);
        options.title(name);
        if (filled)
            options.icon(Tool.BitmapFromVector(context, R.drawable.restaurant_filled));
        else
            options.icon(Tool.BitmapFromVector(context, R.drawable.restaurant_empty));

        return options;
    }

    public static int calculusDistanceBetweenPoints(LatLng first, LatLng second){

        Location A = new Location("");
        A.setLatitude(first.latitude);
        A.setLongitude(first.longitude);

        Location B = new Location("");
        B.setLatitude(second.latitude);
        B.setLongitude(second.longitude);

        int distance = (int) A.distanceTo(B);

        return distance;
    }

    public static int checkExistingLunch(List<User> userList, String idRestaurant){

        int count = 0;
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        for (int i = 0; i<userList.size(); i++){
            if (userList.get(i).getDateLunch() != null){
                if (userList.get(i).getDateLunch().get(date) != null){
                    if (userList.get(i).getDateLunch().get(date).getRestaurantId() != null){
                        if (userList.get(i).getDateLunch().get(date).getRestaurantId().contains(idRestaurant))
                            count++;
                    }
                }
            }
        }

        return count;
    }


}
