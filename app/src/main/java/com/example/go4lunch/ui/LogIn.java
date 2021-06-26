package com.example.go4lunch.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Toast;

import com.example.go4lunch.R;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;


import java.util.Arrays;

public class LogIn extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.startSignInActivity();
    }

    public void startSignInActivity() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(
                                Arrays.asList(
                                        new AuthUI.IdpConfig.EmailBuilder().build(),
                                        new AuthUI.IdpConfig.GoogleBuilder().build(),
                                        new AuthUI.IdpConfig.FacebookBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .setAuthMethodPickerLayout(getCustomAuthLayout())
                        .build(),
                RC_SIGN_IN);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(this, "ici2", Toast.LENGTH_SHORT)
                .show();
        this.handleResponseAfterSigningIn(requestCode, resultCode, data);
    }

    private void handleResponseAfterSigningIn(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Sign in successfully", Toast.LENGTH_SHORT).show();
                Intent toMainActivity = new Intent(this, MainActivity.class);
                startActivity(toMainActivity);
                finish();
            }
        } else
            Toast.makeText(this, "Operation cancelled", Toast.LENGTH_SHORT).show();
    }

    private AuthMethodPickerLayout getCustomAuthLayout() {

        return new AuthMethodPickerLayout.Builder(R.layout.activity_log_in)
                .setEmailButtonId(R.id.button_signin_mail)
                .setGoogleButtonId(R.id.sign_in_button_google)
                .setFacebookButtonId(R.id.login_button_facebook)
                .build();
    }
}