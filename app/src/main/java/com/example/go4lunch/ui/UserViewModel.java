package com.example.go4lunch.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.model.PlaceData;
import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.RepositoryPlaces;
import com.example.go4lunch.repository.RepositoryUser;
import com.example.go4lunch.repository.RepositoryWorkmates;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;

public class UserViewModel extends ViewModel {

    private final RepositoryUser userDataSource;
    private final RepositoryWorkmates workmateDataSource;
    private final Executor executor;
    private final RepositoryPlaces repositoryPlaces;
    private MutableLiveData<String> restaurantIdLunch = new MutableLiveData<>();
    private MutableLiveData<User> mUser = new MutableLiveData();
    private MutableLiveData<List<User>> allUsers = new MutableLiveData<>();
    private MutableLiveData<List<String>> favoriteList = new MutableLiveData<>();
    public MutableLiveData<List<User>> userFromRestaurant = new MutableLiveData<>();
    private MutableLiveData<List<PlaceData>> placeFetchedList = new MutableLiveData<>();


    public UserViewModel(RepositoryUser userDataSource, RepositoryWorkmates workmateDataSource, Executor executor, RepositoryPlaces repositoryPlaces) {
        this.userDataSource = userDataSource;
        this.workmateDataSource = workmateDataSource;
        this.executor = executor;
        this.repositoryPlaces = repositoryPlaces;
    }

    public void createUser(final User user) {
        userDataSource.createUser(user.getUid(),
                user.getFirstName(), user.getLastName(), user.getUrlPicture());
    }

    public void updateFirstName(String firstName, String userId) {
        userDataSource.updateFirstName(firstName, userId);
    }

    public void updateLastName(String lastName, String userId) {
        userDataSource.updateLastName(lastName, userId);
    }

    public void updatePicUrl(String picUrl, String userId) {
        userDataSource.updatePicture(picUrl, userId);
    }

    public Task<DocumentSnapshot> getUser(String uid) {
        return userDataSource.getUser(uid);
    }

    public MutableLiveData<User> getUserObject(String uid) {
        getUser(uid).addOnSuccessListener(documentSnapshot ->
        {
            User user = documentSnapshot.toObject(User.class);
            mUser.setValue(user);
        });

        return mUser;
    }

    public MutableLiveData<List<String>> getFavoriteList(String uid) {
        this.getUser(uid).addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            if (user.getFavorite() != null) {
                List<String> list = new ArrayList<>();
                list.addAll(user.getFavorite());
                favoriteList.setValue(list);
            }
        });
        return favoriteList;
    }

    public void deleteFavoriteFromList(String uid, String restaurantId) {
        userDataSource.deleteFavoriteFromList(uid, restaurantId);
    }


    public MutableLiveData<String> getLunchId(String uid) {
        getUser(uid).addOnSuccessListener(documentSnapshot ->
        {
            User user = documentSnapshot.toObject(User.class);
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            if (user.getDateLunch() != null) {
                if (user.getDateLunch().get(date) != null) {
                    if (user.getDateLunch().get(date).getRestaurantId() != null) {
                        restaurantIdLunch.setValue(user.getDateLunch().get(date).getRestaurantId());
                    } else restaurantIdLunch.postValue(null);
                } else restaurantIdLunch.postValue(null);
            } else restaurantIdLunch.postValue(null);
        });

        return restaurantIdLunch;
    }


    public void createFavoriteList(String uid, String restaurantId) {
        userDataSource.createFavoriteList(uid, restaurantId);
    }

    public void createLunch(String uid, String restaurantId, String restaurantName, String restaurantType) {
        userDataSource.createLunch(uid, restaurantId, restaurantName, restaurantType);
    }

    public Task<QuerySnapshot> getWorkmatesLunch() {
        return workmateDataSource.getWorkmatesLunch();
    }


    public MutableLiveData<List<User>> getAllUsers() {
        List<String> list = new ArrayList<>();
        List<User> listUsers = new ArrayList<>();
        this.getWorkmatesLunch().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (QueryDocumentSnapshot document : task.getResult()) {
                    list.add(document.getId());
                }

                for (int i = 0; i < list.size(); i++) {
                    this.getUser(list.get(i)).addOnSuccessListener(documentSnapshot -> {
                        User specUser = documentSnapshot.toObject(User.class);
                        listUsers.add(specUser);
                        allUsers.setValue(listUsers);
                    });
                }
            }
        });

        return allUsers;
    }


    public MutableLiveData<List<User>> getAllUsersFromRestaurant(String restaurantId) {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        List<String> list = new ArrayList<>();
        List<User> listUsers = new ArrayList<>();
        this.getWorkmatesLunch().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    list.add(document.getId());
                }
                for (int i = 0; i < list.size(); i++) {
                    this.getUser(list.get(i)).addOnSuccessListener(documentSnapshot -> {
                        User specUser = documentSnapshot.toObject(User.class);
                        if (specUser.getDateLunch() != null) {
                            if (specUser.getDateLunch().get(date) != null) {
                                if (specUser.getDateLunch().get(date).getRestaurantId() != null) {
                                    if (specUser.getDateLunch().get(date).getRestaurantId().equals(restaurantId))
                                        listUsers.add(specUser);
                                }
                            }
                        }
                        userFromRestaurant.setValue(listUsers);
                    });
                }
            }
        });

        return userFromRestaurant;
    }

    private MutableLiveData<List<String>> restaurantChosenId = new MutableLiveData<>();

    public MutableLiveData<List<String>> getAlreadyChosenRestaurant() {

        List<String> users = new ArrayList<>();
        List<String> id = new ArrayList<>();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        this.getWorkmatesLunch().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    users.add(document.getId());
                }
                for (int i = 0; i < users.size(); i++) {
                    getUser(users.get(i)).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            User user = task1.getResult().toObject(User.class);
                            if (user.getDateLunch() != null) {
                                if (user.getDateLunch().get(date) != null) {
                                    if (user.getDateLunch().get(date).getRestaurantId() != null) {
                                        id.add(user.getDateLunch().get(date).getRestaurantId());
                                        restaurantChosenId.setValue(id);
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });

        return restaurantChosenId;
    }

    public void deleteLunch(String uid) {
        userDataSource.deleteLunch(uid);
    }


    public List<User> updateListUserSort(List<User> originalList) {
        originalList.sort(new User.nameAZComparator());
        return originalList;
    }

    public void getPlacesIds(LatLng latLng) {
        repositoryPlaces.getPlacesIds(latLng);
    }

    public MutableLiveData<List<HashMap<String, String>>> getHashMapsIds() {
        return repositoryPlaces.getHashMapsIds();
    }


    public MutableLiveData<List<PlaceData>> sendIdToFetchPlace(List<HashMap<String, String>> hashMaps, Context context, PlacesClient placesClient) {
        List<String> listId = new ArrayList<>();
        List<PlaceData> placeFetchedListNotMutable = new ArrayList<>();
        for (int i = 0; i < hashMaps.size(); i++) {
            HashMap<String, String> hashMapList = hashMaps.get(i);
            String id = hashMapList.get("place_id");
            listId.add(id);
            fetchThePlace(id, placesClient, context, placeFetchedListNotMutable);
        }
        return placeFetchedList;
    }


    public void fetchThePlace(String placeId, PlacesClient placesClient, Context context, List<PlaceData> placeFetchedListNotMutable) {

        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
                Place.Field.ADDRESS, com.google.android.libraries.places.api.model.Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI,
                Place.Field.PHOTO_METADATAS, Place.Field.TYPES, Place.Field.RATING, Place.Field.OPENING_HOURS,
                Place.Field.UTC_OFFSET, Place.Field.LAT_LNG);

        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields)
                .build();

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            PlaceData placeData = new PlaceData(place);
            placeFetchedListNotMutable.add(placeData);

            placeFetchedList.postValue(placeFetchedListNotMutable);

            final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
            final PhotoMetadata photoMetadata = metadata.get(0);

            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .build();

            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                placeFetchedListNotMutable.remove(placeData);
                placeData.setBitmap(bitmap);
                placeFetchedListNotMutable.add(placeData);
                placeFetchedList.postValue(placeFetchedListNotMutable);

            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    final ApiException apiException = (ApiException) exception;
                    Log.e("TAG", "Place not found: " + exception.getMessage());
                    final int statusCode = apiException.getStatusCode();
                }
            });

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
                // Handle error with given status code.
                Log.e("TAG", "Place not found: " + exception.getMessage());
            }
        });
    }


}
