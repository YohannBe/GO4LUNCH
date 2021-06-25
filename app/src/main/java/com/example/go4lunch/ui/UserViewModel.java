package com.example.go4lunch.ui;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.RepositoryUser;
import com.example.go4lunch.repository.RepositoryWorkmates;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;

public class UserViewModel extends ViewModel {

    private final RepositoryUser userDataSource;
    private final RepositoryWorkmates workmateDataSource;
    private final Executor executor;
    private MutableLiveData<String> restaurantIdLunch = new MutableLiveData<>();
    private MutableLiveData<User> mUser = new MutableLiveData();
    private MutableLiveData<List<User>> allUsers = new MutableLiveData<>();
    private MutableLiveData<List<String>> favoriteList = new MutableLiveData<>();

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

    //public User user = new User();

    public MutableLiveData<User> getUserObject(String uid) {
        getUser(uid).addOnSuccessListener(documentSnapshot ->
        {
            User user = documentSnapshot.toObject(User.class);
            mUser.setValue(user);
        });

        return mUser;
    }

    public MutableLiveData<List<String>> getFavoriteList(String uid){
     this.getUser(uid).addOnSuccessListener(documentSnapshot -> {
         User user = documentSnapshot.toObject(User.class);
         if (user.getFavorite() != null){
             List<String> list = new ArrayList<>();
             list.addAll(user.getFavorite());
             favoriteList.setValue(list);
         }
     });
     return favoriteList;
    }

    public void deleteFavoriteFromList(String uid, String restaurantId){
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

    public MutableLiveData<List<User>> userFromRestaurant = new MutableLiveData<>();

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

    public void deleteLunch(String uid) {
        userDataSource.deleteLunch(uid);
    }

    public Task<QuerySnapshot> getLunchIfExist(String uid, String restaurantId) {
        return userDataSource.getLunchIfExist(uid, restaurantId);
    }

    public MutableLiveData<List<String>> allUsersCheckedInSpecRestaurant = new MutableLiveData<>();

    public MutableLiveData<List<String>> getAlreadyCheckedRestaurant(String uid, String restaurantId) {
        List<String> list = new ArrayList<>();
        this.getLunchIfExist(uid, restaurantId).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                    list.add(queryDocumentSnapshots.getDocuments().get(i).getId());
                }
            }
        });
        return allUsersCheckedInSpecRestaurant;
    }

    public List<User> updateListUserSort(List<User> originalList) {
        originalList.sort(new User.nameAZComparator());
        return originalList;
    }


}
