package com.example.go4lunch.model;

import android.graphics.Bitmap;

import com.google.android.libraries.places.api.model.Place;

public class PlaceData {
    private Place place;
    private int numberPersons;
    private Bitmap bitmap;

    public PlaceData(Place place, int numberPersons, Bitmap bitmap) {
        this.place = place;
        this.numberPersons = numberPersons;
        this.bitmap = bitmap;
    }

    public PlaceData(Place place, int numberPersons) {
        this.place = place;
        this.numberPersons = numberPersons;
    }

    public PlaceData(Place place) {
        this.place = place;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public int getNumberPersons() {
        return numberPersons;
    }

    public void setNumberPersons(int numberPersons) {
        this.numberPersons = numberPersons;
    }
}
