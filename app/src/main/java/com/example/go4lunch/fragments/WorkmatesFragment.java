package com.example.go4lunch.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.go4lunch.R;
import com.example.go4lunch.model.Lunch;
import com.example.go4lunch.model.User;
import com.example.go4lunch.ui.RecyclerVIewAdapter;
import com.example.go4lunch.ui.UserViewModel;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.time.chrono.JapaneseDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class WorkmatesFragment extends Fragment implements  RecyclerVIewAdapter.UpdateWorkmatesListener{
    RecyclerView recyclerView;

    private UserViewModel userViewModel;
    private RecyclerVIewAdapter adapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
         adapter = new RecyclerVIewAdapter(getContext(), this);
    }




    public WorkmatesFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_workmates, container, false);

        initWorkmateFragment(view);

        return view;
    }


    private void initWorkmateFragment(View view) {


        userViewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);
        userViewModel.getAllUsers().observe(this, this:: getAllUsers);
        recyclerView = view.findViewById(R.id.recyclerview_widget_list);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    private void getAllUsers(List<User> userList) {
        userList = userViewModel.updateListUserSort(userList);
        adapter.updateWorkmateList(userList);
    }

    @Override
    public void onUpdateWorkmate(User user) {

    }
}