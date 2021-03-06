package com.sanedu.fcrecognition.Home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.sanedu.common.Utils.Constants;
import com.sanedu.fcrecognition.AnalysisResult.ResultPageActivity;
import com.sanedu.fcrecognition.Utils.Navigation;
import com.sanedu.fcrecognition.R;
import com.sanedu.common.Utils.ImageResizer;
import com.sanedu.common.Utils.Permission;
import com.sanedu.fcrecognition.Utils.Utils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Activity to be splashed when user logged in or complete its registration
 */
public class HomeActivity extends AppCompatActivity {

    // Activity Views
    DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    ImageView galleryImagePicker, cameraImagePicker;

    Bitmap imageBitmap;
    private final int CAMERA_IMAGE_PICK = 0; // variable to detect camera image pick
    private final int GALLERY_IMAGE_PICK = 1; // variable to detect gallery image pick

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialising views
        _init();

        // Setting up Drawer layout
        SetupDrawerLayout();

        // Pick Gallery Image
        galleryImagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PickGalleryImage();
            }
        });

        // Pick Camera Image
        cameraImagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PickCameraImage();
            }
        });

        // Set Navigation View listener
        SetNavigationListener();
    }

    /**
     * Setting up NavigationListener
     */
    private void SetNavigationListener() {
        Navigation navigation = new Navigation(this, drawerLayout);
        navigationView.setNavigationItemSelectedListener(navigation.listener);
    }

    /**
     * Setting up drawer layout
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

    /**
     * @return boolean - response on icon click
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method to pick Image from Camera
     */
    private void PickCameraImage() {
        if (!Permission.CheckPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                !Permission.CheckPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                !Permission.CheckPermission(this, Manifest.permission.CAMERA)) {
            Permission.RequestPermission(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA});
            return;
        }

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_IMAGE_PICK);
    }

    /**
     * Method to pick Image from Gallery
     */
    private void PickGalleryImage() {
        if (!Permission.CheckPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                !Permission.CheckPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Permission.RequestPermission(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE});
            return;
        }

        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        /*
        // Accuracy testing purpose
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
         */
        startActivityForResult(galleryIntent, GALLERY_IMAGE_PICK);
    }

    /**
     * Implementing onActivityResult to display data fetched from startActivityForResult
     *
     * @param requestCode - Request code to differentiate between different activity results
     * @param resultCode  - Result code to check whether task completed successfully or not
     * @param data        - Intent result data fetched from result may contain some extras too
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            // Implementing code for Gallery image Pick
            if (requestCode == GALLERY_IMAGE_PICK && data.getData() != null) {
                Uri imageUri = data.getData();
                imageBitmap = Utils.Uri2Bitmap(this, imageUri);
                // Move to ImageDisplay Activity
                MoveToImageDisplay();
            }
            // Implementing code for Camera image Pick
            else if (requestCode == CAMERA_IMAGE_PICK && data.getExtras() != null && data.getExtras().get("data") != null) {
                imageBitmap = (Bitmap) data.getExtras().get("data");
                // Move to ImageDisplay Activity
                MoveToImageDisplay();
            }
            /*
        // Accuracy testing purpose
            else if(requestCode==GALLERY_IMAGE_PICK && data.getClipData()!=null){
                int totalSize = data.getClipData().getItemCount();
                testingUri = new ArrayList<>();
                for(int i=0; i<totalSize; ++i){
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    testingUri.add(uri);
                }

                Uri topUri = testingUri.get(0);
                testingUri.remove(topUri);
                Bitmap bitmap = Utils.Uri2Bitmap(this, topUri);
                Intent intent = new Intent(this, ResultPageActivity.class);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap = ImageResizer.reduceBitmapSize(bitmap, 240000);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] bytes = baos.toByteArray();
                intent.putExtra(Constants.IMAGE_BITMAP_BYTES, bytes);
                startActivityForResult(intent, 1221);
            }
            else if(requestCode==1221){
                if(testingUri.size()>0){
                    Uri topUri = testingUri.get(0);
                    testingUri.remove(topUri);
                    Bitmap bitmap = Utils.Uri2Bitmap(this, topUri);
                    Intent intent = new Intent(this, ResultPageActivity.class);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap = ImageResizer.reduceBitmapSize(bitmap, 240000);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] bytes = baos.toByteArray();
                    intent.putExtra(Constants.IMAGE_BITMAP_BYTES, bytes);
                    startActivityForResult(intent, 1221);
                }
            }
             */
        }
    }

    /**
     * Method to move to ImageDisplayActivity activity with imageBitmap byte array
     */
    private void MoveToImageDisplay() {
        // Checking imageBitmap exist or not
        if (imageBitmap != null) {
            Intent intent = new Intent(this, ImageDisplayActivity.class);

            // Converting Bitmap to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap = ImageResizer.reduceBitmapSize(imageBitmap, 240000);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] bytes = baos.toByteArray();

            intent.putExtra(Constants.IMAGE_BITMAP_BYTES, bytes);
            startActivity(intent);
        } else {
            Toast.makeText(this, Constants.AN_ERROR, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Initialising views
     */
    private void _init() {
        galleryImagePicker = findViewById(R.id.home_gallery);
        cameraImagePicker = findViewById(R.id.home_camera);
        drawerLayout = findViewById(R.id.home_drawer_layout);
        navigationView = findViewById(R.id.home_navigation_view);
    }
}