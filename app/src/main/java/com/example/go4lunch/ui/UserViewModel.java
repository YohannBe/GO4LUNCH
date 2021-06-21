package com.example.go4lunch.ui;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.RepositoryUser;
import com.example.go4lunch.repository.RepositoryWorkmates;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class UserViewModel extends ViewModel {

    private final RepositoryUser userDataSource;
    private final RepositoryWorkmates workmateDataSource;
    private final Executor executor;

    public UserViewModel(RepositoryUser userDataSource, RepositoryWorkmates workmateDataSource, Executor executor) {
        this.userDataSource = userDataSource;
        this.workmateDataSource = workmateDataSource;
        this.executor = executor;
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

    public Task<Void> createLunch(String uid, String restaurantId) {
        return userDataSource.createLunch(uid, restaurantId);
    }

    public Task<QuerySnapshot> getWorkmatesLunch() {
        return workmateDataSource.getWorkmatesLunch();
    }

    public MutableLiveData<List<User>> allUsers = new MutableLiveData<>();

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



}
