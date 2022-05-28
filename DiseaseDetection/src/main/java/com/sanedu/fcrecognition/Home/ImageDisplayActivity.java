package com.sanedu.fcrecognition.Home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sanedu.common.Utils.Constants;
import com.sanedu.fcrecognition.AnalysisResult.ResultPageActivity;
import com.sanedu.fcrecognition.R;
import com.sanedu.common.Utils.ImageResizer;
import com.theartofdev.edmodo.cropper.CropImageView;

/**
 * Activity to display selected image with cropImageView
 * Cropping chosen image and proceeding to Result page
 */

public class ImageDisplayActivity extends AppCompatActivity {

    CropImageView imageView;
    TextView proceed;
    Bitmap imageBitmap = null;
    byte[] bytes = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);

        // Initialising views
        _init();

        // Set getIntent Image
        SetImage();

        // Set Proceed Button Action
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoveToResultPage();
            }
        });
    }

    /**
     * Moving to ResultPageActivity activity
     */
    private void MoveToResultPage() {
        // Checking whether bytes exist or not
        if(bytes!=null){
            imageBitmap = imageView.getCroppedImage();
            imageBitmap = ImageResizer.reduceBitmapSize(imageBitmap, 240000);
            Intent resultIntent = new Intent(this, ResultPageActivity.class);
            resultIntent.putExtra(Constants.IMAGE_BITMAP_BYTES, bytes);
            startActivity(resultIntent);
        }
        else{
            Toast.makeText(this, Constants.AN_ERROR, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Setting image from intent data
     */
    private void SetImage() {
        // Checking whether intent data exist or not
        if (getIntent()!=null){
            // converting bytes to Bitmap 
            bytes = getIntent().getByteArrayExtra(Constants.IMAGE_BITMAP_BYTES);
            imageBitmap = BitmapFactory.decodeByteArray(bytes, 0,bytes.length);
            imageBitmap = ImageResizer.reduceBitmapSize(imageBitmap, 240000);
            
            // Setting bitmap to imageView
            imageView.setImageBitmap(imageBitmap);
        }
        else{
            Toast.makeText(this, Constants.AN_ERROR, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Initialising views
     */
    private void _init() {
        imageView = findViewById(R.id.image_display_image_view);
        proceed = findViewById(R.id.image_display_proceed);
    }
}