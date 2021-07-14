package com.example.go4lunch.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.model.User;
import com.example.go4lunch.model.placeModel.ResultPlaces;
import com.example.go4lunch.recyclerview.RecyclerViewAdapterListRestaurant;
import com.example.go4lunch.viewmodel.PlaceViewModel;
import com.example.go4lunch.viewmodel.WorkmateViewModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class ListViewFragment extends Fragment {

    private PlaceViewModel placeViewModel;
    private PlacesClient placesClient;
    private WorkmateViewModel workmateViewModel;
    private RecyclerViewAdapterListRestaurant adapter;
    private final int REQUEST_LOCATION_PERMISSION = 1234;
    private LatLng myLocation;
    private List<User> mUserList = new ArrayList<>();
    private List<ResultPlaces> mRestaurantList = new ArrayList<>();


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        initPlaces();
        adapter = new RecyclerViewAdapterListRestaurant(getContext(), placeViewModel, placesClient);
    }

    public ListViewFragment() {
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

        View v = inflater.inflate(R.layout.fragment_list_view, container, false);
        initRestaurantList(v);
        return v;
    }

    private void initRestaurantList(View v) {
        initViewModels();
        initRecyclerView(v);
        initLocation();
    }


    private void initRecyclerView(View v) {
        RecyclerView recyclerView = v.findViewById(R.id.recyclerview_restaurant_list_format);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void initViewModels() {
        placeViewModel = new PlaceViewModel();
        workmateViewModel = new WorkmateViewModel();
        initUserList();
    }

    private void initPlaces() {
        Places.initialize(Objects.requireNonNull(getActivity()), BuildConfig.MAPS_API_KEY);
        placesClient = Places.createClient(getActivity());
    }

    private void initUserList() {
        workmateViewModel.getDocumentLunchListRestaurant().observe(getActivity(), this::getUsersList);
        placeViewModel.getHashMapsIds().observe(this, this::getIdPlaces);
    }

    private void getUsersList(List<User> userList) {
        mUserList = userList;
        if (mRestaurantList.size() != 0)
            adapter.updateRestaurantList(mRestaurantList, myLocation, mUserList);
    }


    private void initLocation() {

        LocationManager androidLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationListener androidLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                if (myLocation != null)
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

        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_FINE_LOCATION) !=
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(Objects.requireNonNull(getActivity()), perms)) {
            Toast.makeText(getActivity(), getString(R.string.location_permission), Toast.LENGTH_SHORT).show();
        } else {
            EasyPermissions.requestPermissions(getActivity(), getString(R.string.ask_permission), REQUEST_LOCATION_PERMISSION, perms);
        }
    }

    private void getIdPlaces(List<ResultPlaces> resultPlacesList) {
        mRestaurantList = resultPlacesList;
        adapter.updateRestaurantList(mRestaurantList, myLocation, mUserList);
    }


}