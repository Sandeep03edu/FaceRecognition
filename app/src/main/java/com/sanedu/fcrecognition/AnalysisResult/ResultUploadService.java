package com.sanedu.fcrecognition.AnalysisResult;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.security.identity.ResultData;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.sanedu.fcrecognition.Constants;
import com.sanedu.fcrecognition.Face.DetectEyeDisease;
import com.sanedu.fcrecognition.Face.FaceDetection;
import com.sanedu.fcrecognition.Face.FaceParts;
import com.sanedu.fcrecognition.FaceSymptomScorer;
import com.sanedu.fcrecognition.Firebase.FireStorage;
import com.sanedu.fcrecognition.Firebase.FirestoreData;
import com.sanedu.fcrecognition.Model.FaceResult;
import com.sanedu.fcrecognition.Model.LabelledImage;
import com.sanedu.fcrecognition.Model.ResultConfidence;
import com.sanedu.fcrecognition.Model.User;
import com.sanedu.fcrecognition.R;
import com.sanedu.fcrecognition.Start.SplashActivity;
import com.sanedu.fcrecognition.Utils.BackgroundWork;
import com.sanedu.fcrecognition.Utils.SharedPrefData;
import com.sanedu.fcrecognition.Utils.Utils;
import com.tzutalin.dlib.VisionDetRet;

import java.util.ArrayList;

public class ResultUploadService extends Service {

    private static final String TAG = "ResultUploadServiceTag";
    private FaceParts faceParts = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null || !intent.hasExtra(Constants.RESULT_DATA) || !intent.hasExtra(Constants.RESULT_IMAGE_URI)) {
            StopService();
            return START_REDELIVER_INTENT;
        }

        Intent notificationIntent = new Intent(this, SplashActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, Constants.CHANNEL_ID)
                .setContentText("Name")
                .setContentText("Result uploading...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(Constants.FOREGROUND_NOTIFICATION_ID, notification);

        String gson = intent.getStringExtra(Constants.RESULT_DATA);
        if (gson == null || gson.trim().isEmpty()) {
            StopService();
            return START_REDELIVER_INTENT;
        }

        FaceResult faceResult = new Gson().fromJson(gson, FaceResult.class);
        if (faceResult == null) {
            StopService();
            return START_REDELIVER_INTENT;
        }

        // Start Scanning and storing full face data
        String imageUriStr = intent.getStringExtra(Constants.RESULT_IMAGE_URI);
        faceResult.setImageUrl(imageUriStr);
        Uri imageUri = Uri.parse(imageUriStr);
        Bitmap imageBitmap = Utils.Uri2Bitmap(this, imageUri);
        FaceDetection faceDetection = new FaceDetection(ResultUploadService.this, imageUri);
        VisionDetRet face = faceDetection.getFace();
        faceParts = new FaceParts(imageUri.getPath(), face, imageBitmap);

        Bitmap leftEyeBrow = faceParts.getLeftEyebrow();
        Bitmap rightEyeBrow = faceParts.getRightEyebrow();
        Bitmap leftEye = faceParts.getLeftEye();
        Bitmap rightEye = faceParts.getRightEye();
        Bitmap upperLip = faceParts.getUpperLip();
        Bitmap lowerLip = faceParts.getLowerLip();
        Bitmap displayLeftEye = faceParts.getDisplayLeftEye();
        Bitmap displayRightEye = faceParts.getDisplayRightEye();

        FaceSymptomScorer symptomScorer = new FaceSymptomScorer();
        final double[] lDry = {0};
        final double[] rDry = {0};
        final double[] lWhite = {0};
        final double[] rWhite = {0};
        final double[] lRed = {0};
        final double[] rRed = {0};


        // Lips testing
        symptomScorer.setBitmap(upperLip);
        lDry[0] = symptomScorer.detectDryLips();
        symptomScorer.setBitmap(lowerLip);
        rDry[0] = symptomScorer.detectDryLips();
        faceResult.setLowerLipResult(Constants.decimalFormat2.format(lDry[0]) + "% dryness detected");
        faceResult.setUpperLipResult(Constants.decimalFormat2.format(rDry[0]) + "% dryness detected");

        // Eyebrow testing
        symptomScorer.setBitmap(leftEyeBrow);
        lWhite[0] = symptomScorer.detectLossBlackness();
        symptomScorer.setBitmap(rightEyeBrow);
        rWhite[0] = symptomScorer.detectLossBlackness();
        faceResult.setLeftEyebrowResult(Constants.decimalFormat2.format(lWhite[0]) + "% loss of blackness detected");
        faceResult.setRightEyebrowResult(Constants.decimalFormat2.format(rWhite[0]) + "% loss of blackness detected");

        // Eye Redness testing
        symptomScorer.setBitmap(leftEye);
        lRed[0] = symptomScorer.detectRedness();
        symptomScorer.setBitmap(rightEye);
        rRed[0] = symptomScorer.detectRedness();
        faceResult.updateLeftEyeResult(Constants.decimalFormat2.format(lRed[0]) + "% redness detected\n");
        faceResult.updateRightEyeResult(Constants.decimalFormat2.format(rRed[0]) + "% redness detected\n");

        // Eye Disease detection
        final int[] count = {0};
        DetectEyeDisease detectEyeDisease = new DetectEyeDisease();
        detectEyeDisease.setBitmap(this, displayLeftEye);
        detectEyeDisease.getResult(new DetectEyeDisease.ExecutorListener() {
            @Override
            public void onExecutionComplete(ResultConfidence resultConfidence) {
                count[0]++;
                faceResult.updateLeftEyeResult(resultConfidence.getConfidence() + "% chances of " + resultConfidence.getResult() + "\n");

                if (count[0] >= 2) {
                    UploadData(faceResult);
                }
            }

            @Override
            public void onExecutionFailed(Exception e) {
                count[0]++;
                if (count[0] >= 2) {
                    UploadData(faceResult);
                }
            }
        });

        detectEyeDisease.setBitmap(this, displayRightEye);
        detectEyeDisease.getResult(new DetectEyeDisease.ExecutorListener() {
            @Override
            public void onExecutionComplete(ResultConfidence resultConfidence) {
                count[0]++;
                faceResult.updateRightEyeResult(resultConfidence.getConfidence() + "% chances of " + resultConfidence.getResult() + "\n");
                if (count[0] >= 2) {
                    UploadData(faceResult);
                }
            }

            @Override
            public void onExecutionFailed(Exception e) {
                count[0]++;
                if (count[0] >= 2) {
                    UploadData(faceResult);
                }
            }
        });


        return START_STICKY;
    }

    private void UploadData(FaceResult faceResult) {
        String imageFolder = "Result/" + faceResult.getResultId() + "/OriginalImage";
        String imageFileName = faceResult.getUploadTime() + "_image";

        FireStorage fireStorage = new FireStorage(this);

        fireStorage.uploadImage(Utils.Uri2Bitmap(this, Uri.parse(faceResult.getImageUrl())),
                imageFolder,
                imageFileName,
                new FireStorage.ImageUploadListener() {
                    @Override
                    public void getDownloadUrl(ArrayList<LabelledImage> labelledImage) {
                        if (labelledImage.get(0).getImageName().equalsIgnoreCase(imageFileName)) {
                            faceResult.setImageUrl(labelledImage.get(0).getImageUrl());
                        }

                        FirestoreData firestoreData = new FirestoreData();
                        firestoreData.uploadRecord(faceResult, new FirestoreData.FirestoreListener() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "onSuccess: Success Upload");
                                StopService();
                            }

                            @Override
                            public void onFailure(String e) {
                                Log.e(TAG, "onFailure: " + e);
                                StopService();
                            }
                        });
                    }

                    @Override
                    public void onErrorUpload(String err) {
                        StopService();
                        Log.e(TAG, "onFailure: " + err);
                    }
                }
        );

        /*
        String faceFolder = "Result/" + faceResult.getResultId() + "/FaceImage";
        String faceFileName = faceResult.getUploadTime() + "_face";

        fireStorage.uploadMultiImages(this,
                new String[]{faceResult.getImageUrl(), faceResult.getFaceImageUrl()},
                new String[]{imageFolder, faceFolder},
                new String[]{imageFileName, faceFileName},
                new FireStorage.ImageUploadListener() {
                    @Override
                    public void getDownloadUrl(ArrayList<LabelledImage> imageUrl) {
                        for (LabelledImage labelledImage : imageUrl) {
                            if (labelledImage.getImageName().equalsIgnoreCase(imageFileName)) {
                                faceResult.setImageUrl(labelledImage.getImageUrl());
                            }
                        }

                        FirestoreData firestoreData = new FirestoreData();
                        firestoreData.uploadRecord(faceResult, new FirestoreData.FirestoreListener() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "onSuccess: Success Upload");
                                StopService();
                            }

                            @Override
                            public void onFailure(String e) {
                                Log.e(TAG, "onFailure: " + e);
                                StopService();
                            }
                        });
                    }

                    @Override
                    public void onErrorUpload(String err) {
                        Log.e(TAG, "onErrorUpload: " + err);
                        StopService();
                    }
                }
        );

         */
    }

    private void StopService() {
        stopForeground(true);
        stopSelf();
    }
}
