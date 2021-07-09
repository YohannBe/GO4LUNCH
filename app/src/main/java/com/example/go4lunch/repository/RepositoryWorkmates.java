package com.example.go4lunch.repository;

import androidx.lifecycle.LiveData;

import com.example.go4lunch.api.WorkmateHelper;
import com.example.go4lunch.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RepositoryWorkmates {

    private final WorkmateHelper workmateHelper;
    private static volatile RepositoryWorkmates instance;

    public static RepositoryWorkmates getInstance() {
        RepositoryWorkmates result = instance;
        if (result != null) {
            return result;
        }
        synchronized (RepositoryWorkmates.class) {
            if (instance == null) {
                instance = new RepositoryWorkmates();
            }return instance;
        }
    }


    public RepositoryWorkmates() {
        workmateHelper = WorkmateHelper.getInstance();
    }

    public Task<QuerySnapshot> getWorkmatesLunch() {
        return workmateHelper.getWorkmatesLunch();
    }


    public Query getDocumentsQuery(String uid){
        return workmateHelper.getDocumentsQuery(uid);
    }

    public Query getDocumentsQueryLunch(String restaurantId){
        return workmateHelper.getDocumentsQueryLunch(restaurantId);
    }

    public LiveData<List<User>> getDocumentLunch(String restaurantId){
        return workmateHelper.getDocumentLunch(restaurantId);
    }

    public LiveData<List<User>> getDocumentLunchListRestaurant() {
        return workmateHelper.getDocumentLunchListRestaurant();
    }
}
