package com.example.go4lunch.api;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.tool.JsonParser;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.auth.Token;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlaceHelper {


    private static volatile PlaceHelper instance;

    public PlaceHelper() {
    }

    public static PlaceHelper getInstance() {
        PlaceHelper result = instance;
        if (result != null) {
            return result;
        }
        synchronized (PlaceHelper.class) {
            if (instance == null) {
                instance = new PlaceHelper();
            }
            return instance;
        }
    }

    private MutableLiveData<List<AutocompletePrediction>> listPredictionPlaces = new MutableLiveData<>();

    public LiveData<List<AutocompletePrediction>> startSearch(String query, PlacesClient placesClient, AutocompleteSessionToken token) {
        RectangularBounds LAT_LNG_BOUNDS = RectangularBounds.newInstance(
                new LatLng(-33.880490, 151.184363),
                new LatLng(-33.858754, 151.229596));

        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setCountries("US", "FR")
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setSessionToken(token)
                .setQuery(query)
                .build();
        List<AutocompletePrediction> list = new ArrayList<>();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            list.clear();
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                list.add(prediction);
                Log.i("TAG", prediction.getPlaceId());
                Log.i("TAG", prediction.getPrimaryText(null).toString());
            }
            listPredictionPlaces.setValue(list);
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e("TAG", "Place not found: " + apiException.getStatusCode());
            }
        });
        return listPredictionPlaces;
    }


    public void initUrlMapsNearby(LatLng latLng) {
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latLng.latitude + "," +
                latLng.longitude + "&radius=1500&type=restaurant&key=" + BuildConfig.MAPS_API_KEY;
        new PlaceHelper.PlaceTask().execute(url);
    }

    public static class PlaceTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            String data = null;
            try {
                data = downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            new ParserTask().execute(s);
        }
    }

    private static String downloadUrl(String string) throws IOException {
        URL url = new URL(string);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();

        InputStream stream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();
        String line = "";

        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        String data = builder.toString();
        reader.close();
        return data;
    }

    public static class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {
        public static MutableLiveData<List<HashMap<String, String>>> hashMapsFinal = new MutableLiveData<>();

        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {

            JsonParser jsonParser = new JsonParser();
            List<HashMap<String, String>> mapList = null;
            try {
                JSONObject object = new JSONObject(strings[0]);

                mapList = jsonParser.parseResult(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            hashMapsFinal.setValue(hashMaps);
            sendListToFragment();

        }

        public static LiveData<List<HashMap<String, String>>> sendListToFragment() {
            return hashMapsFinal;
        }
    }
}
