package com.sanedu.fcrecognition.AnalysisResult;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sanedu.fcrecognition.Constants;
import com.sanedu.fcrecognition.Face.AgeGenderDetection;
import com.sanedu.fcrecognition.Face.FaceDetection;
import com.sanedu.fcrecognition.Face.FaceParts;
import com.sanedu.fcrecognition.Model.AgeGender;
import com.sanedu.fcrecognition.R;
import com.sanedu.fcrecognition.Utils.BackgroundWork;
import com.sanedu.fcrecognition.Utils.ImageResizer;
import com.sanedu.fcrecognition.Utils.LayoutUtils;
import com.sanedu.fcrecognition.Utils.Utils;
import com.tzutalin.dlib.VisionDetRet;

public class ResultPageActivity extends AppCompatActivity {

    private static final String TAG = "ResultPageActivityTag";
    private byte[] bytes;
    private Bitmap originalBitmap;
    private FaceDetection faceDetection;
    private ProgressDialog progressDialog;
    private Uri imageUri;
    private FaceParts faceParts;
    private AgeGenderDetection ageGenderDetection;
    private int dataDetected = 0;

    // Layout views
    CardView ageCard, genderCard;
    LinearLayout eyebrowLl, eyeLl, lipsLl;
    ImageView ageImg, genderImg, leftEyebrowImg, rightEyebrowImg, leftEyeImg, rightEyeImg, noseImg, upperLipImg, lowerLipImg;
    TextView ageTv, genderTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_page);

        // Views initialisation
        _init();

        // Get Image Bitmap
        GetImageBitmap();

        // Check Num of faces
        CheckFacesCount();

        // Setting Click actions
        SetClickActions();
    }

    private void SetClickActions() {
        AgeGenderClickAction();
    }

    private void AgeGenderClickAction() {
        Intent ageGenderResIntent= new Intent(this, AgeGenderResCard.class);

        ageCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AgeGender age = ageGenderDetection.getAgeGroup();
                String gson = new Gson().toJson(age);
                ageGenderResIntent.putExtra(Constants.AG_MODEL, gson);
                ageGenderResIntent.putExtra(Constants.AG_TYPE, Constants.AGE);
                startActivity(ageGenderResIntent);
            }
        });

        genderCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AgeGender gender = ageGenderDetection.getGender();
                String gson = new Gson().toJson(gender);
                ageGenderResIntent.putExtra(Constants.AG_MODEL, gson);
                ageGenderResIntent.putExtra(Constants.AG_TYPE, Constants.GENDER);
                startActivity(ageGenderResIntent);
            }
        });
    }

    private void _init() {
        ageCard = findViewById(R.id.result_age_card);
        genderCard = findViewById(R.id.result_gender_card);

        eyebrowLl = findViewById(R.id.result_eyebrow_ll);
        eyeLl = findViewById(R.id.result_eye_ll);
        lipsLl = findViewById(R.id.result_lip_ll);

        ageImg = findViewById(R.id.result_age_image);
        genderImg = findViewById(R.id.result_gender_image);
        leftEyebrowImg = findViewById(R.id.result_left_eyebrow_image);
        rightEyebrowImg = findViewById(R.id.result_right_eyebrow_image);
        leftEyeImg = findViewById(R.id.result_left_eye_image);
        rightEyeImg = findViewById(R.id.result_right_eye_image);
        noseImg = findViewById(R.id.result_nose_image);
        upperLipImg = findViewById(R.id.result_upper_lip_image);
        lowerLipImg = findViewById(R.id.result_lower_lip_image);

        ageTv = findViewById(R.id.result_age_name);
        genderTv = findViewById(R.id.result_gender_name);

        LayoutUtils.fixRatioImageView(this, 2, new ImageView[]{ageImg, genderImg, leftEyebrowImg, rightEyebrowImg, leftEyeImg, rightEyeImg, noseImg, upperLipImg, lowerLipImg});

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Fetching data");
        progressDialog.setMessage("Please wait...");
    }


    private void CheckFacesCount() {
        showDialog();
        new BackgroundWork(this) {
            @Override
            public void doInBackground() {
                super.doInBackground();
                faceDetection = new FaceDetection(ResultPageActivity.this, imageUri);
            }

            @Override
            public void onPostExecute() {
                super.onPostExecute();
                int faceCount = faceDetection.faceCount();
                if (faceCount <= 0) {
                    dismissDialog();
                    Toast.makeText(ResultPageActivity.this, "No face found", Toast.LENGTH_SHORT).show();
                } else if (faceCount > 1) {
                    dismissDialog();
                    Toast.makeText(ResultPageActivity.this, "More than 1 face detected\nTry cropping or blurring other faces", Toast.LENGTH_SHORT).show();
                } else {
                    DetectAgeGender();
                    DetectFaceParts();
                }
            }
        }.execute();
    }

    private void DetectFaceParts() {
        new BackgroundWork(this) {
            @Override
            public void doInBackground() {
                super.doInBackground();
                VisionDetRet face = faceDetection.getFace();
                faceParts = new FaceParts(imageUri.getPath(), face, originalBitmap);
            }

            @Override
            public void onPostExecute() {
                super.onPostExecute();
                dataDetected++;
                SetFacePartsImage();

                if (dataDetected >= 2) {
                    dismissDialog();
                }
            }
        }.execute();
    }

    private void SetFacePartsImage() {
        Bitmap leftEyeBrow = faceParts.getLeftEyebrow();
        Bitmap rightEyeBrow = faceParts.getRightEyebrow();

        Bitmap leftEye = faceParts.getLeftEye();
        Bitmap rightEye = faceParts.getRightEye();

        Bitmap upperLip = faceParts.getUpperLip();
        Bitmap lowerLip = faceParts.getLowerLip();

        Bitmap nose = faceParts.getNose();

        leftEyebrowImg.setImageBitmap(leftEyeBrow);
        rightEyebrowImg.setImageBitmap(rightEyeBrow);

        leftEyeImg.setImageBitmap(leftEye);
        rightEyeImg.setImageBitmap(rightEye);

        upperLipImg.setImageBitmap(upperLip);
        lowerLipImg.setImageBitmap(lowerLip);

        noseImg.setImageBitmap(nose);
    }

    private void DetectAgeGender() {
        new BackgroundWork(this) {
            @Override
            public void doInBackground() {
                super.doInBackground();
                ageGenderDetection = new AgeGenderDetection(ResultPageActivity.this, originalBitmap);
            }

            @Override
            public void onPostExecute() {
                super.onPostExecute();
                dataDetected++;

                if (dataDetected >= 2) {
                    dismissDialog();
                }

                if (ageGenderDetection.getGender().getAgeGender().equalsIgnoreCase(Constants.MALE)) {
                    genderImg.setImageResource(R.drawable.ic_baseline_male_128);
                } else if (ageGenderDetection.getGender().getAgeGender().equalsIgnoreCase(Constants.FEMALE)) {
                    genderImg.setImageResource(R.drawable.ic_baseline_female_128);
                }

                ageImg.setImageResource(R.drawable.age_vector);

                ageTv.setText(ageGenderDetection.getAgeGroup().getAgeGender());
                genderTv.setText(ageGenderDetection.getGender().getAgeGender());
            }
        }.execute();
    }

    private void GetImageBitmap() {
        if (getIntent() != null) {
            bytes = getIntent().getByteArrayExtra(Constants.IMAGE_BITMAP_BYTES);
            originalBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            originalBitmap = ImageResizer.reduceBitmapSize(originalBitmap, 240000);
            imageUri = Utils.Bitmap2Uri(this, originalBitmap);
        }
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

}