package com.example.go4lunch.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.go4lunch.R;
import com.example.go4lunch.fragments.ListViewFragment;
import com.example.go4lunch.fragments.MainFragment;
import com.example.go4lunch.fragments.MyReservationRestaurant;
import com.example.go4lunch.fragments.SettingFragment;
import com.example.go4lunch.fragments.WorkmatesFragment;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private MainFragment mainFragment;
    private Fragment fragmentSetting;
    private Fragment fragmentReservation;



    private static final int FRAGMENT_SETTING = 0;
    private static final int FRAGMENT_RESERVATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkConnection();
        initElements();
    }

    private void checkConnection() {
        if(this.getCurrentUser() == null){
            Intent toLogInActivity = new Intent(this, LogIn.class);
            startActivity(toLogInActivity);
            finish();
        }
    }

    private void initElements() {
        this.configureToolbar();
        this.configureDrawerLayout();
        this.configureNavigationView();
        this.configureBottomBar();

        this.configureAndShowMainFragment();
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

    private void configureBottomBar(){
        this.bottomNavigationView = findViewById(R.id.bottom_navigation);
        this.bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
    }

    private void configureAndShowMainFragment(){
        mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.main_activity_frame_layout);
        if(mainFragment==null){
            mainFragment = new MainFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_activity_frame_layout, mainFragment)
                    .commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {

        Fragment selectedFragment = null;

        switch (item.getItemId()){
            case R.id.page_1:
                selectedFragment = new MainFragment();
                break;
            case R.id.page_2:
                selectedFragment = new ListViewFragment();
                break;
            case R.id.page_3:
                selectedFragment = new WorkmatesFragment();
                break;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_activity_frame_layout, selectedFragment)
                .commit();
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
                Toast.makeText(this, "nothing yet", Toast.LENGTH_SHORT)
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                checkConnection();
            }
        };
    }


    private void showFragment(int fragmentIdentifier){
        switch (fragmentIdentifier){
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


    private void showSettingFragment(){
        if(this.fragmentSetting == null)
            this.fragmentSetting = SettingFragment.newInstance();
        this.startTransactionFragment(this.fragmentSetting);
    }

    private void showReservationFragment(){
        if(this.fragmentReservation == null)
            this.fragmentReservation = MyReservationRestaurant.newInstance();
        this.startTransactionFragment(this.fragmentReservation);
    }

    private void startTransactionFragment(Fragment fragment){
        if (!fragment.isVisible()){
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
    protected FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser(); }

    protected Boolean isCurrentUserLogged(){
        return (this.getCurrentUser() != null); }

}