package com.sanedu.fcrecognition.Home;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sanedu.fcrecognition.Constants;
import com.sanedu.fcrecognition.R;

public class ImageDisplayActivity extends AppCompatActivity {

    ImageView imageView;
    TextView proceed;
    Bitmap imageBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);

        _init();

        // Set getIntent Image
        SetImage();

        // Set Proceed Button Action
    }

    private void SetImage() {
        if (getIntent()!=null){
            byte[] bytes = getIntent().getByteArrayExtra(Constants.IMAGE_BITMAP_BYTES);
            imageBitmap = BitmapFactory.decodeByteArray(bytes, 0,bytes.length);
            imageView.setImageBitmap(imageBitmap);
        }
        else{
            Toast.makeText(this, Constants.AN_ERROR, Toast.LENGTH_SHORT).show();
        }
    }

    private void _init() {
        imageView = findViewById(R.id.image_display_image_view);
        proceed = findViewById(R.id.image_display_proceed);
    }
}