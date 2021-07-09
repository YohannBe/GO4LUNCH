package com.example.go4lunch.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import com.example.go4lunch.R;
import com.example.go4lunch.model.User;
import com.example.go4lunch.recyclerview.RecyclerViewAdapterListWorkmate;

import com.example.go4lunch.viewmodel.WorkmateViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Query;

import java.util.Objects;


public class WorkmatesFragment extends Fragment implements RecyclerViewAdapterListWorkmate.Listener {
    private WorkmateViewModel workmateViewModel;
    private RecyclerViewAdapterListWorkmate recyclerViewAdapterListWorkmate;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        workmateViewModel = new WorkmateViewModel();
        this.configureRecyclerView(getActivity());
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
        View view = inflater.inflate(R.layout.fragment_workmates, container, false);
        initWorkmateFragment(view);
        return view;
    }


    private void initWorkmateFragment(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview_widget_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(this.recyclerViewAdapterListWorkmate);
    }

    private void configureRecyclerView(FragmentActivity activity) {
        this.recyclerViewAdapterListWorkmate = new RecyclerViewAdapterListWorkmate(generateOptionsForAdapter
                (workmateViewModel.getDocumentsQuery(Objects.requireNonNull(getCurrentUser()).getUid())), this, activity);
        recyclerViewAdapterListWorkmate.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {

            }
        });
    }

    private FirestoreRecyclerOptions<User> generateOptionsForAdapter(Query query) {
        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .setLifecycleOwner(this)
                .build();
    }


    @Nullable
    protected FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }


    @Override
    public void onDataChanged() {
    }
}