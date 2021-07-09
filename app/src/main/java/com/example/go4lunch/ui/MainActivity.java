package com.example.go4lunch.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.fragments.ListViewFragment;
import com.example.go4lunch.fragments.MainFragment;
import com.example.go4lunch.fragments.MyReservationRestaurant;
import com.example.go4lunch.fragments.SettingFragment;
import com.example.go4lunch.fragments.WorkmatesFragment;
import com.example.go4lunch.model.User;
import com.example.go4lunch.myInterface.OnButtonClickedListener;

import com.example.go4lunch.tool.Tool;
import com.example.go4lunch.viewmodel.PlaceViewModel;
import com.example.go4lunch.viewmodel.UserViewModel;
import com.example.go4lunch.viewmodel.WorkmateViewModel;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.go4lunch.api.UserHelper.COLLECTION_USER;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, OnButtonClickedListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Fragment fragmentSetting;
    private Fragment fragmentReservation;
    private TextView fullName, mail;
    private FirebaseFirestore firebaseFirestore;
    private UserViewModel userViewModel;
    private PlaceViewModel placeViewModel;
    private ImageView picDrawer;
    private MenuItem mMenuItemDrawer, bottomNav;
    private Fragment selectedFragment = null;
    private int tag = 0;
    private boolean openSearch = false;
    private AutoCompleteTextView searchAutoCompletion;
    private CardView searchHolder;
    private BottomNavigationView bottomNavigationView;
    private PlacesClient placesClient;
    private AutocompleteSessionToken token;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;


    private static final int FRAGMENT_SETTING = 0;
    private static final int FRAGMENT_RESERVATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tool.loadLanguage(this, this, false);
        setContentView(R.layout.activity_main);
        configureViewModel();

        initElements();
    }

    private void configureViewModel() {
        this.userViewModel = new UserViewModel();
        this.placeViewModel = new PlaceViewModel();
        WorkmateViewModel workmateViewModel = new WorkmateViewModel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = this.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        checkConnection(currentUser);

    }

    private void checkConnection(FirebaseUser currentUser) {

        if (currentUser == null) {
            toLogInActivity();
        } else {
            firebaseFirestore.collection(COLLECTION_USER).document(currentUser.getUid()).get().addOnCompleteListener(task -> {
                if (!task.getResult().exists()) {
                    Intent toFirstEditActivity = new Intent(MainActivity.this, EditProfile.class);
                    toFirstEditActivity.putExtra("FIRST", true);
                    startActivity(toFirstEditActivity);
                } else {
                    this.userViewModel.getUser(currentUser.getUid()).addOnSuccessListener(documentSnapshot -> {
                        User myUser = documentSnapshot.toObject(User.class);
                        String userFirstName = TextUtils.isEmpty(myUser.getFirstName()) ?
                                getString(R.string.fname_notfound) : myUser.getFirstName();
                        String userLastName = TextUtils.isEmpty(myUser.getLastName()) ?
                                getString(R.string.lname_not_found) : myUser.getLastName();
                        String fullNameString = userFirstName + "  " + userLastName;
                        fullName.setText(fullNameString);
                        mail.setText(Objects.requireNonNull(getCurrentUser()).getEmail());

                        if (myUser.getUrlPicture() != "" && myUser.getUrlPicture() != null) {
                            FirebaseStorage.getInstance().getReference(myUser.getUrlPicture()).getDownloadUrl()
                                    .addOnSuccessListener(uri -> Glide.with(MainActivity.this)
                                            .load(uri)
                                            .apply(RequestOptions.circleCropTransform())
                                            .into(picDrawer));
                        }
                    });
                }
            });
        }
    }

    private void toLogInActivity() {
        Intent toLogInActivityIntent = new Intent(this, LogIn.class);
        startActivity(toLogInActivityIntent);
        finish();
    }

    private void initElements() {
        this.configureToolbar();
        this.configureDrawerLayout();
        this.configureNavigationView();
        this.configureBottomBar();

        this.configureAndShowMainFragment();

        token = AutocompleteSessionToken.newInstance();

        View parentView = navigationView.getHeaderView(0);
        fullName = parentView.findViewById(R.id.textview_firstname_drawer);
        mail = parentView.findViewById(R.id.mail_drawernavigation_header);
        picDrawer = parentView.findViewById(R.id.imageview_header_drawer);
        initSearch();
    }

    private void initSearch() {
        Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);
        placesClient = Places.createClient(this);
    }

   public void startSearch(String query){

        placeViewModel.startSearch(query, placesClient, token).observe(this, this::shoPlaces);

    }

    private void shoPlaces(List<AutocompletePrediction> autocompletePredictions) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i<autocompletePredictions.size(); i++){
            list.add(autocompletePredictions.get(i).getFullText(null).toString());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        searchAutoCompletion.setAdapter(adapter);
        searchAutoCompletion.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, DetailRestaurant.class);
            intent.putExtra("restaurantId", autocompletePredictions.get(position).getPlaceId());
            startActivity(intent);
        });
    }

    private void configureDrawerLayout() {
        this.drawerLayout = findViewById(R.id.main_activity_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void configureNavigationView() {
        this.navigationView = findViewById(R.id.main_activity_navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void configureToolbar() {
        this.toolbar = findViewById(R.id.include);
        searchAutoCompletion = findViewById(R.id.search_place_edittext);
        searchHolder = findViewById(R.id.cardview_search_holder);
        searchAutoCompletion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                    String query = searchAutoCompletion.getText().toString();
                if (query.length()>2) {
                    //Toast.makeText(getApplicationContext(), query, Toast.LENGTH_SHORT).show();
                    startSearch(query);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        setSupportActionBar(toolbar);
    }

    private void configureBottomBar() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
    }

    private void configureAndShowMainFragment() {

        if (tag == 0) {
            MainFragment mainFragment = new MainFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_activity_frame_layout, mainFragment)
                    .commit();
        } else {
            switch (tag) {
                case 1:
                    selectedFragment = new MainFragment();
                    principalFragment(selectedFragment);
                    break;
                case 2:
                    selectedFragment = new ListViewFragment();
                    principalFragment(selectedFragment);
                    break;
                case 3:
                    selectedFragment = new WorkmatesFragment();
                    principalFragment(selectedFragment);
                    break;
                case 4:
                    this.showFragment(FRAGMENT_RESERVATION);
                    break;
                case 5:
                    this.showFragment(FRAGMENT_SETTING);
                    break;
            }
        }
    }

    public void principalFragment(Fragment selectedFragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_activity_frame_layout, selectedFragment)
                .commit();
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        outState.putInt("selected_fragment", tag);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tag = savedInstanceState.getInt("selected_fragment");
        configureAndShowMainFragment();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {


        switch (item.getItemId()) {
            case R.id.page_1:
                tag = 1;
                break;
            case R.id.page_2:
                tag = 2;
                break;
            case R.id.page_3:
                tag = 3;
                break;
        }
        if (mMenuItemDrawer != null)
            mMenuItemDrawer.setChecked(false);
        configureAndShowMainFragment();
        return true;
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                if (!openSearch) {
                    TransitionManager.beginDelayedTransition(searchHolder);
                    searchHolder.setVisibility(View.VISIBLE);
                    item.setIcon(R.drawable.close_search_icons);
                    openSearch = true;
                } else {
                    TransitionManager.beginDelayedTransition(searchHolder);
                    searchHolder.setVisibility(View.GONE);
                    item.setIcon(R.drawable.search_icons);
                    openSearch = false;
                }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(getApplicationContext(), status.toString(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        mMenuItemDrawer = item;
        switch (item.getItemId()) {
            case R.id.yourlunch_item:
                tag = 4;
                break;
            case R.id.setting_item:
                tag = 5;
                break;
            case R.id.log_out_item:
                logOutFromApp();
                break;
        }
        if (bottomNavigationView != null) {
            bottomNavigationView.getMenu().setGroupCheckable(0, true, false);
            for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
                bottomNavigationView.getMenu().getItem(i).setChecked(false);
            }
            bottomNavigationView.getMenu().setGroupCheckable(0, true, true);
        }
        configureAndShowMainFragment();
        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logOutFromApp() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUiAfterRestRequestCompleted());
    }

    private OnSuccessListener<? super Void> updateUiAfterRestRequestCompleted() {
        return (OnSuccessListener<Void>) aVoid -> checkConnection(getCurrentUser());
    }


    private void showFragment(int fragmentIdentifier) {
        switch (fragmentIdentifier) {
            case FRAGMENT_RESERVATION:
                this.showReservationFragment();
                break;
            case FRAGMENT_SETTING:
                this.showSettingFragment();
                break;
            default:
                break;
        }
    }


    private void showSettingFragment() {
        if (this.fragmentSetting == null)
            this.fragmentSetting = SettingFragment.newInstance();
        this.startTransactionFragment(this.fragmentSetting);
    }

    private void showReservationFragment() {
        if (this.fragmentReservation == null)
            this.fragmentReservation = MyReservationRestaurant.newInstance();
        this.startTransactionFragment(this.fragmentReservation);
    }

    private void startTransactionFragment(Fragment fragment) {
        if (!fragment.isVisible()) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_activity_frame_layout, fragment)
                    .commit();
        }
    }


    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START))
            this.drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @Nullable
    protected FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void onButtonClicked(View view) {
        Intent toEditProfileActivity = new Intent(this, EditProfile.class);
        toEditProfileActivity.putExtra("First", false);
        startActivity(toEditProfileActivity);
    }

    @Override
    public void onButtonClickedReservation(View v, ScrollView container, TextView nothingText) {
        this.userViewModel.deleteLunch(getCurrentUser().getUid());
        v.setEnabled(false);
        v.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccentLighter));
        container.setVisibility(View.GONE);
        nothingText.setVisibility(View.VISIBLE);
    }
}