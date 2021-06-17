package com.example.go4lunch.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.go4lunch.R;
import com.example.go4lunch.myInterface.OnButtonClickedListener;


public class SettingFragment extends Fragment implements View.OnClickListener {

    private Button editProfile;
    private OnButtonClickedListener mCallback;



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

        return result;
    }

    @Override
    public void onAttach(Context context) {
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
}