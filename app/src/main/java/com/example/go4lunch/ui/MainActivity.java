package com.example.go4lunch.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.example.go4lunch.repository.RepositoryPlaces;
import com.example.go4lunch.repository.RepositoryUser;
import com.example.go4lunch.repository.RepositoryWorkmates;
import com.example.go4lunch.viewmodel.ViewModelFactory;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

import static com.example.go4lunch.api.UserHelper.COLLECTION_USER;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, OnButtonClickedListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private MainFragment mainFragment;
    private Fragment fragmentSetting;
    private Fragment fragmentReservation;
    private TextView fullName, mail;
    private FirebaseFirestore firebaseFirestore;
    private RepositoryUser repositoryUser = new RepositoryUser();
    private RepositoryWorkmates repositoryWorkmates = new RepositoryWorkmates();
    private RepositoryPlaces repositoryPlaces = new RepositoryPlaces();
    private Executor executor;
    private UserViewModel userViewModel;
    private ImageView picDrawer;
    Fragment selectedFragment = null;
    int tag = 0;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168),
            new LatLng(71, 136));
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;


    private static final int FRAGMENT_SETTING = 0;
    private static final int FRAGMENT_RESERVATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configureViewModel();

        initElements();
    }

    private void configureViewModel() {
        FirebaseUser currentUser = this.getCurrentUser();
        ViewModelFactory mViewModelFactory = new ViewModelFactory(repositoryUser, repositoryWorkmates, executor, repositoryPlaces);
        this.userViewModel = ViewModelProviders.of(this, mViewModelFactory).get(UserViewModel.class);

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
                    Intent toFirstEditActivity = new Intent(MainActivity.this, EditeProfile.class);
                    toFirstEditActivity.putExtra("FIRST", true);
                    startActivity(toFirstEditActivity);
                } else {
                    this.userViewModel.getUser(currentUser.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User myUser = documentSnapshot.toObject(User.class);
                            String userFirstName = TextUtils.isEmpty(myUser.getFirstName()) ?
                                    "First name not found" : myUser.getFirstName();
                            String userLastName = TextUtils.isEmpty(myUser.getLastName()) ?
                                    "Last name not found" : myUser.getLastName();
                            fullName.setText(userFirstName + "  " + userLastName);
                            mail.setText(getCurrentUser().getEmail());

                            if (myUser.getUrlPicture() != "" && myUser.getUrlPicture() != null) {
                                FirebaseStorage.getInstance().getReference(myUser.getUrlPicture()).getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Glide.with(MainActivity.this)
                                                        .load(uri)
                                                        .apply(RequestOptions.circleCropTransform())
                                                        .into(picDrawer);
                                            }
                                        });
                            }
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

        View parentView = navigationView.getHeaderView(0);
        fullName = parentView.findViewById(R.id.textview_firstname_drawer);
        mail = parentView.findViewById(R.id.mail_drawernavigation_header);
        picDrawer = parentView.findViewById(R.id.imageview_header_drawer);
        initSearch();

    }

    private void initSearch() {
        Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);

        PlacesClient placesClient = Places.createClient(this);

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
        setSupportActionBar(toolbar);
    }

    private void configureBottomBar() {
        this.bottomNavigationView = findViewById(R.id.bottom_navigation);
        this.bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
    }

    private void configureAndShowMainFragment() {

        if (tag == 0) {
            mainFragment = new MainFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_activity_frame_layout, mainFragment)
                    .commit();
        } else {
            switch (tag) {
                case 1:
                    selectedFragment = new MainFragment();
                    break;
                case 2:
                    selectedFragment = new ListViewFragment();
                    break;
                case 3:
                    selectedFragment = new WorkmatesFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_activity_frame_layout, selectedFragment)
                    .commit();
        }
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

                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .build(this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
                return true;
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
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.yourlunch_item:
                this.showFragment(FRAGMENT_RESERVATION);
                break;
            case R.id.setting_item:
                this.showFragment(FRAGMENT_SETTING);
                break;
            case R.id.log_out_item:
                logOutFromApp();
                break;
        }
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

    protected Boolean isCurrentUserLogged() {
        return (this.getCurrentUser() != null);
    }

    protected OnFailureListener onFailureListener() {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            }
        };
    }

    @Override
    public void onButtonClicked(View view) {
        Intent toEditProfileActivity = new Intent(this, EditeProfile.class);
        toEditProfileActivity.putExtra("First", false);
        startActivity(toEditProfileActivity);
    }

    @Override
    public void onButtonClickedReservation(View v, ScrollView container, TextView nothingText) {
       this.userViewModel.deleteLunch(getCurrentUser().getUid());
       v.setEnabled(false);
       v.setBackgroundColor(ContextCompat.getColor(this,R.color.colorAccentLighter));
       container.setVisibility(View.GONE);
       nothingText.setVisibility(View.VISIBLE);
    }
}