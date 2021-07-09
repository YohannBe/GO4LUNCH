package com.example.go4lunch.tool;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.model.User;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class Tool {
    static SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    public static String transformDateToString(long time) {
        return formatter.format(new Date((time)));
    }

    public static boolean checkDateMessages(String dateVisibility, long time) {
        return dateVisibility.equals(formatter.format(new Date((time))));
    }

    public static boolean checkFavorite(List<String> list, String restaurantId) {
        if (list != null)
            return list.contains(restaurantId);
        else return false;
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

    public static int calculusDistanceBetweenPoints(LatLng first, LatLng second) {

        Location A = new Location("");
        A.setLatitude(first.latitude);
        A.setLongitude(first.longitude);

        Location B = new Location("");
        B.setLatitude(second.latitude);
        B.setLongitude(second.longitude);

        int distance = (int) A.distanceTo(B);

        return distance;
    }

    public static String giveDependingDate() {
        String date;
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        if (currentHour <= 12) {
            date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        } else {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            date = formatter.format(calendar.getTime());
        }
        return date;
    }

    public static int checkExistingLunch(List<User> userList, String idRestaurant) {
        int count = 0;
        for (int i = 0; i < userList.size(); i++) {
            if (checkIdDateRestaurantExist(userList.get(i), idRestaurant)) {
                count++;
            }
        }
        return count;
    }

    public static String nameToUpperCase(String name) {
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        return name;
    }

    public static class nameAZComparator implements Comparator<User> {
        @Override
        public int compare(User left, User right) {
            return left.getFirstName().compareTo(right.getFirstName());
        }
    }

    public static class IdAZComparator implements Comparator<String> {
        @Override
        public int compare(String uidSender, String uidReceiver) {
            return uidSender.compareTo(uidReceiver);
        }
    }

    public static void updatePictureGlide(ImageView imageView, String emplacement, Context context) {
        FirebaseStorage.getInstance().getReference(emplacement).getDownloadUrl()
                .addOnSuccessListener(uri -> Glide.with(context)
                        .load(uri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageView));
    }

    public static String giveActualDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    public static String getActualHour(Date date) {
        if (date != null)
            return new SimpleDateFormat("HH:mm").format(date);
        else
            return new SimpleDateFormat("HH:mm").format(new Date());
    }

    public static void loadLanguage(Context context, Activity activity, boolean setting) {
        SharedPreferences preferences = context.getSharedPreferences("languages", MODE_PRIVATE);
        String language = preferences.getString("language_choice", null);
        setLocal(language, context, activity, setting);
    }

    public static void setLocal(String languageChosen, Context context, Activity activity, boolean setting) {

        if (languageChosen != null && !languageChosen.equals("Default")) {
            if (setting) {
                SharedPreferences.Editor editor = context.getSharedPreferences("languages", MODE_PRIVATE).edit();
                editor.putString("language_choice", languageChosen);
                editor.apply();
            }

            switch (languageChosen) {
                case "Français":
                    languageChosen = "fr";
                    break;
                case "English":
                    languageChosen = "en";
                    break;
                default:
                    break;
            }

            Locale locale = new Locale(languageChosen);
            Locale.setDefault(locale);
            Configuration configuration = new Configuration();
            configuration.setLocale(locale);
            activity.getBaseContext().getResources().updateConfiguration(configuration, activity.getBaseContext().getResources().getDisplayMetrics());
        }
    }

    public static boolean checkIfDateExist(User user) {
        if (user.getDateLunch() != null) {
            if (user.getDateLunch().get(giveDependingDate()) != null) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkIdDateRestaurantExist(User user, String restaurantId) {
        if (checkIfDateExist(user)) {
            if (user.getDateLunch().get(giveDependingDate()).getRestaurantId().equals(restaurantId)) {
                return true;
            } else
                return false;
        } else
            return false;
    }

}
