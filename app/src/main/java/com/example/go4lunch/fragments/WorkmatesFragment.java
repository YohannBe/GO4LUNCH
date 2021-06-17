package com.example.go4lunch.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.go4lunch.R;
import com.example.go4lunch.model.User;
import com.example.go4lunch.ui.RecyclerVIewAdapter;
import com.example.go4lunch.ui.UserViewModel;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class WorkmatesFragment extends Fragment {
    RecyclerView recyclerView;
    private ArrayList<User> workerList = new ArrayList<>();
    private List<String> list = new ArrayList<>();
    private UserViewModel userViewModel;
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mPicturesUrl = new ArrayList<>();



    public WorkmatesFragment() {
        // Required empty public constructor
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
        recyclerView = view.findViewById(R.id.recyclerview_widget_list);
        RecyclerVIewAdapter adapter = new RecyclerVIewAdapter(workerList, getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        userViewModel.getWorkmatesLunch().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (QueryDocumentSnapshot document : task.getResult()) {
                    this.list.add(document.getId());
                }
                for (int i = 0; i < list.size(); i++) {
                    userViewModel.getUser(list.get(i)).addOnSuccessListener(documentSnapshot -> {
                        User specUser = documentSnapshot.toObject(User.class);
                        workerList.add(specUser);
                        adapter.notifyDataSetChanged();
                    });
                }
            }
        });
    }
}