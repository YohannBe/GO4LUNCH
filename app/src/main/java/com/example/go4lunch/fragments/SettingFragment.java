package com.example.go4lunch.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.go4lunch.R;
import com.example.go4lunch.model.User;
import com.example.go4lunch.myInterface.OnButtonClickedListener;
import com.example.go4lunch.recyclerview.RecyclerviewAdapterListFavorite;
import com.example.go4lunch.tool.Tool;
import com.example.go4lunch.viewmodel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;


public class SettingFragment extends Fragment implements View.OnClickListener {

    private OnButtonClickedListener mCallback;
    private UserViewModel userViewModel;
    private ImageView profilePic;
    private TextView fullName;
    private RecyclerView recyclerViewFavorite;
    private RecyclerviewAdapterListFavorite recyclerviewAdapterListFavorite;



    public SettingFragment() {
        // Required empty public constructor
    }

    public static SettingFragment newInstance (){
        return (new SettingFragment());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_setting, container, false);
        result.findViewById(R.id.button_setting_fragment).setOnClickListener(this);
        initElements(result);
        userViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(UserViewModel.class);
        userViewModel.getUserObject(Objects.requireNonNull(getCurrentUser()).getUid()).observe(getViewLifecycleOwner(), this::initWidgets);
        recyclerViewFavorite = result.findViewById(R.id.recyclerview_profile);
        recyclerViewFavorite.setLayoutManager(new LinearLayoutManager(getActivity()));
        return result;
    }

    private void initElements(View result) {
        profilePic = result.findViewById(R.id.image_profile_setting);
        fullName = result.findViewById(R.id.fullname_profile);
        recyclerViewFavorite = result.findViewById(R.id.recyclerview_profile);
    }

    private void initWidgets(User user) {

        String fullNameString =user.getFirstName() + " " + user.getLastName();
        fullName.setText(fullNameString);
        if (user.getUrlPicture() != null)
            Tool.updatePictureGlide(profilePic, user.getUrlPicture(), getContext());
        this.recyclerviewAdapterListFavorite = new RecyclerviewAdapterListFavorite(getActivity(), userViewModel, user.getFavorite(), Objects.requireNonNull(getCurrentUser()).getUid());
        recyclerViewFavorite.setAdapter(recyclerviewAdapterListFavorite);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.createCallbackToParentActivity();
    }

    @Override
    public void onClick(View v) {
        mCallback.onButtonClicked(v);
    }

    private void createCallbackToParentActivity(){
        try {
            //Parent activity will automatically subscribe to callback
            mCallback = (OnButtonClickedListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString()+ " must implement OnButtonClickedListener");
        }
    }

    @Nullable
    protected FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

}