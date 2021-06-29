package com.example.go4lunch.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.RepositoryPlaces;
import com.example.go4lunch.repository.RepositoryUser;
import com.example.go4lunch.repository.RepositoryWorkmates;
import com.example.go4lunch.viewmodel.ViewModelFactory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;
import java.util.concurrent.Executor;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class EditeProfile extends AppCompatActivity {

    private EditText firstName, lastName;
    private String picUrl = null;
    private Button saveChanges;
    private boolean valueIntent;
    private ImageButton changePic;
    private Uri uriImageSelected = null;
    private RepositoryUser repositoryUser = new RepositoryUser();
    private RepositoryWorkmates repositoryWorkmates = new RepositoryWorkmates();
    private RepositoryPlaces repositoryPlaces = new RepositoryPlaces();

    private Executor executor;
    private UserViewModel userViewModel;


    private static final String PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int RC_IMAGE_PERMS = 100;
    private static final int RC_CHOOSE_PHOTO = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edite_profile);
        configureViewModel();

        initElements();
        initPersonalData();
    }

    private void initPersonalData() {
        if (!valueIntent) {
            this.userViewModel.getUser(getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {
                User currentUser = documentSnapshot.toObject(User.class);
                String userFirstName = TextUtils.isEmpty(currentUser.getFirstName()) ?
                        "" : currentUser.getFirstName();
                firstName.setHint(userFirstName);

                String userLastName = TextUtils.isEmpty(currentUser.getLastName()) ?
                        "" : currentUser.getLastName();
                lastName.setHint(userLastName);


                String userPic = TextUtils.isEmpty(currentUser.getUrlPicture()) ?
                        "" : currentUser.getUrlPicture();

                if (userPic != "" && userPic != null) {
                    FirebaseStorage.getInstance().getReference(userPic).getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Glide.with(EditeProfile.this)
                                            .load(uri)
                                            .apply(RequestOptions.circleCropTransform())
                                            .into(changePic);
                                }
                            });
                }
            });
        }
    }

    private void configureViewModel() {
        FirebaseUser currentUser = this.getCurrentUser();
        ViewModelFactory mViewModelFactory = new ViewModelFactory(repositoryUser, repositoryWorkmates, executor, repositoryPlaces);
        this.userViewModel = ViewModelProviders.of(this, mViewModelFactory).get(UserViewModel.class);

    }

    private void initElements() {
        firstName = findViewById(R.id.edittext_firstname);
        lastName = findViewById(R.id.editText_lastname);
        changePic = findViewById(R.id.imageButton_edit_profil_pic);
        saveChanges = findViewById(R.id.button_save_changes_profile);
        changePic.setOnClickListener(v -> onClickAddFile());
        valueIntent = getIntent().getBooleanExtra("FIRST", false);
        if (!valueIntent) {
            firstName.setHint("Update your first name");
            lastName.setHint("Update your last name");
        }
        saveChanges.setOnClickListener(v -> {

            if (valueIntent) {
                if (TextUtils.isEmpty(firstName.getText().toString()) || TextUtils.isEmpty(lastName.getText().toString())) {
                    Toast.makeText(EditeProfile.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    User user = new User(getCurrentUser().getUid(),
                            firstName.getText().toString(),
                            lastName.getText().toString(),
                            picUrl);

                    this.userViewModel.createUser(user);

                    if (this.uriImageSelected != null) {
                        uploadPhotoToFirebase();
                    }
                    Intent backToMainActivity = new Intent(EditeProfile.this, MainActivity.class);
                    startActivity(backToMainActivity);
                }
            } else {
                if (!TextUtils.isEmpty(firstName.getText().toString())) {
                    this.userViewModel.updateFirstName(firstName.getText().toString(), getCurrentUser().getUid());
                }
                if (!TextUtils.isEmpty(lastName.getText().toString())) {
                    this.userViewModel.updateLastName(lastName.getText().toString(), getCurrentUser().getUid());
                }
                if (this.uriImageSelected != null) {
                    uploadPhotoToFirebase();
                }
                EditeProfile.this.onBackPressed();
            }
        });
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(RC_IMAGE_PERMS)
    public void onClickAddFile() {

        if (!EasyPermissions.hasPermissions(this, PERMS)) {
            EasyPermissions.requestPermissions(this, "file access", RC_IMAGE_PERMS, PERMS);
            return;
        }

        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RC_CHOOSE_PHOTO);
    }

    private void handleResponsePic(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) {
                this.uriImageSelected = data.getData();
                Glide.with(this).load(this.uriImageSelected)
                        .apply(RequestOptions.circleCropTransform())
                        .into(this.changePic);
            } else
                Toast.makeText(this, "no photo chosen", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponsePic(requestCode, resultCode, data);
    }

    private void uploadPhotoToFirebase() {
        String uuid = UUID.randomUUID().toString();
        Toast.makeText(EditeProfile.this, uuid, Toast.LENGTH_SHORT).show();
        StorageReference mImageRef = FirebaseStorage.getInstance().getReference(uuid);

        mImageRef.putFile(this.uriImageSelected)
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                this.onFailure(e);
            }
        });

        updatePicUrl(mImageRef.getPath());
    }

    private void updatePicUrl(String uuid) {
        this.userViewModel.updatePicUrl(uuid, getCurrentUser().getUid());
    }


    @Override
    protected void onStart() {
        super.onStart();


    }

    @Nullable
    protected FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }
}