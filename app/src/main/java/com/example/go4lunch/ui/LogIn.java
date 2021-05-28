package com.example.go4lunch.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.go4lunch.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.util.Arrays;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;

public class LogIn extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

    }


    public void startSignInActivity(View view) {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(
                                Arrays.asList(
                                        new AuthUI.IdpConfig.EmailBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponseAfterSigningIn(requestCode, resultCode, data);
    }

    private void handleResponseAfterSigningIn(int requestCode, int resultCode, @Nullable Intent data) {

        //IdpResponse idpResponse = IdpResponse.fromResultIntent(data);
        if (requestCode == RC_SIGN_IN) {
            Toast.makeText(this, "Sign in successfully", Toast.LENGTH_SHORT).show();
            Intent toMainActivity = new Intent(this, MainActivity.class);
            startActivity(toMainActivity);
            finish();
        } else
            Toast.makeText(this, "Operation cancelled", Toast.LENGTH_SHORT).show();
    }

    public void startSignInActivityFacebook(View view) {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(
                                Arrays.asList(
                                        new AuthUI.IdpConfig.FacebookBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .build(),
                RC_SIGN_IN);
    }
}