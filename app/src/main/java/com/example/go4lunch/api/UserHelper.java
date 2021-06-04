package com.example.go4lunch.api;

import com.example.go4lunch.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserHelper {

    public static final String COLLECTION_USER = "users";

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_USER);
    }


    public static Task<Void> createUser(String uid, String firstName, String lastName, String urlPicture) {
        // 1 - Create User object
        User userToCreate = new User(uid, firstName, lastName, urlPicture);
        // 2 - Add a new User Document to Firestore
        return UserHelper.getUsersCollection()
                .document(uid) // Setting uID for Document
                .set(userToCreate); // Setting object for Document
    }

    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
    }

    public static Task<Void> updateFirstName(String firstName, String uid){
        return UserHelper.getUsersCollection().document(uid).update("first name", firstName);
    }
    public static Task<Void> updateLastName(String lastName, String uid){
        return UserHelper.getUsersCollection().document(uid).update("last name", lastName);
    }

    public static Task<Void> updatePic(String urlPic, String uid){
        return UserHelper.getUsersCollection().document(uid).update("picture", urlPic);
    }

    public static Task<Void> deleteUser(String uid){
        return UserHelper.getUsersCollection().document(uid).delete();
    }
}
