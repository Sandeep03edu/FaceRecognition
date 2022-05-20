package com.sanedu.fcrecognition.AnalysisResult;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sanedu.fcrecognition.Constants;
import com.sanedu.fcrecognition.Model.DualImageModel;
import com.sanedu.fcrecognition.R;
import com.sanedu.fcrecognition.Utils.LayoutUtils;

public class DualImageResult extends AppCompatActivity {

    ImageView lImg, rImg;
    TextView lType,rType, lResult, rResult, reScan;
    DualImageModel model;
    private final int RESCAN_REQUEST = 121;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dual_image_result);

        _init();

        // Get Data from Intent
        GetIntentData();

        // Set Data from Intent
        SetIntentData();

        // TODO : Get Result

        // Rescan Action
        RescanData();
    }

    private void RescanData() {
        reScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getIntent()!=null && getIntent().hasExtra(Constants.DUAL_IMAGE_TEST)){
                    String gson = getIntent().getStringExtra(Constants.DUAL_IMAGE_TEST);
                    Intent rescanIntent = new Intent(DualImageResult.this, DualRescanData.class);
                    rescanIntent.putExtra(Constants.DUAL_IMAGE_TEST, gson);
                    startActivityForResult(rescanIntent, RESCAN_REQUEST);
                }
            }
        });
    }

    private void SetIntentData() {
        if(model!=null){
            setTitle(model.getType());
            lImg.setImageURI(Uri.parse(model.getLeftDisplayImgUri()));
            rImg.setImageURI(Uri.parse(model.getRightDisplayImgUri()));

            lType.setText(model.getLeftImgType());
            rType.setText(model.getRightImgType());
        }
    }

    private void GetIntentData() {
        if(getIntent()!=null && getIntent().hasExtra(Constants.DUAL_IMAGE_TEST)){
            String gson = getIntent().getStringExtra(Constants.DUAL_IMAGE_TEST);
            model = new Gson().fromJson(gson, DualImageModel.class);
        }
    }

    private void _init() {
        lImg = findViewById(R.id.dual_image_res_l_img);
        rImg = findViewById(R.id.dual_image_res_r_img);

        LayoutUtils.fixRatioImageView(this, 2, new ImageView[]{lImg , rImg});

        lType = findViewById(R.id.dual_image_res_l_type);
        rType = findViewById(R.id.dual_image_res_r_type);

        lResult = findViewById(R.id.dual_image_res_l_result);
        rResult = findViewById(R.id.dual_image_res_r_result);

        reScan = findViewById(R.id.dual_image_res_rescan);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==RESCAN_REQUEST && data!=null){
            model = new Gson().fromJson(data.getStringExtra(Constants.DUAL_IMAGE_TEST), DualImageModel.class);
            SetIntentData();
        }
    }
}