package com.example.go4lunch.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.api.PlaceHelper;
import com.example.go4lunch.model.PlaceData;
import com.example.go4lunch.model.User;
import com.example.go4lunch.tool.JsonParser;
import com.example.go4lunch.tool.Tool;
import com.example.go4lunch.ui.DetailRestaurant;
import com.example.go4lunch.ui.RecyclerVIewAdapter;
import com.example.go4lunch.ui.RecyclerViewAdapterListRestaurant;
import com.example.go4lunch.ui.UserViewModel;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.example.go4lunch.api.PlaceHelper.ParserTask.sendListToFragment;


public class ListViewFragment extends Fragment {

    private UserViewModel userViewModel;
    private RecyclerViewAdapterListRestaurant adapter;
    private RecyclerView recyclerView;
    private final int REQUEST_LOCATION_PERMISSION = 1234;
    private LatLng myLocation;
    private List<PlaceData> dataList = new ArrayList<>();
    private MutableLiveData<List<PlaceData>> dataListMutable = new MutableLiveData<>();
    private List<String> listId = new ArrayList<>();
    private PlacesClient placesClient;
    private List<User> mUserList = new ArrayList<>();


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        adapter = new RecyclerViewAdapterListRestaurant(getContext(), Places.createClient(getActivity()));
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
        Places.initialize(getActivity(), BuildConfig.MAPS_API_KEY);
        placesClient = Places.createClient(getActivity());
        userViewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);
        userViewModel.getAllUsers().observe(this, this::getUsersList);
        recyclerView = v.findViewById(R.id.recyclerview_restaurant_list_format);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        initLocation();
        userViewModel.getHashMapsIds().observe(this, this::getIdPlaces);

    }

    private void getUsersList(List<User> userList) {
        mUserList = userList;
    }


    private void initLocation() {
        LocationManager androidLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationListener androidLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                userViewModel.getPlacesIds(myLocation);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
            Toast.makeText(getActivity(), "Permission already granted", Toast.LENGTH_SHORT).show();
        } else {
            EasyPermissions.requestPermissions(getActivity(), "Please grant the location permission", REQUEST_LOCATION_PERMISSION, perms);
        }
    }

    private void getIdPlaces(List<HashMap<String, String>> hashMaps) {
        userViewModel.sendIdToFetchPlace(hashMaps, getActivity(), placesClient).observe(this, this::updateFinalList);
    }

    private void updateFinalList(List<PlaceData> placeData) {
        adapter.updateRestaurantList(placeData, myLocation, mUserList);
    }

}