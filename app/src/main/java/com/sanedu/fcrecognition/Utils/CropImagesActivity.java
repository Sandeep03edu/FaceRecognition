package com.sanedu.fcrecognition.Utils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.sanedu.fcrecognition.Constants;
import com.sanedu.fcrecognition.R;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;

public class CropImagesActivity extends AppCompatActivity {

    private static final int EXTERNAL_STORAGE_PERMISSION = 12;
    private static final String TAG = "CropImagesActivityTag";
    ImageView backBtn, nextBtn, submitBtn;
    CropImageView cropImageView;

    int count = 0;
    ArrayList<Uri> uriArrayList = new ArrayList<>();
    Bitmap[] croppedImagesBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_images);

        _init();

        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra(Constants.IMAGES_BUNDLE)) {
            return;
        }

        if(intent.hasExtra(Constants.DISABLE_ASPECT_CROP)){
            cropImageView.setFixedAspectRatio(false);
        }

        Bundle imageBundle = intent.getBundleExtra(Constants.IMAGES_BUNDLE);
        uriArrayList = (ArrayList<Uri>) imageBundle.getSerializable(Constants.IMAGES);

        if (uriArrayList == null || uriArrayList.size() == 0) {
            return;
        }

        // Setting buttons visibility
        backBtn.setVisibility(View.GONE);
        if(uriArrayList.size()==1){
            nextBtn.setVisibility(View.GONE);
            submitBtn.setVisibility(View.VISIBLE);
        }
        else{
            nextBtn.setVisibility(View.VISIBLE);
            submitBtn.setVisibility(View.GONE);
        }

        // Initializing cropped Bitmaps
        croppedImagesBitmap = new Bitmap[uriArrayList.size()];

        // Setting first image
        Uri firstImage = uriArrayList.get(0);
        Log.d(TAG, "onCreate: FirstUri: " + firstImage);

        Log.d(TAG, "onCreate: FirstUri: " + firstImage);
        cropImageView.setImageUriAsync(firstImage);

        // Set Next button action
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NextButtonAction();
            }
        });

        // Set Back button action
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BackButtonAction();
            }
        });

        // Set submit button action
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SubmitAction();
            }
        });
    }

    private void SubmitAction() {
        Intent resultIntent = new Intent();
        croppedImagesBitmap[count] = cropImageView.getCroppedImage();

        if(ContextCompat.checkSelfPermission(CropImagesActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
            ArrayList<Uri> croppedUriArrayList = new ArrayList<>();
            for(Bitmap bitmap : croppedImagesBitmap){
                Uri uri = Utils.Bitmap2Uri(this, bitmap);
                croppedUriArrayList.add(uri);
            }

            Bundle imageBundle = new Bundle();
            imageBundle.putSerializable(Constants.IMAGES, croppedUriArrayList);
            resultIntent.putExtra(Constants.IMAGES_BUNDLE, imageBundle);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
        else{
            ActivityCompat.requestPermissions(CropImagesActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION);
        }
    }

    private void BackButtonAction() {
        croppedImagesBitmap[count] = cropImageView.getCroppedImage();
        count--;
        cropImageView.setImageUriAsync(uriArrayList.get(count));
        SetIconsVisibility();
    }

    private void NextButtonAction() {
        croppedImagesBitmap[count] = cropImageView.getCroppedImage();
        count++;
        cropImageView.setImageUriAsync(uriArrayList.get(count));
        SetIconsVisibility();
    }

    private void SetIconsVisibility() {
        if (count == uriArrayList.size() - 1) {
            nextBtn.setVisibility(View.GONE);
            submitBtn.setVisibility(View.VISIBLE);
        } else {
            nextBtn.setVisibility(View.VISIBLE);
            submitBtn.setVisibility(View.GONE);
        }

        if (count == 0) {
            backBtn.setVisibility(View.GONE);
        } else {
            backBtn.setVisibility(View.VISIBLE);
        }
    }

    private void _init() {
        backBtn = findViewById(R.id.crop_images_back);
        nextBtn = findViewById(R.id.crop_images_next);
        submitBtn = findViewById(R.id.crop_images_submit);
        cropImageView = findViewById(R.id.crop_images_crop_image_view);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SubmitAction();
            }
        }
    }
}