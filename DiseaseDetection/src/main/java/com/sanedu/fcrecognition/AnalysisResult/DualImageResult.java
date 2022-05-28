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
import com.sanedu.common.Utils.Constants;
import com.sanedu.fcrecognition.Face.DetectEyeDisease;
import com.sanedu.fcrecognition.Face.FaceSymptomScorer;
import com.sanedu.fcrecognition.Model.DualImageModel;
import com.sanedu.fcrecognition.Model.FaceResult;
import com.sanedu.fcrecognition.Model.ResultConfidence;
import com.sanedu.fcrecognition.R;
import com.sanedu.common.Utils.BackgroundWork;
import com.sanedu.common.Utils.LayoutUtils;
import com.sanedu.fcrecognition.Utils.Utils;


/**
 * Dialog Activity to display dual image type predicted result
 * Like Eyebrows, Eyes and Lips results
 */
public class DualImageResult extends AppCompatActivity {

    private static final String TAG = "DualImageResultTag";

    ImageView lImg, rImg;
    TextView lType, rType, lResult, rResult, reScan;
    DualImageModel dualImageModel;
    private final int RESCAN_REQUEST = 121;
    private ProgressDialog progressDialog;
    private FaceResult faceResult = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dual_image_result);

        // Initialising views
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

    /**
     * Displaying fetched result from intent
     */
    private void GetResult() {
        // Checking whether result exist or not
        if (dualImageModel != null) {
            showDialog();
            String type = dualImageModel.getType(); // Dual result type
            if (type.equalsIgnoreCase(Constants.EYE_BROW_TEST)) {
                // Displaying Eyebrow Result
                SetEyebrowResult();
            } else if (type.equalsIgnoreCase(Constants.EYE_RED_TEST)) {
                // Displaying Eyes result
                SetEyeResult();
            } else if (type.equalsIgnoreCase(Constants.LIPS_TEST)) {
                // Displaying Lips result
                SetLipDryResult();
            }
        }
    }

    /**
     * Setting Lips result
     */
    private void SetLipDryResult() {
        // Set past result data
        if (faceResult != null) {
            dismissDialog();
            lResult.setText(faceResult.getUpperLipResult());
            rResult.setText(faceResult.getLowerLipResult());
            return;
        }

        // Setting new Result data
        FaceSymptomScorer symptomScorer = new FaceSymptomScorer();
        final double[] lDry = {0};
        final double[] rDry = {0};

        // Background task to fetch result
        new BackgroundWork(this) {
            @Override
            public void doInBackground() {
                super.doInBackground();
                // Calculating dryness score in background
                symptomScorer.setBitmap(Utils.Uri2Bitmap(DualImageResult.this, Uri.parse(dualImageModel.getLeftImgUri())));
                lDry[0] = symptomScorer.detectDryLips();

                symptomScorer.setBitmap(Utils.Uri2Bitmap(DualImageResult.this, Uri.parse(dualImageModel.getRightImgUri())));
                rDry[0] = symptomScorer.detectDryLips();
            }

            @Override
            public void onPostExecute() {
                super.onPostExecute();
                dismissDialog();
                // Setting dryness score on UI thread
                lResult.setText(Constants.decimalFormat2.format(lDry[0]) + "% dryness detected");
                rResult.setText(Constants.decimalFormat2.format(rDry[0]) + "% dryness detected");
            }
        }.execute();
    }

    private void SetEyeResult() {
        // Set past result data
        if (faceResult != null) {
            dismissDialog();
            lResult.setText(faceResult.getLeftEyeResult());
            rResult.setText(faceResult.getRightEyeResult());
            return;
        }

        // Setting new Result data

        final int[] count = {0}; // Variable used to calculate number of tasks done on eye

        final String[] leftResult = {""};
        final String[] rightResult = {""};

        final double[] lRed = {0};
        final double[] rRed = {0};

        final ResultConfidence[] lResultConfidence = new ResultConfidence[1];
        final ResultConfidence[] rResultConfidence = new ResultConfidence[1];
        FaceSymptomScorer symptomScorer = new FaceSymptomScorer();

        Log.d(TAG, "SetEyeRednessResult: Model: " + dualImageModel);

        // Background task to fetch result
        new BackgroundWork(this) {
            @Override
            public void doInBackground() {
                super.doInBackground();
                // Calculating redness score in background
                symptomScorer.setBitmap(Utils.Uri2Bitmap(DualImageResult.this, Uri.parse(dualImageModel.getRightImgUri())));
                rRed[0] = symptomScorer.detectRedness();

                symptomScorer.setBitmap(Utils.Uri2Bitmap(DualImageResult.this, Uri.parse(dualImageModel.getLeftImgUri())));
                lRed[0] = symptomScorer.detectRedness();
            }

            @Override
            public void onPostExecute() {
                super.onPostExecute();
                dismissDialog();
                // Setting redness score in UI Thread
                leftResult[0] = Constants.decimalFormat2.format(lRed[0]) + "% redness detected\n";
                rightResult[0] = Constants.decimalFormat2.format(rRed[0]) + "% redness detected\n";
                lResult.append(leftResult[0]);
                rResult.append(rightResult[0]);

                // Incrementing count by 2 for completing 2 tasks in background thread
                count[0] += 2;

                // Dismissing ProgressDialog if all tests are done
                if (count[0] >= 4) {
                    dismissDialog();
                }
            }
        }.execute();

        // Implementing Eye disease model
        DetectEyeDisease detectEyeDisease = new DetectEyeDisease();

        // Setting left eye bitmap
        detectEyeDisease.setBitmap(DualImageResult.this, Utils.Uri2Bitmap(DualImageResult.this, Uri.parse(dualImageModel.getLeftImgUri())));

        // Fetching result
        detectEyeDisease.getResult(new DetectEyeDisease.ExecutorListener() {
            @Override
            public void onExecutionComplete(ResultConfidence resultConfidence) {
                // Fetching result when execution is completed
                lResultConfidence[0] = resultConfidence;

                // Displaying result if execution is completed successfully
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Append result data
                        lResult.append(lResultConfidence[0].getConfidence() + "% chances of " + lResultConfidence[0].getResult() + "\n");

                        // Incrementing count by 1 for completing 1 tasks in background thread
                        count[0]++;

                        // Dismissing ProgressDialog if all tests are done
                        if (count[0] >= 4) {
                            dismissDialog();
                        }
                    }
                });
            }

            @Override
            public void onExecutionFailed(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Incrementing count by 1 for failing to complete 1 tasks in background thread
                        count[0]++;

                        // Dismissing ProgressDialog if all tests are done
                        if (count[0] >= 4) {
                            dismissDialog();
                        }
                    }
                });
                Log.e(TAG, "onExecutionFailed: Left Err ", e);
            }
        });

        // Setting right eye bitmap
        detectEyeDisease.setBitmap(DualImageResult.this, Utils.Uri2Bitmap(DualImageResult.this, Uri.parse(dualImageModel.getRightImgUri())));

        // Fetching result
        detectEyeDisease.getResult(new DetectEyeDisease.ExecutorListener() {
            @Override
            public void onExecutionComplete(ResultConfidence resultConfidence) {
                // Fetching result when execution is completed
                rResultConfidence[0] = resultConfidence;

                // Displaying result if execution is completed successfully
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Append result data
                        rResult.append(rResultConfidence[0].getConfidence() + "% chances of " + rResultConfidence[0].getResult() + "\n");

                        // Incrementing count by 1 for completing 1 tasks in background thread
                        count[0]++;

                        // Dismissing ProgressDialog if all tests are done
                        if (count[0] >= 4) {
                            dismissDialog();
                        }
                    }
                });
            }

            @Override
            public void onExecutionFailed(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Incrementing count by 1 for failing to complete 1 tasks in background thread
                        count[0]++;

                        // Dismissing ProgressDialog if all tests are done
                        if (count[0] >= 4) {
                            dismissDialog();
                        }
                    }
                });
                Log.e(TAG, "onExecutionFailed: Right Err ", e);
            }
        });
    }


    /**
     * Setting Eyebrows result
     */
    private void SetEyebrowResult() {
        // Set past result data
        if (faceResult != null) {
            dismissDialog();
            lResult.setText(faceResult.getLeftEyebrowResult());
            rResult.setText(faceResult.getRightEyebrowResult());
            return;
        }

        // Setting new Result data
        FaceSymptomScorer symptomScorer = new FaceSymptomScorer();
        final double[] lWhite = {0};
        final double[] rWhite = {0};

        lResult.setText("");
        rResult.setText("");

        // Background task to fetch result
        new BackgroundWork(this) {
            @Override
            public void doInBackground() {
                super.doInBackground();
                // Calculating blackness loss score in background
                symptomScorer.setBitmap(Utils.Uri2Bitmap(DualImageResult.this, Uri.parse(dualImageModel.getLeftImgUri())));
                lWhite[0] = symptomScorer.detectLossBlackness();

                symptomScorer.setBitmap(Utils.Uri2Bitmap(DualImageResult.this, Uri.parse(dualImageModel.getRightImgUri())));
                rWhite[0] = symptomScorer.detectLossBlackness();
            }

            @Override
            public void onPostExecute() {
                super.onPostExecute();
                // Setting blackness loss score on UI thread
                dismissDialog();
                lResult.setText(Constants.decimalFormat2.format(lWhite[0]) + "% loss of blackness detected");
                rResult.setText(Constants.decimalFormat2.format(rWhite[0]) + "% loss of blackness detected");
            }
        }.execute();
    }

    /**
     * Function to Rescan data
     */
    private void RescanData() {
        reScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Checking whether prev result exist or not
                if (getIntent() != null && getIntent().hasExtra(Constants.DUAL_IMAGE_TEST)) {
                    String gson = getIntent().getStringExtra(Constants.DUAL_IMAGE_TEST);
                    Intent rescanIntent = new Intent(DualImageResult.this, DualRescanData.class);
                    rescanIntent.putExtra(Constants.DUAL_IMAGE_TEST, gson);

                    // Sending prev result data for new scan and result
                    startActivityForResult(rescanIntent, RESCAN_REQUEST);
                }
            }
        });
    }

    /**
     * Setting intent fetched data
     */
    private void SetIntentData() {
        // Checking whether prev result exist or not
        if (dualImageModel != null) {
            // Setting dialog title as model type
            setTitle(dualImageModel.getType());

            // Displaying Display left and right image from Uri
            if(!dualImageModel.getLeftDisplayImgUri().trim().isEmpty())
                lImg.setImageURI(Uri.parse(dualImageModel.getLeftDisplayImgUri()));
            if(!dualImageModel.getRightDisplayImgUri().trim().isEmpty())
                rImg.setImageURI(Uri.parse(dualImageModel.getRightDisplayImgUri()));

            // Setting left and right image type
            lType.setText(dualImageModel.getLeftImgType());
            rType.setText(dualImageModel.getRightImgType());
        }
    }

    /**
     * Gettind prev data using Intent
     */
    private void GetIntentData() {
        // Checking whether data exist or not
        if (getIntent() != null && getIntent().hasExtra(Constants.DUAL_IMAGE_TEST)) {
            String gson = getIntent().getStringExtra(Constants.DUAL_IMAGE_TEST);
            dualImageModel = new Gson().fromJson(gson, DualImageModel.class);
        }

        // Removing rescan button from CardView if past scan history result exist
        if (getIntent() != null && getIntent().hasExtra(Constants.INTENT_RESULT)) {
            String gson = getIntent().getStringExtra(Constants.INTENT_RESULT);
            faceResult = new Gson().fromJson(gson, FaceResult.class);
            reScan.setVisibility(View.GONE);
        }
    }

    /**
     * Initialsing views
     */
    private void _init() {
        lImg = findViewById(R.id.dual_image_res_l_img);
        rImg = findViewById(R.id.dual_image_res_r_img);

        lType = findViewById(R.id.dual_image_res_l_type);
        rType = findViewById(R.id.dual_image_res_r_type);

        lResult = findViewById(R.id.dual_image_res_l_result);
        rResult = findViewById(R.id.dual_image_res_r_result);

        reScan = findViewById(R.id.dual_image_res_rescan);

        // Fixing imageViews dimensions as square with half width of screen
        LayoutUtils.fixRatioImageView(this, 2, new ImageView[]{lImg, rImg});

        // Initialising progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Fetching result");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
    }

    /**
     * Displaying dialog if not null
     */
    private void showDialog() {
        if (progressDialog != null) {
            progressDialog.show();
        }
    }

    /**
     * Removing dialog if not null
     */
    private void dismissDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
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
        // Setting Ui data based on Rescanned new images
        if (resultCode == RESULT_OK && requestCode == RESCAN_REQUEST && data != null) {
            faceResult = null;
            dualImageModel = new Gson().fromJson(data.getStringExtra(Constants.DUAL_IMAGE_TEST), DualImageModel.class);
            SetIntentData();
            GetResult();
        }
    }
}