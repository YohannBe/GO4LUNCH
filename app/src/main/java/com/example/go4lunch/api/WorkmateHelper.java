package com.example.go4lunch.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.Lunch;
import com.example.go4lunch.model.User;
import com.example.go4lunch.tool.Tool;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.go4lunch.api.UserHelper.COLLECTION_USER;

public class WorkmateHelper {


    private static volatile WorkmateHelper instance;
    private MutableLiveData<List<User>> allUsersObject = new MutableLiveData<>();
    private MutableLiveData<List<User>> allUsersObjectRestaurant = new MutableLiveData<>();

    public WorkmateHelper() {
    }

    public static WorkmateHelper getInstance() {
        WorkmateHelper result = instance;
        if (result != null) {
            return result;
        }
        synchronized (WorkmateHelper.class) {
            if (instance == null) {
                instance = new WorkmateHelper();
            }
            return instance;
        }
    }


    public Task<QuerySnapshot> getWorkmatesLunch() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_USER).get();
    }

    /*public LiveData<List<User>> getListUserObject() {
        this.getWorkmatesLunch().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    User user = document.toObject(User.class);
                    userList.add(user);
                }
                allUsersObject.setValue(userList);
            }
        });
        return allUsersObject;
    }*/

    public LiveData<List<User>> getDocumentLunch(String restaurantId) {
        FirebaseFirestore.getInstance()
                .collection(COLLECTION_USER)
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        List<User> userList = new ArrayList<>();
                        List<DocumentSnapshot> snapshotList = value.getDocuments();
                        for (DocumentSnapshot snapshot : snapshotList) {
                            User user = snapshot.toObject(User.class);
                            if (Tool.checkIdDateRestaurantExist(user, restaurantId)) {
                                userList.add(user);
                            }
                        }
                        allUsersObjectRestaurant.setValue(userList);
                    }
                });
        return allUsersObjectRestaurant;
    }

    public LiveData<List<User>> getDocumentLunchListRestaurant() {
        FirebaseFirestore.getInstance()
                .collection(COLLECTION_USER)
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        List<User> userList = new ArrayList<>();
                        List<DocumentSnapshot> snapshotList = value.getDocuments();
                        for (DocumentSnapshot snapshot : snapshotList) {
                            User user = snapshot.toObject(User.class);
                            if (Tool.checkIfDateExist(user)) {
                                userList.add(user);
                            }
                        }
                        allUsersObject.setValue(userList);
                    }
                });
        return allUsersObject;
    }

    public Query getDocumentsQuery(String uid) {
        return FirebaseFirestore.getInstance().collection(COLLECTION_USER)
                .whereNotEqualTo("uid", uid);
    }

    public Query getDocumentsQueryLunch(String restaurantId) {
        return FirebaseFirestore.getInstance().collection(COLLECTION_USER)
                .whereArrayContains("restaurantId", restaurantId);
    }


}
