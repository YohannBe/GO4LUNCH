package com.example.go4lunch.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.repository.RepositoryUser;
import com.example.go4lunch.repository.RepositoryWorkmates;
import com.example.go4lunch.ui.UserViewModel;

import java.util.concurrent.Executor;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final RepositoryUser repositoryUser;
    private final RepositoryWorkmates repositoryWorkmates;
    private final Executor executor;

    public ViewModelFactory(RepositoryUser repositoryUser, RepositoryWorkmates repositoryWorkmates, Executor executor) {
        this.repositoryUser = repositoryUser;
        this.repositoryWorkmates = repositoryWorkmates;
        this.executor = executor;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserViewModel.class)) {
        return (T) new UserViewModel(repositoryUser,repositoryWorkmates, executor);}
        throw new IllegalArgumentException("unknown viewModel class");
    }
}
