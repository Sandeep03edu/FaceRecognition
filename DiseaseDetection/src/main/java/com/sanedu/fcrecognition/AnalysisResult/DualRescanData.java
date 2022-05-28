package com.sanedu.fcrecognition.AnalysisResult;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sanedu.common.Utils.Constants;
import com.sanedu.fcrecognition.Model.DualImageModel;
import com.sanedu.fcrecognition.R;

import com.sanedu.common.Utils.ImageResizer;
import com.sanedu.common.Utils.LayoutUtils;
import com.sanedu.common.Utils.Permission;
import com.sanedu.fcrecognition.Utils.CropImagesActivity;
import com.sanedu.fcrecognition.Utils.SelectImage;
import com.sanedu.fcrecognition.Utils.Utils;

import java.util.ArrayList;

/**
 * Activity used to fetch new images for dual result new data rescanning
 */
public class DualRescanData extends AppCompatActivity {

    ImageView lImg, rImg;
    TextView lType, rType, reScan;
    CardView lCard, rCard;

    int turn = -1; // Variable to check which imageview is clicked
    private final int leftCard = 0; // Variable for Left Card click
    private final int rightCard = 1; // Variable for Right Card click

    private final int CAMERA_IMAGE_PICK = 10; // Variable for requesting image from camera
    private final int GALLERY_IMAGE_PICK = 11; // Variable for requesting image from gallery

    Uri leftUri = null, rightUri = null; // Variables to store left and right image Uri

    DualImageModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dual_rescan_data);

        // Initialising views
        _init();

        // Set Intent Data
        SetData();

        // Set CardView CLick listener
        SetCardClickAction();

        // SetRescan Action
        SetRescanAction();
    }

    /**
     * Setting intent data in activity views
     */
    private void SetData() {
        // Checking whether data exist or not
        if (getIntent() != null && getIntent().hasExtra(Constants.DUAL_IMAGE_TEST)) {
            // Fetching model using gson from intent
            model = new Gson().fromJson(getIntent().getStringExtra(Constants.DUAL_IMAGE_TEST), DualImageModel.class);

            // Setting model types
            lType.setText(model.getLeftImgType());
            rType.setText(model.getRightImgType());

            // Setting activity title
            setTitle(model.getType());

            // Adding Temp Images
            if (model.getType().equalsIgnoreCase(Constants.EYE_BROW_TEST)) {
                lImg.setImageResource(R.drawable.left_eyebrow);
                rImg.setImageResource(R.drawable.right_eyebrow);
            } else if (model.getType().equalsIgnoreCase(Constants.EYE_RED_TEST)) {
                lImg.setImageResource(R.drawable.left_eye);
                rImg.setImageResource(R.drawable.right_eye);
            } else if (model.getType().equalsIgnoreCase(Constants.LIPS_TEST)) {
                lImg.setImageResource(R.drawable.upper_lip);
                rImg.setImageResource(R.drawable.lower_lip);
            }
        }
    }

    /**
     * Rescan Action function
     * Set Uris to result intent and finish to DualImageResult.java activity
     */
    private void SetRescanAction() {
        reScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Checking whether both images are taken or not
                if (leftUri != null && rightUri != null) {
                    model.setLeftImgUri(String.valueOf(leftUri));
                    model.setLeftDisplayImgUri(String.valueOf(leftUri));

                    model.setRightImgUri(String.valueOf(rightUri));
                    model.setRightDisplayImgUri(String.valueOf(rightUri));

                    // Result intent to store new images and finishing with result and data
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(Constants.DUAL_IMAGE_TEST, new Gson().toJson(model));
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(DualRescanData.this, "Please select both images", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Setting onClickListener for CardViews lCard and rCard
     * Getting image by onClickListener
     */
    private void SetCardClickAction() {
        lCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Checking permission
                if (Permission.CheckPermission(DualRescanData.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                        Permission.CheckPermission(DualRescanData.this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                        Permission.CheckPermission(DualRescanData.this, Manifest.permission.CAMERA)) {

                    // Displaying image selection window
                    SelectImage selectImage = new SelectImage(DualRescanData.this, view);
                    selectImage.GetImage(new SelectImage.SelectImageListener() {
                        @Override
                        public void onGalleryPick() {
                            // Picking gallery image for left card
                            turn = leftCard;
                            PickGalleryImage();
                        }

                        @Override
                        public void onCameraPick() {
                            // Picking Camera image for left card
                            turn = leftCard;
                            PickCameraImage();
                        }
                    });
                }
                // Requesting permission
                else {
                    Permission.RequestPermission(DualRescanData.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE});
                }
            }
        });

        rCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Checking permission
                if (Permission.CheckPermission(DualRescanData.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                        Permission.CheckPermission(DualRescanData.this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                        Permission.CheckPermission(DualRescanData.this, Manifest.permission.CAMERA)) {

                    // Displaying image selection window
                    SelectImage selectImage = new SelectImage(DualRescanData.this, view);
                    selectImage.GetImage(new SelectImage.SelectImageListener() {
                        @Override
                        public void onGalleryPick() {
                            // Picking gallery image for right card
                            turn = rightCard;
                            PickGalleryImage();
                        }

                        @Override
                        public void onCameraPick() {
                            // Picking Camera image for right card
                            turn = rightCard;
                            PickCameraImage();
                        }
                    });
                }
                // Requesting permission
                else {
                    Permission.RequestPermission(DualRescanData.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA});
                }
            }
        });
    }

    /**
     * Picking Camera image using startActivityForResult
     */
    private void PickCameraImage() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_IMAGE_PICK);
    }

    /**
     * Picking Gallery image using startActivityForResult
     */
    private void PickGalleryImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_IMAGE_PICK);
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
            // Getting gallery picked image
            if (requestCode == GALLERY_IMAGE_PICK && data.getData() != null) {
                Uri imageUri = data.getData();
                ArrayList<Uri> uriArrayList = new ArrayList<>();
                uriArrayList.add(imageUri);

                // Sending activity for cropping image
                Intent cropImagesIntent = new Intent(this, CropImagesActivity.class);
                Bundle imagesBundle = new Bundle();
                imagesBundle.putSerializable(Constants.IMAGES, uriArrayList);
                cropImagesIntent.putExtra(Constants.IMAGES_BUNDLE, imagesBundle);
                cropImagesIntent.putExtra(Constants.DISABLE_ASPECT_CROP, true);
                startActivityForResult(cropImagesIntent, Constants.CROP_IMAGE_REQUEST_CODE);

            }
            // Getting Camera picked image
            else if (requestCode == CAMERA_IMAGE_PICK && data.getExtras() != null && data.getExtras().get("data") != null) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                imageBitmap = ImageResizer.reduceBitmapSize(imageBitmap, 240000);

                Uri uri = Utils.Bitmap2Uri(this, imageBitmap);
                ArrayList<Uri> uriArrayList = new ArrayList<>();
                uriArrayList.add(uri);

                // Sending activity for cropping image
                Intent cropImagesIntent = new Intent(this, CropImagesActivity.class);
                Bundle imagesBundle = new Bundle();
                imagesBundle.putSerializable(Constants.IMAGES, uriArrayList);
                cropImagesIntent.putExtra(Constants.IMAGES_BUNDLE, imagesBundle);
                cropImagesIntent.putExtra(Constants.CROP_CAMERA, true);
                cropImagesIntent.putExtra(Constants.DISABLE_ASPECT_CROP, true);
                startActivityForResult(cropImagesIntent, Constants.CROP_IMAGE_REQUEST_CODE);
            }
            // Performing Crop Image request action
            else if (requestCode == Constants.CROP_IMAGE_REQUEST_CODE) {
                // Receiving intent with cropped images list
                Bundle imageBundle = data.getBundleExtra(Constants.IMAGES_BUNDLE);
                ArrayList<Uri> croppedUriArrayList = (ArrayList<Uri>) imageBundle.getSerializable(Constants.IMAGES);

                // Displaying cropped images Uri
                if (croppedUriArrayList.size() > 0) {
                    if (turn == leftCard) {
                        leftUri = croppedUriArrayList.get(0);
                        lImg.setImageURI(croppedUriArrayList.get(0));
                    } else if (turn == rightCard) {
                        rightUri = croppedUriArrayList.get(0);
                        rImg.setImageURI(croppedUriArrayList.get(0));
                    }
                    turn = -1;
                }
            }
        }
    }

    // Initialising Views
    private void _init() {
        lImg = findViewById(R.id.dual_rescan_data_l_img);
        rImg = findViewById(R.id.dual_rescan_data_r_img);

        lType = findViewById(R.id.dual_rescan_data_l_type);
        rType = findViewById(R.id.dual_rescan_data_r_type);
        reScan = findViewById(R.id.dual_rescan_data_re_scan);

        lCard = findViewById(R.id.dual_rescan_data_l_card);
        rCard = findViewById(R.id.dual_rescan_data_r_card);

        // Fixing imageViews dimensions as square with half width of screen
        LayoutUtils.fixRatioImageView(this, 2, new View[]{lImg, rImg});
    }
}