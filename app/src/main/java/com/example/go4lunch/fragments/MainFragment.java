package com.example.go4lunch.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.model.User;
import com.example.go4lunch.model.placeModel.ResultPlaces;
import com.example.go4lunch.tool.Tool;
import com.example.go4lunch.ui.DetailRestaurant;
import com.example.go4lunch.viewmodel.PlaceViewModel;
import com.example.go4lunch.viewmodel.WorkmateViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainFragment extends Fragment {

    private final int REQUEST_LOCATION_PERMISSION = 1234;
    private static GoogleMap mGoogleMap;
    private PlaceViewModel placeViewModel;
    private WorkmateViewModel workmateViewModel;
    private List<ResultPlaces> places = new ArrayList<>();
    private LatLng myLocation;
    private final float zoomLevel = 15.0f;
    private final List<User> userListWithReservation = new ArrayList<>();
    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {

            mGoogleMap = googleMap;
            LocationManager androidLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            LocationListener androidLocationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, zoomLevel));
                    placeViewModel.getPlacesIds(myLocation);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            };

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestLocationPermission();
                return;
            }

            androidLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000,
                    5,
                    androidLocationListener
            );

        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        this.workmateViewModel = new WorkmateViewModel();
        this.placeViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(PlaceViewModel.class);
        FloatingActionButton focusUserButton = v.findViewById(R.id.focus_userFloating_button);
        focusUserButton.setOnClickListener(v1 -> mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, zoomLevel)));
        return v;
    }


    private void putTheMarkers() {
        mGoogleMap.clear();
        boolean userExist;
        int j;
        if (userListWithReservation.size() != 0) {
            for (int i = 0; i < places.size(); i++) {
                ResultPlaces resultPlaces = places.get(i);
                j = 0;
                userExist = false;
                while (j < userListWithReservation.size() && !userExist) {
                    if (Tool.checkIdDateRestaurantExist(userListWithReservation.get(j), resultPlaces.getPlaceId())) {
                        MarkerOptions options = Tool.createMarker(resultPlaces, true, getActivity());
                        mGoogleMap.addMarker(options).setTag(resultPlaces.getPlaceId());
                        userExist = true;
                    }
                    j++;
                }
                if (!userExist) {
                    MarkerOptions options = Tool.createMarker(resultPlaces, false, getActivity());
                    mGoogleMap.addMarker(options).setTag(resultPlaces.getPlaceId());
                }
            }
        } else {
            for (int i = 0; i < places.size(); i++) {
                ResultPlaces resultPlaces = places.get(i);
                MarkerOptions options = Tool.createMarker(resultPlaces, false, getActivity());
                mGoogleMap.addMarker(options).setTag(resultPlaces.getPlaceId());
            }
        }

        mGoogleMap.setOnMarkerClickListener(marker -> {
            Intent intent = new Intent(getActivity(), DetailRestaurant.class);
            String mId = marker.getTag().toString();
            intent.putExtra("restaurantId", mId);
            startActivity(intent);
            return false;
        });
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
            placeViewModel.getHashMapsIds().observe(this, this::getIdPlaces);
            workmateViewModel.getDocumentLunchListRestaurant().observe(getViewLifecycleOwner(), this::updateList);
            Places.initialize(getActivity(), BuildConfig.MAPS_API_KEY);
        }
    }

    private void getIdPlaces(List<ResultPlaces> resultPlacesList) {
        if (myLocation != null) {
            places = resultPlacesList;
            if (places.size() != 0)
                putTheMarkers();
        }
    }

    private void updateList(List<User> list) {
        userListWithReservation.clear();
        userListWithReservation.addAll(list);
        if (places.size() != 0 && userListWithReservation.size() != 0)
            putTheMarkers();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
            Toast.makeText(getActivity(), getString(R.string.location_permission), Toast.LENGTH_SHORT).show();
        } else {
            EasyPermissions.requestPermissions(getActivity(), getString(R.string.ask_permission), REQUEST_LOCATION_PERMISSION, perms);
        }
    }
}