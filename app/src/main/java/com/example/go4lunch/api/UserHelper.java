package com.example.go4lunch.api;

import com.example.go4lunch.model.Lunch;
import com.example.go4lunch.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UserHelper {

    public static final String COLLECTION_USER = "users";

    public static CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_USER);
    }

    public Task<Void> createUser(String uid, String firstName, String lastName, String urlPicture) {
        User userToCreate = new User(uid, firstName, lastName, urlPicture);
        return UserHelper.getUsersCollection()
                .document(uid)
                .set(userToCreate);
    }


    public void createLunchUser(String uid, String restaurantId, String restaurantName, String restaurantType) {
        this.getUser(uid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                Map<String, Object> updates = new HashMap<>();
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                Lunch lunchToCreate = new Lunch(restaurantId, uid, restaurantName, restaurantType);
                DocumentReference docRef = UserHelper.getUsersCollection().document(uid);
                if (user.getDateLunch() != null) {
                    updates.putAll(user.getDateLunch());
                }
                updates.put(date, lunchToCreate);
                docRef.update("dateLunch", updates);
            }
        });

    }


    public void createFavoriteList(String uid, String restaurantId) {
        DocumentReference docRef = UserHelper.getUsersCollection().document(uid);
        this.getUser(uid).addOnSuccessListener(documentSnapshot -> {
            ArrayList<String> update = new ArrayList<>();
            User user = documentSnapshot.toObject(User.class);
            if (user.getFavorite() != null) {
                update.addAll(user.getFavorite());
                if (user.getFavorite() != null) {
                    if (!user.getFavorite().contains(restaurantId)) {
                        update.add(restaurantId);
                    }
                }
            } else {
                update.add(restaurantId);
            }
            docRef.update("favorite", update);
        });
    }

    public void deleteFavoriteFromList(String uid, String restaurantId) {
        DocumentReference docRef = UserHelper.getUsersCollection().document(uid);
        this.getUser(uid).addOnSuccessListener(documentSnapshot -> {
            ArrayList<String> update = new ArrayList<>();
            User user = documentSnapshot.toObject(User.class);
            if (user.getFavorite() != null)
                update.addAll(user.getFavorite());
            if (user.getFavorite().contains(restaurantId)) {
                update.remove(restaurantId);
                docRef.update("favorite", update);
            }
        });
    }

    public void deleteReservation(String uid) {
        this.getUser(uid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                User user = documentSnapshot.toObject(User.class);
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                DocumentReference docRef = UserHelper.getUsersCollection().document(uid);
                Map<String, Object> updates = new HashMap<>();
                if (user.getDateLunch() != null) {
                    updates.putAll(user.getDateLunch());
                }
                updates.remove(date);

                docRef.update("dateLunch", updates);
            }
        });
    }

    public Task<DocumentSnapshot> getUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).get();
    }

    public Task<Void> updateFirstName(String firstName, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("firstName", firstName);
    }

    public Task<Void> updateLastName(String lastName, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("lastName", lastName);
    }

    public Task<Void> updatePic(String urlPic, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("urlPicture", urlPic);
    }

    public Task<Void> deleteUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).delete();
    }
}
