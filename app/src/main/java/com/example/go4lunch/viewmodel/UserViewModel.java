package com.example.go4lunch.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.RepositoryUser;
import com.example.go4lunch.tool.Tool;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class UserViewModel extends ViewModel {

    private final RepositoryUser repositoryUser;
    private final MutableLiveData<User> mUser = new MutableLiveData<>();
    private final MutableLiveData<List<String>> favoriteList = new MutableLiveData<>();

    public UserViewModel() {
        this.repositoryUser = RepositoryUser.getInstance();
    }

    public void createUser(final User user) {
        this.repositoryUser.createUser(user.getUid(),
                user.getFirstName(), user.getLastName(), user.getUrlPicture());
    }

    public void updateFirstName(String firstName, String userId) {
        repositoryUser.updateFirstName(firstName, userId);
    }

    public void updateLastName(String lastName, String userId) {
        repositoryUser.updateLastName(lastName, userId);
    }

    public void updatePicUrl(String picUrl, String userId) {
        repositoryUser.updatePicture(picUrl, userId);
    }

    public Task<DocumentSnapshot> getUser(String uid) {
        return repositoryUser.getUser(uid);
    }

    public LiveData<User> getUserObject(String uid) {
        getUser(uid).addOnSuccessListener(documentSnapshot ->
        {
            User user = documentSnapshot.toObject(User.class);
            mUser.setValue(user);
        });

        return mUser;
    }

    public LiveData<List<String>> getFavoriteList(String uid) {
        this.getUser(uid).addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            assert user != null;
            if (user.getFavorite() != null) {
                List<String> list = user.getFavorite();
                favoriteList.setValue(list);
            }
        });
        return favoriteList;
    }

    public void deleteFavoriteFromList(String uid, String restaurantId) {
        repositoryUser.deleteFavoriteFromList(uid, restaurantId);
    }

    public void createFavoriteList(String uid, String restaurantId) {
        repositoryUser.createFavoriteList(uid, restaurantId);
    }

    public void createLunch(String uid, String restaurantId, String restaurantName, String restaurantType, String address) {
        repositoryUser.createLunch(uid, restaurantId, restaurantName, restaurantType, address);
    }

    public void deleteLunch(String uid) {
        repositoryUser.deleteLunch(uid);
    }


}
