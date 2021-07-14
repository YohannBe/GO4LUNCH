package com.example.go4lunch.model.placeModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PhotoPlace {

    @SerializedName("photo_reference")
    @Expose
    private String photoReference;

    public String getPhotoReference() {
        return photoReference;
    }

    public void setPhotoReference(String photoReference) {
        this.photoReference = photoReference;
    }

    @Override
    public String toString() {
        return "PhotoPlace{" +
                "photoReference='" + photoReference + '\'' +
                '}';
    }
}
