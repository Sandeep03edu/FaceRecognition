package com.sanedu.fcrecognition.AnalysisResult;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sanedu.fcrecognition.Constants;
import com.sanedu.fcrecognition.Face.AgeGenderDetection;
import com.sanedu.fcrecognition.Face.FaceDetection;
import com.sanedu.fcrecognition.Face.FaceLandmarks;
import com.sanedu.fcrecognition.Face.FaceParts;
import com.sanedu.fcrecognition.Model.FaceResult;
import com.sanedu.fcrecognition.Model.ResultConfidence;
import com.sanedu.fcrecognition.Model.DualImageModel;
import com.sanedu.fcrecognition.R;
import com.sanedu.fcrecognition.Utils.BackgroundWork;
import com.sanedu.fcrecognition.Utils.ImageResizer;
import com.sanedu.fcrecognition.Utils.LayoutUtils;
import com.sanedu.fcrecognition.Utils.Permission;
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
    private FaceResult faceResult = null;

    // Layout views
    CardView ageCard, genderCard;
    LinearLayout eyebrowLl, eyeLl, lipsLl;
    ImageView ageImg, genderImg, leftEyebrowImg, rightEyebrowImg, leftEyeImg, rightEyeImg, noseImg, upperLipImg, lowerLipImg, faceImg;
    TextView ageTv, genderTv, saveDataTv;

    // Face parts bitmaps
    Bitmap leftEyeBrow, rightEyeBrow, leftEye, rightEye, upperLip, lowerLip, nose;
    Bitmap displayLeftEyeBrow, displayRightEyeBrow, displayLeftEye, displayRightEye, displayUpperLip, displayLowerLip, displayNose;


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

        // Save Action();
        SaveData();
    }

    private void SaveData() {
        saveDataTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    if (Permission.CheckPermission(ResultPageActivity.this, Manifest.permission.FOREGROUND_SERVICE)) {
                        Intent intent = new Intent(ResultPageActivity.this, ResultUploadScreen.class);
                        intent.putExtra(Constants.RESULT_IMAGE_URI, imageUri.toString());
                        startActivity(intent);
                    } else {
                        Permission.RequestPermission(ResultPageActivity.this, new String[]{Manifest.permission.FOREGROUND_SERVICE});
                    }
                }
                else{
                    Intent intent = new Intent(ResultPageActivity.this, ResultUploadScreen.class);
                    intent.putExtra(Constants.RESULT_IMAGE_URI, imageUri.toString());
                    startActivity(intent);
                }
            }
        });
    }

    private void SetClickActions() {
        AgeGenderClickAction();

        DualImageResultProvider();
    }

    private void DualImageResultProvider() {
        Intent dualImgResIntent = new Intent(this, DualImageResult.class);

        if(faceResult!=null){
            dualImgResIntent.putExtra(Constants.INTENT_RESULT, new Gson().toJson(faceResult));
        }

        eyebrowLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri displayLeftUri = Utils.Bitmap2Uri(ResultPageActivity.this, displayLeftEyeBrow);
                Uri displayRightUri = Utils.Bitmap2Uri(ResultPageActivity.this, displayRightEyeBrow);
                Uri leftUri = Utils.Bitmap2Uri(ResultPageActivity.this, leftEyeBrow);
                Uri rightUri = Utils.Bitmap2Uri(ResultPageActivity.this, rightEyeBrow);
                DualImageModel model = new DualImageModel(Constants.EYE_BROW_TEST, String.valueOf(displayLeftUri), String.valueOf(displayRightUri), String.valueOf(leftUri), String.valueOf(rightUri), "Left Eyebrow", "Right Eyebrow");
                String gson = new Gson().toJson(model);
                dualImgResIntent.putExtra(Constants.DUAL_IMAGE_TEST, gson);
                startActivity(dualImgResIntent);
            }
        });

        eyeLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri displayLeftUri = Utils.Bitmap2Uri(ResultPageActivity.this, displayLeftEye);
                Uri displayRightUri = Utils.Bitmap2Uri(ResultPageActivity.this, displayRightEye);
                Uri leftUri = Utils.Bitmap2Uri(ResultPageActivity.this, leftEye);
                Uri rightUri = Utils.Bitmap2Uri(ResultPageActivity.this, rightEye);
                DualImageModel model = new DualImageModel(Constants.EYE_RED_TEST, String.valueOf(displayLeftUri), String.valueOf(displayRightUri), String.valueOf(leftUri), String.valueOf(rightUri), "Left Eye", "Right Eye");
                String gson = new Gson().toJson(model);
                dualImgResIntent.putExtra(Constants.DUAL_IMAGE_TEST, gson);
                startActivity(dualImgResIntent);
            }
        });

        lipsLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri displayLeftUri = Utils.Bitmap2Uri(ResultPageActivity.this, displayUpperLip);
                Uri displayRightUri = Utils.Bitmap2Uri(ResultPageActivity.this, displayLowerLip);
                Uri leftUri = Utils.Bitmap2Uri(ResultPageActivity.this, upperLip);
                Uri rightUri = Utils.Bitmap2Uri(ResultPageActivity.this, lowerLip);
                DualImageModel model = new DualImageModel(Constants.LIPS_TEST, String.valueOf(displayLeftUri), String.valueOf(displayRightUri), String.valueOf(leftUri), String.valueOf(rightUri), "Upper Lip", "Lower Lip");
                String gson = new Gson().toJson(model);
                dualImgResIntent.putExtra(Constants.DUAL_IMAGE_TEST, gson);
                startActivity(dualImgResIntent);
            }
        });
    }

    private void AgeGenderClickAction() {
        Intent ageGenderResIntent = new Intent(this, AgeGenderResCard.class);

        ageCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResultConfidence age;
                if (faceResult != null) {
                    age = new ResultConfidence(String.valueOf(faceResult.getAge()), -1);
                }
                else {
                    age = ageGenderDetection.getAgeGroup();
                }
                String gson = new Gson().toJson(age);
                ageGenderResIntent.putExtra(Constants.AG_MODEL, gson);
                ageGenderResIntent.putExtra(Constants.AG_TYPE, Constants.AGE);
                startActivity(ageGenderResIntent);
            }
        });

        genderCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResultConfidence gender;
                if (faceResult != null) {
                    gender = new ResultConfidence(String.valueOf(faceResult.getGender()), -1);
                }
                else {
                    gender = ageGenderDetection.getGender();
                }
                String gson = new Gson().toJson(gender);
                ageGenderResIntent.putExtra(Constants.AG_MODEL, gson);
                ageGenderResIntent.putExtra(Constants.AG_TYPE, Constants.GENDER);
                startActivity(ageGenderResIntent);
            }
        });
    }

    private void _init() {
        setTitle("Result");
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
        faceImg = findViewById(R.id.result_face_image);

        ageTv = findViewById(R.id.result_age_name);
        genderTv = findViewById(R.id.result_gender_name);
        saveDataTv = findViewById(R.id.result_save_data);

        LayoutUtils.fixRatioImageView(this, 2, new ImageView[]{ageImg, genderImg, leftEyebrowImg, rightEyebrowImg, leftEyeImg, rightEyeImg, noseImg, upperLipImg, lowerLipImg, faceImg});

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
                    saveDataTv.setVisibility(View.GONE);
                    Toast.makeText(ResultPageActivity.this, "No face found\nTry cropping image, face should be major part of the image", Toast.LENGTH_SHORT).show();

                    /*
                    // Accuracy testing purpose
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                    finish();
                     */

                } else if (faceCount > 1) {
                    dismissDialog();
                    saveDataTv.setVisibility(View.GONE);
                    Toast.makeText(ResultPageActivity.this, "More than 1 face detected\nTry cropping or blurring other faces", Toast.LENGTH_SHORT).show();

                    /*
                    // Accuracy testing purpose
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                    finish();
                     */

                } else {
                    DetectAgeGender();
                    DetectFaceParts();
                }
            }
        }.execute();
    }

    private void DetectFaceParts() {
        final Bitmap[] bitmap = new Bitmap[1];
        new BackgroundWork(this) {
            @Override
            public void doInBackground() {
                super.doInBackground();
                VisionDetRet face = faceDetection.getFace();
                faceParts = new FaceParts(imageUri.getPath(), face, originalBitmap);
                bitmap[0] = FaceLandmarks.getFaceBitmap(imageUri.getPath(), face);
                Log.d(TAG, "doInBackground: Landmarks detected: " + faceParts.getFaceLandmarks().size());
                Log.d(TAG, "doInBackground: FacePart: ImageUri: " + imageUri);
                Log.d(TAG, "doInBackground: FacePart: ImageUriPath: " + imageUri.getPath());
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
        Bitmap faceBitmap = faceParts.getDisplayFace();
        faceImg.setImageBitmap(faceBitmap);

        leftEyeBrow = faceParts.getLeftEyebrow();
        rightEyeBrow = faceParts.getRightEyebrow();
        leftEye = faceParts.getLeftEye();
        rightEye = faceParts.getRightEye();
        upperLip = faceParts.getUpperLip();
        lowerLip = faceParts.getLowerLip();
        nose = faceParts.getNose();

        displayLeftEyeBrow = faceParts.getDisplayLeftEyebrow();
        displayRightEyeBrow = faceParts.getDisplayRightEyebrow();
        displayLeftEye = faceParts.getDisplayLeftEye();
        displayRightEye = faceParts.getDisplayRightEye();
        displayUpperLip = faceParts.getDisplayUpperLip();
        displayLowerLip = faceParts.getDisplayLowerLip();
        displayNose = faceParts.getDisplayNose();

//        leftEyebrowImg.setImageBitmap(leftEyeBrow);
//        rightEyebrowImg.setImageBitmap(rightEyeBrow);
        leftEyebrowImg.setImageBitmap(displayLeftEyeBrow);
        rightEyebrowImg.setImageBitmap(displayRightEyeBrow);
        leftEyeImg.setImageBitmap(displayLeftEye);
        rightEyeImg.setImageBitmap(displayRightEye);
        upperLipImg.setImageBitmap(displayUpperLip);
        lowerLipImg.setImageBitmap(displayLowerLip);
        noseImg.setImageBitmap(displayNose);
    }

    private void DetectAgeGender() {
        // Setting prev scan data
        if (faceResult != null) {
            dismissDialog();

            if (faceResult.getGender().equalsIgnoreCase(Constants.MALE)) {
                genderImg.setImageResource(R.drawable.ic_baseline_male_128);
            } else if (faceResult.getGender().equalsIgnoreCase(Constants.FEMALE)) {
                genderImg.setImageResource(R.drawable.ic_baseline_female_128);
            }

            ageImg.setImageResource(R.drawable.age_vector);
            ageTv.setText(String.valueOf(faceResult.getAge()));
            genderTv.setText(faceResult.getGender());

            dataDetected++;

            if (dataDetected >= 2) {
                dismissDialog();
            }
            return;
        }


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

                if (ageGenderDetection.getGender().getResult().equalsIgnoreCase(Constants.MALE)) {
                    genderImg.setImageResource(R.drawable.ic_baseline_male_128);
                } else if (ageGenderDetection.getGender().getResult().equalsIgnoreCase(Constants.FEMALE)) {
                    genderImg.setImageResource(R.drawable.ic_baseline_female_128);
                }

                ageImg.setImageResource(R.drawable.age_vector);
                ageTv.setText(ageGenderDetection.getAgeGroup().getResult());
                genderTv.setText(ageGenderDetection.getGender().getResult());

                /*
                // Accuracy testing purspose
                Log.d("TESTING", "AGE: " + ageGenderDetection.getAgeGroup().getResult() + " " + ageGenderDetection.getAgeGroup().getConfidence() +"%");
                Log.d("TESTING", "GENDER: " + ageGenderDetection.getGender().getResult() + " " + ageGenderDetection.getGender().getConfidence() +"%");

                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                finish();
                 */
            }
        }.execute();
    }

    private void GetImageBitmap() {
        if (getIntent() != null) {
            bytes = getIntent().getByteArrayExtra(Constants.IMAGE_BITMAP_BYTES);
            originalBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            originalBitmap = ImageResizer.reduceBitmapSize(originalBitmap, 240000);
            imageUri = Utils.Bitmap2Uri(this, originalBitmap);

            if (getIntent().hasExtra(Constants.INTENT_RESULT)) {
                faceResult = new Gson().fromJson(getIntent().getStringExtra(Constants.INTENT_RESULT), FaceResult.class);
                saveDataTv.setVisibility(View.GONE);
            }
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