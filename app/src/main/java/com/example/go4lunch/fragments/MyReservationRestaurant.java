package com.example.go4lunch.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.go4lunch.R;
import com.example.go4lunch.myInterface.OnButtonClickedListener;
import com.example.go4lunch.ui.MainActivity;

import java.util.zip.Inflater;


public class MyReservationRestaurant extends Fragment implements View.OnClickListener {

    private OnButtonClickedListener mCallback;



    public MyReservationRestaurant() {
        // Required empty public constructor
    }

    public static MyReservationRestaurant newInstance (){
        return (new MyReservationRestaurant());
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_my_reservation_restaurant, container, false);

        result.findViewById(R.id.button_myreservation).setOnClickListener(this);

        return result;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.createCallbackToParentActivity();
    }

    @Override
    public void onClick(View v) {
        mCallback.onButtonClickedReservation(v);
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