package com.example.go4lunch.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

public class LogIn extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123, RC_SIGN_IN_GOOGLE = 124;
    private Button googleSignInButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        initGoogleConnexion();
        mAuth = FirebaseAuth.getInstance();

    }

    private void initGoogleConnexion() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.API_KEY_FIREBASE)
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInButton = findViewById(R.id.sign_in_button);
        googleSignInButton.setOnClickListener(v -> signIn(mGoogleSignInClient));
    }


    private void signIn(GoogleSignInClient mGoogleSignInClient) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        Toast.makeText(this, "ici1", Toast.LENGTH_SHORT)
                .show();
        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
    }

    private void updateUI(FirebaseUser account) {
        if (account != null){
            Toast.makeText(this, "Sign in successfully", Toast.LENGTH_SHORT).show();
            Intent toMainActivity = new Intent(this, MainActivity.class);
            startActivity(toMainActivity);
            finish();
        }
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
        Toast.makeText(this, "ici2", Toast.LENGTH_SHORT)
                .show();
        this.handleResponseAfterSigningIn(requestCode, resultCode, data);
    }

    private void handleResponseAfterSigningIn(int requestCode, int resultCode, @Nullable Intent data) {

        //IdpResponse idpResponse = IdpResponse.fromResultIntent(data);
        if (requestCode == RC_SIGN_IN) {
            Toast.makeText(this, "Sign in successfully", Toast.LENGTH_SHORT).show();
            Intent toMainActivity = new Intent(this, MainActivity.class);
            startActivity(toMainActivity);
            finish();
        } else if (requestCode == RC_SIGN_IN_GOOGLE) {
            Toast.makeText(this, "ici3", Toast.LENGTH_SHORT)
                    .show();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else
            Toast.makeText(this, "Operation cancelled", Toast.LENGTH_SHORT).show();
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Toast.makeText(this, "ici4", Toast.LENGTH_SHORT)
                    .show();
            firebaseAuthWithGoogle(account.getIdToken());

            //updateUI(account);
        } catch (ApiException e) {
            Toast.makeText(this, "signInResult:failed code=" + e.getStatusCode(), Toast.LENGTH_SHORT)
                    .show();
            Log.w("Google Error", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        Toast.makeText(this, "ici5", Toast.LENGTH_SHORT)
                .show();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG credential", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
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