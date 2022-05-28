package com.sanedu.fcrecognition.Profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.sanedu.common.Utils.Constants;
import com.sanedu.fcrecognition.Firebase.FireStorage;
import com.sanedu.fcrecognition.Firebase.FirestoreData;
import com.sanedu.fcrecognition.Model.LabelledImage;
import com.sanedu.fcrecognition.Model.User;
import com.sanedu.fcrecognition.Utils.CropImagesActivity;
import com.sanedu.fcrecognition.Utils.Navigation;
import com.sanedu.fcrecognition.R;
import com.sanedu.common.Utils.LayoutUtils;
import com.sanedu.common.Utils.Permission;
import com.sanedu.fcrecognition.Utils.SelectImage;
import com.sanedu.fcrecognition.Utils.SharedPrefData;
import com.sanedu.fcrecognition.Utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Activity to display My user profile
 */
public class MyProfileActivity extends AppCompatActivity {

    // Activity Views
    DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    CircleImageView profileImage;
    TextView nameTv, phoneNumberTv;

    private final int CAMERA_IMAGE_PICK = 0; // variable to detect camera image pick
    private final int GALLERY_IMAGE_PICK = 1; // variable to detect gallery image pick

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        // Initialising views
        _init();

        // Setting up drawer layout
        SetupDrawerLayout();

        // Setting up Navigation Listener
        SetNavigationListener();

        // Set User Profile data
        SetUserData();

        // Change Profile Pic
        ChangeProfilePic();
    }

    /**
     * Method to change profile pic on onClickAction
     */
    private void ChangeProfilePic() {
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Getting select image popup Window
                SelectImage selectImage = new SelectImage(MyProfileActivity.this, view);
                selectImage.GetImage(new SelectImage.SelectImageListener() {

                    // Picking gallery image
                    @Override
                    public void onGalleryPick() {
                        // Checking permissions
                        if (Permission.CheckPermission(MyProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                && Permission.CheckPermission(MyProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            GetGalleryImage();
                        }
                        // Requesting permissions
                        else {
                            Permission.RequestPermission(MyProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE});
                        }
                    }

                    // Picking gallery image
                    @Override
                    public void onCameraPick() {
                        // Checking permissions
                        if (Permission.CheckPermission(MyProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                && Permission.CheckPermission(MyProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                && Permission.CheckPermission(MyProfileActivity.this, Manifest.permission.CAMERA)) {
                            GetCameraImage();
                        }
                        // Requesting permissions
                        else {
                            Permission.RequestPermission(MyProfileActivity.this, new String[]{Manifest.permission.CAMERA});
                        }
                    }
                });
            }
        });
    }

    /**
     * Method to getCameraImage
     */
    private void GetCameraImage() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_IMAGE_PICK);
    }

    /**
     * Method to get Gallery Image
     */
    private void GetGalleryImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_IMAGE_PICK);
    }

    /**
     * Setting user details into UI
     */
    private void SetUserData() {
        // Getting user from SharedPrefData
        User myUser = SharedPrefData.getUser(this);

        // Checking user exist or not
        if (myUser != null) {
            // Setting user details
            nameTv.setText(myUser.getUsername());
            phoneNumberTv.setText(myUser.getPhoneNumber());

            // Setting user profile pic
            String profilePic = myUser.getAvatar();
            if (!profilePic.trim().isEmpty() && !profilePic.equalsIgnoreCase(Constants.AVATAR)) {
                Picasso.get()
                        .load(profilePic)
                        .placeholder(R.drawable.ic_baseline_person_96)
                        .into(profileImage);
            }
        }
    }

    /**
     * Setting Drawer layout navigationView listener
     */
    private void SetNavigationListener() {
        Navigation navigation = new Navigation(this, drawerLayout);
        navigationView.setNavigationItemSelectedListener(navigation.listener);
    }

    /**
     * Setting up drawer Layout
     */
    private void SetupDrawerLayout() {
        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Implementing onActivityResult to display data fetched from startActivityForResult
     *
     * @param requestCode - Request code to differentiate between different activity results
     * @param resultCode - Result code to check whether task completed successfully or not
     * @param data - Intent result data fetched from result may contain some extras too
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            // Gallery image Pick result action
            if (requestCode == GALLERY_IMAGE_PICK && data.getData() != null) {
                Uri imageUri = data.getData();
                ArrayList<Uri> uriArrayList = new ArrayList<>();
                uriArrayList.add(imageUri);

                // Sending image to CropImagesActivity
                Intent cropImagesIntent = new Intent(this, CropImagesActivity.class);
                Bundle imagesBundle = new Bundle();
                imagesBundle.putSerializable(Constants.IMAGES, uriArrayList);
                cropImagesIntent.putExtra(Constants.IMAGES_BUNDLE, imagesBundle);

                // Starting cropAction for result
                startActivityForResult(cropImagesIntent, Constants.CROP_IMAGE_REQUEST_CODE);
            }
            // Camera image Pick result action
            else if (requestCode == CAMERA_IMAGE_PICK && data.getExtras() != null && data.getExtras().get("data") != null) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                Uri imageUri = Utils.Bitmap2Uri(this, imageBitmap);

                ArrayList<Uri> uriArrayList = new ArrayList<>();
                uriArrayList.add(imageUri);

                // Sending image to CropImagesActivity
                Intent cropImagesIntent = new Intent(this, CropImagesActivity.class);
                Bundle imagesBundle = new Bundle();
                imagesBundle.putSerializable(Constants.IMAGES, uriArrayList);
                cropImagesIntent.putExtra(Constants.IMAGES_BUNDLE, imagesBundle);

                // Starting cropAction for result
                startActivityForResult(cropImagesIntent, Constants.CROP_IMAGE_REQUEST_CODE);
            }
            // CropImage result action
            else if (requestCode == Constants.CROP_IMAGE_REQUEST_CODE) {
                Bundle imageBundle = data.getBundleExtra(Constants.IMAGES_BUNDLE);
                ArrayList<Uri> croppedUriArrayList = (ArrayList<Uri>) imageBundle.getSerializable(Constants.IMAGES);
                // Checking whether croppedUri exist or not with size
                if (croppedUriArrayList.size() <= 0) {
                    dismissDialog();
                    return;
                }

                // Setting Cropped Image Uri in ImageView
                Uri imageUri = croppedUriArrayList.get(0);
                Bitmap imageBitmap = Utils.Uri2Bitmap(this, imageUri);
                UpdateProfilePic(imageBitmap);
            }
        }
    }

    /**
     * Displaying dialog if not null
     */
    private void showDialog() {
        if (progressDialog != null) {
            progressDialog.show();
        }
    }

    /**
     * Removing dialog if not null
     */
    private void dismissDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }


    /**
     * Method to Update user profile picture of myUser
     * @param imageBitmap - Selected cropped image bitmap
     */
    private void UpdateProfilePic(Bitmap imageBitmap) {
        showDialog();
        // Uploading image
        FireStorage storage = new FireStorage(this);
        storage.uploadImage(imageBitmap, Constants.PROFILE_PIC_FOLDER, String.valueOf(System.currentTimeMillis()), new FireStorage.ImageUploadListener() {
            @Override
            public void getDownloadUrl(ArrayList<LabelledImage> imageUrl) {
                String uri = imageUrl.get(0).getImageUrl();

                // Updating profile picture
                FirestoreData data = new FirestoreData();
                data.updateUserImage(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), uri, new FirestoreData.FirestoreListener() {
                    @Override
                    public void onSuccess() {
                        dismissDialog();
                        User myUser = SharedPrefData.getUser(MyProfileActivity.this);
                        if(uri != null && myUser!=null) {
                            // Saving data to SharedPreference
                            myUser.setAvatar(uri);
                            SharedPrefData.addUser(MyProfileActivity.this, myUser);
                            SetUserData();
                        }
                    }

                    @Override
                    public void onFailure(String e) {
                        dismissDialog();
                        Toast.makeText(MyProfileActivity.this, Constants.AN_ERROR + e, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onErrorUpload(String err) {
                dismissDialog();
                Toast.makeText(MyProfileActivity.this, Constants.AN_ERROR + err, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Initialising views
     */
    private void _init() {
        setTitle("My Profile");
        drawerLayout = findViewById(R.id.my_profile_drawer_layout);
        navigationView = findViewById(R.id.my_profile_navigation_view);

        profileImage = findViewById(R.id.my_profile_image);
        nameTv = findViewById(R.id.my_profile_name);
        phoneNumberTv = findViewById(R.id.my_profile_phone_number);

        // Fixing imageViews dimensions as square with half width of screen
        LayoutUtils.fixRatioImageView(this, 2, new View[]{profileImage});

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Changing Profile Pic");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
    }
}