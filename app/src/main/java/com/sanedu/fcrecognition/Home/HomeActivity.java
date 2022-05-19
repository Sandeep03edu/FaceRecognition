package com.sanedu.fcrecognition.Home;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.sanedu.fcrecognition.Constants;
import com.sanedu.fcrecognition.R;
import com.sanedu.fcrecognition.Utils.ImageResizer;
import com.sanedu.fcrecognition.Utils.Permission;
import com.sanedu.fcrecognition.Utils.Utils;

import java.io.ByteArrayOutputStream;

public class HomeActivity extends AppCompatActivity {

    ImageView galleryImagePicker, cameraImagePicker;
    Bitmap imageBitmap;
    private final int CAMERA_IMAGE_PICK = 0;
    private final int GALLERY_IMAGE_PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        _init();

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
    }

    private void PickCameraImage() {
        if (!Permission.CheckPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || !Permission.CheckPermission(this, Manifest.permission.CAMERA)) {
            Permission.RequestPermission(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA});
            return;
        }

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_IMAGE_PICK);

    }

    private void PickGalleryImage() {
        if (!Permission.CheckPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Permission.RequestPermission(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
            return;
        }

        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && data!=null){
            if(requestCode==GALLERY_IMAGE_PICK && data.getData()!=null){
                Uri imageUri = data.getData();
                imageBitmap = Utils.Uri2Bitmap(this, imageUri);
                // Move to ImageDisplay Activity
                MoveToImageDisplay();
            }
            else if(requestCode==CAMERA_IMAGE_PICK && data.getExtras()!=null && data.getExtras().get("data")!=null){
                imageBitmap = (Bitmap) data.getExtras().get("data");
                // Move to ImageDisplay Activity
                MoveToImageDisplay();
            }
        }
    }

    private void MoveToImageDisplay() {
        if(imageBitmap!=null) {
            Intent intent = new Intent(this, ImageDisplayActivity.class);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap = ImageResizer.reduceBitmapSize(imageBitmap, 240000);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] bytes = baos.toByteArray();
            intent.putExtra(Constants.IMAGE_BITMAP_BYTES, bytes);
            startActivity(intent);
        }
        else{
            Toast.makeText(this, Constants.AN_ERROR, Toast.LENGTH_SHORT).show();
        }
    }

    private void _init() {
        galleryImagePicker = findViewById(R.id.home_gallery);
        cameraImagePicker = findViewById(R.id.home_camera);
    }
}