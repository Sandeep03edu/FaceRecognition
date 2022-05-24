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

public class DualRescanData extends AppCompatActivity {

    ImageView lImg, rImg;
    TextView lType, rType, reScan;
    CardView lCard, rCard;

    int turn = -1;
    private final int leftCard = 0;
    private final int rightCard = 1;

    private final int CAMERA_IMAGE_PICK = 10;
    private final int GALLERY_IMAGE_PICK = 11;

    Uri leftUri = null, rightUri = null;

    DualImageModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dual_rescan_data);

        _init();

        // Set Intent Data
        SetData();

        // Set CardView CLick listener
        SetCardClickAction();

        // SetRescan Action
        SetRescanAction();
    }

    private void SetData() {
        if (getIntent() != null && getIntent().hasExtra(Constants.DUAL_IMAGE_TEST)) {
            model = new Gson().fromJson(getIntent().getStringExtra(Constants.DUAL_IMAGE_TEST), DualImageModel.class);

            lType.setText(model.getLeftImgType());
            rType.setText(model.getRightImgType());

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

    private void SetRescanAction() {
        reScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (leftUri != null && rightUri != null) {
                    model.setLeftImgUri(String.valueOf(leftUri));
                    model.setLeftDisplayImgUri(String.valueOf(leftUri));

                    model.setRightImgUri(String.valueOf(rightUri));
                    model.setRightDisplayImgUri(String.valueOf(rightUri));

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

    private void SetCardClickAction() {
        lCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Permission.CheckPermission(DualRescanData.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                        Permission.CheckPermission(DualRescanData.this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                        Permission.CheckPermission(DualRescanData.this, Manifest.permission.CAMERA)) {
                    SelectImage selectImage = new SelectImage(DualRescanData.this, view);
                    selectImage.GetImage(new SelectImage.SelectImageListener() {
                        @Override
                        public void onGalleryPick() {
                            turn = leftCard;
                            PickGalleryImage();
                        }

                        @Override
                        public void onCameraPick() {
                            turn = leftCard;
                            PickCameraImage();
                        }
                    });
                } else {
                    Permission.RequestPermission(DualRescanData.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE});
                }
            }
        });

        rCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Permission.CheckPermission(DualRescanData.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                        Permission.CheckPermission(DualRescanData.this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                        Permission.CheckPermission(DualRescanData.this, Manifest.permission.CAMERA)) {
                    SelectImage selectImage = new SelectImage(DualRescanData.this, view);
                    selectImage.GetImage(new SelectImage.SelectImageListener() {
                        @Override
                        public void onGalleryPick() {
                            turn = rightCard;
                            PickGalleryImage();
                        }

                        @Override
                        public void onCameraPick() {
                            turn = rightCard;
                            PickCameraImage();
                        }
                    });
                } else {
                    Permission.RequestPermission(DualRescanData.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA});
                }
            }
        });
    }

    private void PickCameraImage() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_IMAGE_PICK);
    }

    private void PickGalleryImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == GALLERY_IMAGE_PICK && data.getData() != null) {
                Uri imageUri = data.getData();
                ArrayList<Uri> uriArrayList = new ArrayList<>();
                uriArrayList.add(imageUri);

                Intent cropImagesIntent = new Intent(this, CropImagesActivity.class);
                Bundle imagesBundle = new Bundle();
                imagesBundle.putSerializable(Constants.IMAGES, uriArrayList);
                cropImagesIntent.putExtra(Constants.IMAGES_BUNDLE, imagesBundle);
                cropImagesIntent.putExtra(Constants.DISABLE_ASPECT_CROP, true);
                startActivityForResult(cropImagesIntent, Constants.CROP_IMAGE_REQUEST_CODE);

            } else if (requestCode == CAMERA_IMAGE_PICK && data.getExtras() != null && data.getExtras().get("data") != null) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                imageBitmap = ImageResizer.reduceBitmapSize(imageBitmap, 240000);

                Uri uri = Utils.Bitmap2Uri(this, imageBitmap);
                ArrayList<Uri> uriArrayList = new ArrayList<>();
                uriArrayList.add(uri);

                Intent cropImagesIntent = new Intent(this, CropImagesActivity.class);
                Bundle imagesBundle = new Bundle();
                imagesBundle.putSerializable(Constants.IMAGES, uriArrayList);
                cropImagesIntent.putExtra(Constants.IMAGES_BUNDLE, imagesBundle);
                cropImagesIntent.putExtra(Constants.CROP_CAMERA, true);
                cropImagesIntent.putExtra(Constants.DISABLE_ASPECT_CROP, true);
                startActivityForResult(cropImagesIntent, Constants.CROP_IMAGE_REQUEST_CODE);
            } else if (requestCode == Constants.CROP_IMAGE_REQUEST_CODE) {
                // Receiving intent with cropped images list
                Bundle imageBundle = data.getBundleExtra(Constants.IMAGES_BUNDLE);
                ArrayList<Uri> croppedUriArrayList = (ArrayList<Uri>) imageBundle.getSerializable(Constants.IMAGES);

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

    private void _init() {
        lImg = findViewById(R.id.dual_rescan_data_l_img);
        rImg = findViewById(R.id.dual_rescan_data_r_img);

        LayoutUtils.fixRatioImageView(this, 2, new View[]{lImg, rImg});

        lType = findViewById(R.id.dual_rescan_data_l_type);
        rType = findViewById(R.id.dual_rescan_data_r_type);
        reScan = findViewById(R.id.dual_rescan_data_re_scan);

        lCard = findViewById(R.id.dual_rescan_data_l_card);
        rCard = findViewById(R.id.dual_rescan_data_r_card);
    }
}