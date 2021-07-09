package com.example.go4lunch.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.model.User;
import com.example.go4lunch.tool.Tool;
import com.example.go4lunch.viewmodel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class EditProfile extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText firstName, lastName;
    private boolean valueIntent;
    private ImageButton changePic;
    private Uri uriImageSelected = null;
    private Spinner spinner;
    private String languageSelected = null;

    private String firstNameString, lastNameString;
    private UserViewModel userViewModel;


    private static final String PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int RC_IMAGE_PERMS = 100;
    private static final int RC_CHOOSE_PHOTO = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tool.loadLanguage(this, this, true);
        setContentView(R.layout.activity_edite_profile);
        configureViewModel();

        initElements();
        initPersonalData();
    }

    private void initPersonalData() {
        if (!valueIntent) {
            this.userViewModel.getUser(Objects.requireNonNull(getCurrentUser()).getUid()).addOnSuccessListener(documentSnapshot -> {
                User currentUser = documentSnapshot.toObject(User.class);
                assert currentUser != null;
                String userFirstName = TextUtils.isEmpty(currentUser.getFirstName()) ?
                        "" : currentUser.getFirstName();
                firstName.setHint(userFirstName);

                String userLastName = TextUtils.isEmpty(currentUser.getLastName()) ?
                        "" : currentUser.getLastName();
                lastName.setHint(userLastName);


                String userPic = TextUtils.isEmpty(currentUser.getUrlPicture()) ?
                        "" : currentUser.getUrlPicture();

                if (!userPic.equals("")) {
                    FirebaseStorage.getInstance().getReference(userPic).getDownloadUrl()
                            .addOnSuccessListener(uri -> Glide.with(EditProfile.this)
                                    .load(uri)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(changePic));
                }
            });
        }
    }

    private void configureViewModel() {
        this.userViewModel = new UserViewModel();
    }

    private void initElements() {
        firstName = findViewById(R.id.edittext_firstname);
        lastName = findViewById(R.id.editText_lastname);
        changePic = findViewById(R.id.imageButton_edit_profil_pic);
        Button saveChanges = findViewById(R.id.button_save_changes_profile);
        spinner = findViewById(R.id.spinner_languages);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.languages_choice, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        changePic.setOnClickListener(v -> onClickAddFile());
        valueIntent = getIntent().getBooleanExtra("FIRST", false);
        if (!valueIntent) {
            firstName.setHint(R.string.update_fname_hint);
            lastName.setHint(R.string.update_lname_hint);
        }
        saveChanges.setOnClickListener(v -> {

            if (valueIntent) {
                if (TextUtils.isEmpty(firstName.getText().toString()) || TextUtils.isEmpty(lastName.getText().toString())) {
                    Toast.makeText(EditProfile.this, getString(R.string.empty_fields), Toast.LENGTH_SHORT).show();
                } else {
                    firstNameString = Tool.nameToUpperCase(firstName.getText().toString());
                    lastNameString = Tool.nameToUpperCase(lastName.getText().toString());
                    User user = new User(Objects.requireNonNull(getCurrentUser()).getUid(),
                            firstNameString,
                            lastNameString,
                            null);

                    this.userViewModel.createUser(user);

                    if (this.uriImageSelected != null) {
                        uploadPhotoToFirebase();
                    }
                    Intent backToMainActivity = new Intent(EditProfile.this, MainActivity.class);
                    startActivity(backToMainActivity);
                }
            } else {
                if (!TextUtils.isEmpty(firstName.getText().toString())) {
                    firstNameString = Tool.nameToUpperCase(firstName.getText().toString());
                    this.userViewModel.updateFirstName(firstNameString, Objects.requireNonNull(getCurrentUser()).getUid());
                }
                if (!TextUtils.isEmpty(lastName.getText().toString())) {
                    lastNameString = Tool.nameToUpperCase(lastName.getText().toString());
                    this.userViewModel.updateLastName(lastNameString, Objects.requireNonNull(getCurrentUser()).getUid());
                }
                if (this.uriImageSelected != null) {
                    uploadPhotoToFirebase();
                }
                if (languageSelected != null) {
                    Tool.setLocal(languageSelected, this, this, true);
                }
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        });
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
                Toast.makeText(this, getString(R.string.no_photo_choosen), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponsePic(requestCode, resultCode, data);
    }

    private void uploadPhotoToFirebase() {
        String uuid = UUID.randomUUID().toString();
        Toast.makeText(EditProfile.this, uuid, Toast.LENGTH_SHORT).show();
        StorageReference mImageRef = FirebaseStorage.getInstance().getReference(uuid);

        mImageRef.putFile(this.uriImageSelected)
                .addOnFailureListener(e -> Toast.makeText(EditProfile.this, e.toString(), Toast.LENGTH_SHORT).show());

        updatePicUrl(mImageRef.getPath());
    }

    private void updatePicUrl(String uuid) {
        this.userViewModel.updatePicUrl(uuid, Objects.requireNonNull(getCurrentUser()).getUid());
    }


    @Override
    protected void onStart() {
        super.onStart();


    }

    @Nullable
    protected FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        languageSelected = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}