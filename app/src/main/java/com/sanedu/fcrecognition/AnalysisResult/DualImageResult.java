package com.sanedu.fcrecognition.AnalysisResult;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sanedu.fcrecognition.Constants;
import com.sanedu.fcrecognition.FaceSymptomScorer;
import com.sanedu.fcrecognition.Model.DualImageModel;
import com.sanedu.fcrecognition.R;
import com.sanedu.fcrecognition.Utils.BackgroundWork;
import com.sanedu.fcrecognition.Utils.LayoutUtils;
import com.sanedu.fcrecognition.Utils.Utils;

public class DualImageResult extends AppCompatActivity {

    private static final String TAG = "DualImageResultTag";
    ImageView lImg, rImg;
    TextView lType, rType, lResult, rResult, reScan;
    DualImageModel model;
    private final int RESCAN_REQUEST = 121;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dual_image_result);

        _init();

        // Get Data from Intent
        GetIntentData();

        // Set Data from Intent
        SetIntentData();

        // Get Result
        GetResult();

        // Rescan Action
        RescanData();
    }

    private void GetResult() {
        if (model != null) {
            showDialog();
            String type = model.getType();
            if (type.equalsIgnoreCase(Constants.EYE_BROW_TEST)) {
                SetEyebrowResult();
            } else if (type.equalsIgnoreCase(Constants.EYE_RED_TEST)) {
                SetEyeRednessResult();
            } else if (type.equalsIgnoreCase(Constants.LIPS_TEST)) {
                SetLipDryResult();
            }
        }
    }

    private void SetLipDryResult() {
        FaceSymptomScorer symptomScorer = new FaceSymptomScorer();
        final double[] lDry = {0};
        final double[] rDry = {0};

        new BackgroundWork(this) {
            @Override
            public void doInBackground() {
                super.doInBackground();
                symptomScorer.setBitmap(Utils.Uri2Bitmap(DualImageResult.this, Uri.parse(model.getLeftImgUri())));
                lDry[0] = symptomScorer.detectDryLips();

                symptomScorer.setBitmap(Utils.Uri2Bitmap(DualImageResult.this, Uri.parse(model.getRightImgUri())));
                rDry[0] = symptomScorer.detectDryLips();
            }

            @Override
            public void onPostExecute() {
                super.onPostExecute();
                dismissDialog();
                lResult.setText(Constants.decimalFormat2.format(lDry[0]) + "% dryness detected");
                rResult.setText(Constants.decimalFormat2.format(rDry[0]) + "% dryness detected");
            }
        }.execute();
    }

    private void SetEyeRednessResult() {
        FaceSymptomScorer symptomScorer = new FaceSymptomScorer();
        final double[] lRed = {0};
        final double[] rRed = {0};
        Log.d(TAG, "SetEyeRednessResult: Model: " + model);
        new BackgroundWork(this) {
            @Override
            public void doInBackground() {
                super.doInBackground();
                symptomScorer.setBitmap(Utils.Uri2Bitmap(DualImageResult.this, Uri.parse(model.getLeftImgUri())));
                lRed[0] = symptomScorer.detectRedness();

                symptomScorer.setBitmap(Utils.Uri2Bitmap(DualImageResult.this, Uri.parse(model.getRightImgUri())));
                rRed[0] = symptomScorer.detectRedness();
            }

            @Override
            public void onPostExecute() {
                super.onPostExecute();
                dismissDialog();
                lResult.setText(Constants.decimalFormat2.format(lRed[0]) + "% redness detected");
                rResult.setText(Constants.decimalFormat2.format(rRed[0]) + "% redness detected");
            }
        }.execute();
    }

    private void SetEyebrowResult() {
        FaceSymptomScorer symptomScorer = new FaceSymptomScorer();
        final double[] lWhite = {0};
        final double[] rWhite = {0};

        new BackgroundWork(this) {
            @Override
            public void doInBackground() {
                super.doInBackground();
                symptomScorer.setBitmap(Utils.Uri2Bitmap(DualImageResult.this, Uri.parse(model.getLeftImgUri())));
                lWhite[0] = symptomScorer.detectLossBlackness();

                symptomScorer.setBitmap(Utils.Uri2Bitmap(DualImageResult.this, Uri.parse(model.getRightImgUri())));
                rWhite[0] = symptomScorer.detectLossBlackness();
            }

            @Override
            public void onPostExecute() {
                super.onPostExecute();
                dismissDialog();
                lResult.setText(Constants.decimalFormat2.format(lWhite[0]) + "% loss of blackness detected");
                rResult.setText(Constants.decimalFormat2.format(rWhite[0]) + "% loss of blackness detected");
            }
        }.execute();
    }

    private void RescanData() {
        reScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getIntent() != null && getIntent().hasExtra(Constants.DUAL_IMAGE_TEST)) {
                    String gson = getIntent().getStringExtra(Constants.DUAL_IMAGE_TEST);
                    Intent rescanIntent = new Intent(DualImageResult.this, DualRescanData.class);
                    rescanIntent.putExtra(Constants.DUAL_IMAGE_TEST, gson);
                    startActivityForResult(rescanIntent, RESCAN_REQUEST);
                }
            }
        });
    }

    private void SetIntentData() {
        if (model != null) {
            setTitle(model.getType());
            lImg.setImageURI(Uri.parse(model.getLeftDisplayImgUri()));
            rImg.setImageURI(Uri.parse(model.getRightDisplayImgUri()));

            lType.setText(model.getLeftImgType());
            rType.setText(model.getRightImgType());
        }
    }

    private void GetIntentData() {
        if (getIntent() != null && getIntent().hasExtra(Constants.DUAL_IMAGE_TEST)) {
            String gson = getIntent().getStringExtra(Constants.DUAL_IMAGE_TEST);
            model = new Gson().fromJson(gson, DualImageModel.class);
        }
    }

    private void _init() {
        lImg = findViewById(R.id.dual_image_res_l_img);
        rImg = findViewById(R.id.dual_image_res_r_img);

        LayoutUtils.fixRatioImageView(this, 2, new ImageView[]{lImg, rImg});

        lType = findViewById(R.id.dual_image_res_l_type);
        rType = findViewById(R.id.dual_image_res_r_type);

        lResult = findViewById(R.id.dual_image_res_l_result);
        rResult = findViewById(R.id.dual_image_res_r_result);

        reScan = findViewById(R.id.dual_image_res_rescan);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Fetching result");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
    }

    private void showDialog() {
        if (progressDialog != null) {
            progressDialog.show();
        }
    }

    private void dismissDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == RESCAN_REQUEST && data != null) {
            model = new Gson().fromJson(data.getStringExtra(Constants.DUAL_IMAGE_TEST), DualImageModel.class);
            SetIntentData();
            GetResult();
        }
    }
}