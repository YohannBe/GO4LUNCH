package com.example.go4lunch.repository;

import com.example.go4lunch.api.UserHelper;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class RepositoryUser {

    private final UserHelper userHelper;

    public RepositoryUser() {
        userHelper = new UserHelper();
    }

    public Task<DocumentSnapshot> getUser(String uid){
        return userHelper.getUser(uid);
    }

    public Task<Void> createUser(String uid, String firstName, String lastName, String urlPicture){
        return userHelper.createUser(uid, firstName, lastName, urlPicture);
    }

    public Task<Void> updateFirstName(String firstName, String uid){
        return userHelper.updateFirstName(firstName, uid);
    }

    public Task<Void> updateLastName(String lastName, String uid){
        return userHelper.updateLastName(lastName, uid);
    }
    public Task<Void> updatePicture(String picUrl, String uid){
        return userHelper.updatePic(picUrl, uid);
    }

    public void createLunch(String uid, String restaurantId, String restaurantName, String restaurantType){
        userHelper.createLunchUser(uid, restaurantId, restaurantName, restaurantType);
    }

    public void deleteFavoriteFromList(String uid, String restaurantId){
        userHelper.deleteFavoriteFromList(uid, restaurantId);
    }

    public void deleteLunch(String uid){
        userHelper.deleteReservation(uid);
    }

    public void createFavoriteList(String uid, String restaurantId){
        userHelper.createFavoriteList(uid, restaurantId);
    }


}
