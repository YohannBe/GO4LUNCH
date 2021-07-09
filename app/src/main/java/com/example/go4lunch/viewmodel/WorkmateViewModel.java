package com.example.go4lunch.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.RepositoryWorkmates;
import com.example.go4lunch.tool.Tool;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class WorkmateViewModel extends ViewModel {

    private RepositoryWorkmates repositoryWorkmates;

    public WorkmateViewModel(){
        this.repositoryWorkmates = RepositoryWorkmates.getInstance();
    }

    public Task<QuerySnapshot> getWorkmatesLunch() {
        return repositoryWorkmates.getWorkmatesLunch();
    }

    public List<User> updateListUserSort(List<User> originalList) {
        originalList.sort(new Tool.nameAZComparator());
        return originalList;
    }

    public Query getDocumentsQuery(String uid){
        return repositoryWorkmates.getDocumentsQuery(uid);
    }

    public Query getDocumentsQueryLunch(String restaurantId){
        return repositoryWorkmates.getDocumentsQueryLunch(restaurantId);
    }

    public LiveData<List<User>> getDocumentLunch(String restaurantId){
        return repositoryWorkmates.getDocumentLunch(restaurantId);
    }

    public LiveData<List<User>> getDocumentLunchListRestaurant() {
        return repositoryWorkmates.getDocumentLunchListRestaurant();
    }
}
