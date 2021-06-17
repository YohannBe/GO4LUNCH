package com.example.go4lunch.repository;

import com.example.go4lunch.api.WorkmateHelper;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RepositoryWorkmates {

    private final WorkmateHelper workmateHelper;


    public RepositoryWorkmates() {
        workmateHelper = new  WorkmateHelper();
    }

    public Task<QuerySnapshot> getWorkmatesLunch(){
        return workmateHelper.getWorkmatesLunch();
    }
}
