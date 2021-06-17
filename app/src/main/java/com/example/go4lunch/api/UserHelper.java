package com.example.go4lunch.api;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.go4lunch.model.Lunch;
import com.example.go4lunch.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserHelper {

    public static final String COLLECTION_USER = "users";

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_USER);
    }

    public Task<Void> createUser(String uid, String firstName, String lastName, String urlPicture) {
        User userToCreate = new User(uid, firstName, lastName, urlPicture);
        return UserHelper.getUsersCollection()
                .document(uid)
                .set(userToCreate);
    }

    public Task<Void> createLunchUser(String uid, String restaurantId){

        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Lunch lunchToCreate = new Lunch(restaurantId, uid);
        return UserHelper.getUsersCollection()
                .document(uid)
                .update(date, lunchToCreate);

    }



    public Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
    }

    public Task<Void> updateFirstName(String firstName, String uid){
        return UserHelper.getUsersCollection().document(uid).update("firstName", firstName);
    }
    public Task<Void> updateLastName(String lastName, String uid){
        return UserHelper.getUsersCollection().document(uid).update("lastName", lastName);
    }

    public Task<Void> updatePic(String urlPic, String uid){
        return UserHelper.getUsersCollection().document(uid).update("urlPicture", urlPic);
    }

    public Task<Void> deleteUser(String uid){
        return UserHelper.getUsersCollection().document(uid).delete();
    }
}
