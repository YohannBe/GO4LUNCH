package com.example.go4lunch.myInterface;

import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

public interface OnButtonClickedListener {
    public void onButtonClicked(View view);
    public void onButtonClickedReservation(View v, ScrollView container, TextView nothingText);
}
