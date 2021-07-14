package com.example.go4lunch.tool;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.api.interfaceApi.PlacesInterfaceApi;
import com.example.go4lunch.api.interfaceApi.PlacesListInterfaceApi;
import com.example.go4lunch.model.placeModel.Feed;
import com.example.go4lunch.model.User;
import com.example.go4lunch.model.placeModel.ListFeed;
import com.example.go4lunch.model.placeModel.ResultPlaces;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static MarkerOptions createMarker(ResultPlaces resultPlaces, boolean filled, Context context) {

        LatLng latLng = new LatLng(resultPlaces.getGeometry().getLocation().getLat(),
                resultPlaces.getGeometry().getLocation().getLng());
        String name = resultPlaces.getName();

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
            }
        }
        return false;
    }


    public static String updateListUserSort(String currentUserId, String mSecondUserId) {
        List<String> originalList = new ArrayList<>();
        originalList.add(currentUserId);
        originalList.add(mSecondUserId);
        originalList.sort(new Tool.IdAZComparator());
        return originalList.get(0) + originalList.get(1);
    }

    public static void buildRetrofit(String idRestaurant, TextView name, ImageView picture, RatingBar ratingBar,
                                     TextView address, Context context, TextView schedules, LatLng myPosition,
                                     TextView distance, int count, TextView persons) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/place/details/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PlacesInterfaceApi placesInterfaceApi = retrofit.create(PlacesInterfaceApi.class);
        Call<Feed> call = placesInterfaceApi.getData(idRestaurant,
                "rating,photos,opening_hours,geometry,formatted_address,name",
                BuildConfig.MAPS_API_KEY);

        String personGoingToRestaurantString = "(" + count + ")";

        if (count != 0) {
            persons.setVisibility(View.VISIBLE);
            persons.setText(personGoingToRestaurantString);
        }

        call.enqueue(new Callback<Feed>() {
            @Override
            public void onResponse(Call<Feed> call, Response<Feed> response) {
                Feed feed = response.body();
                Log.d("tentative", feed.getData().getAddress());
                Log.d("tentative", String.valueOf(count));

                name.setText(feed.getData().getName());
                address.setText(feed.getData().getAddress());

                if (feed.getData().getPhotos() != null) {
                    String urlPhoto = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" +
                            feed.getData().getPhotos().get(0).getPhotoReference()
                            + "&key=" + BuildConfig.MAPS_API_KEY;

                    Glide.with(context)
                            .load(urlPhoto)
                            .into(picture);
                }

                double rating;
                rating = feed.getData().getRating();
                if (0 < rating && rating < 1.6) {
                    ratingBar.setRating(1);
                } else if (rating < 3.2) {
                    ratingBar.setRating(3);
                } else if (rating == 0) {
                    ratingBar.setVisibility(View.GONE);
                } else if (rating >= 3.2)
                    ratingBar.setRating(5);

                if (checkOpeningHours(feed)) {
                    int openHour = Integer.parseInt(feed.getData().getOpening_hours().getPeriods().get(0).getOpen().getTime().substring(0, 2));
                    int openMinute = Integer.parseInt(feed.getData().getOpening_hours().getPeriods().get(0).getOpen().getTime().substring(3));
                    int closedHour = Integer.parseInt(feed.getData().getOpening_hours().getPeriods().get(0).getClose().getTime().substring(0, 2));
                    int closedMinute = Integer.parseInt(feed.getData().getOpening_hours().getPeriods().get(0).getClose().getTime().substring(3));

                    Calendar rightNow = Calendar.getInstance();
                    int hour = rightNow.get(Calendar.HOUR_OF_DAY);
                    int minute = rightNow.get(Calendar.MINUTE);
                    if (closedHour - hour == 0 && closedMinute - minute > 0)
                        schedules.setText("Closing soon");
                    else if (hour < closedHour && hour > openHour)
                        schedules.setText("Open");
                    else if ((hour > closedHour && hour <= 23) || hour < openHour)
                        schedules.setText("Closed");
                } else schedules.setText("No data");

                String distanceString = "";
                LatLng latLng = new LatLng(feed.getData().getGeometry().getLocation().getLat(), feed.getData().getGeometry().getLocation().getLng());
                if (myPosition != null)
                    distanceString = Tool.calculusDistanceBetweenPoints(Objects.requireNonNull(myPosition), Objects.requireNonNull(latLng)) + "m";
                distance.setText(distanceString);


            }

            @Override
            public void onFailure(Call<Feed> call, Throwable t) {

            }
        });
    }


    public static void buildRetrofitNearby(LatLng myPosition) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PlacesListInterfaceApi placesListInterfaceApi = retrofit.create(PlacesListInterfaceApi.class);
        Call<ListFeed> call = placesListInterfaceApi.getData(myPosition.latitude + "," + myPosition.longitude,
                "1500", "restaurant",
                BuildConfig.MAPS_API_KEY);

        call.enqueue(new Callback<ListFeed>() {
            @Override
            public void onResponse(Call<ListFeed> call, Response<ListFeed> response) {
                ListFeed listFeed = response.body();

                listFeed.getResults();
                for (int i = 0; i < listFeed.getResults().size(); i++) {
                    Log.d("tentative", listFeed.getResults().get(i).getName());
                }


            }

            @Override
            public void onFailure(Call<ListFeed> call, Throwable t) {

            }
        });
    }


    public static boolean checkOpeningHours(Feed feed) {
        if (feed.getData().getOpening_hours() != null) {
            if (feed.getData().getOpening_hours().getOpenNow() != null) {
                if (feed.getData().getOpening_hours().getPeriods() != null) {
                    if (feed.getData().getOpening_hours().getPeriods().get(0).getOpen() != null) {
                        if (feed.getData().getOpening_hours().getPeriods().get(0).getClose() != null) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean checkOpeningHoursPlace(Place place) {
        if (place.getOpeningHours() != null) {
            if (place.getOpeningHours().getPeriods() != null) {
                if (place.getOpeningHours().getPeriods().get(0).getOpen() != null) {
                    if (place.getOpeningHours().getPeriods().get(0).getClose() != null) {
                        return true;
                    }
                }

            }
        }
        return false;
    }

    public static String buildSentenceOpening(Place place, Context context) {
        String newSentence = "";
        if (checkOpeningHoursPlace(place)) {

            int openHour = place.getOpeningHours().getPeriods().get(0).getOpen().getTime().getHours();
            int openMinute = place.getOpeningHours().getPeriods().get(0).getOpen().getTime().getMinutes();
            int closedHour = place.getOpeningHours().getPeriods().get(0).getClose().getTime().getHours();
            int closedMinute = place.getOpeningHours().getPeriods().get(0).getClose().getTime().getMinutes();

            Calendar rightNow = Calendar.getInstance();
            int hour = rightNow.get(Calendar.HOUR_OF_DAY);
            int minute = rightNow.get(Calendar.MINUTE);
            if (closedHour - hour == 0 && closedMinute - minute > 0)
                newSentence = context.getString(R.string.closingsoon);
            else if (hour < closedHour && hour > openHour)
                newSentence = context.getString(R.string.openstring);
            else if ((hour > closedHour && hour <= 23) || hour < openHour)
                newSentence = context.getString(R.string.closed_string);
        } else newSentence = context.getString(R.string.no_data);
        return newSentence;
    }
}
