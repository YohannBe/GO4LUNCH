package com.example.go4lunch.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.go4lunch.R;


public class MyReservationRestaurant extends Fragment {


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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_reservation_restaurant, container, false);
    }
}