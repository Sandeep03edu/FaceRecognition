package com.sanedu.fcrecognition.AnalysisResult;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.sanedu.common.Utils.Constants;
import com.sanedu.common.Utils.ImageResizer;
import com.sanedu.fcrecognition.Face.DetectEyeDisease;
import com.sanedu.fcrecognition.Face.FaceDetection;
import com.sanedu.fcrecognition.Face.FaceParts;
import com.sanedu.fcrecognition.Face.FaceSymptomScorer;
import com.sanedu.fcrecognition.Firebase.FireStorage;
import com.sanedu.fcrecognition.Firebase.FirestoreData;
import com.sanedu.fcrecognition.Model.FaceResult;
import com.sanedu.fcrecognition.Model.LabelledImage;
import com.sanedu.fcrecognition.Model.ResultConfidence;
import com.sanedu.fcrecognition.R;
import com.sanedu.fcrecognition.Start.SplashActivity;
import com.sanedu.fcrecognition.Utils.Utils;
import com.tzutalin.dlib.VisionDetRet;

import java.io.ByteArrayOutputStream;
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
        // Checking whether intent is  not null and has data
        if (intent == null || !intent.hasExtra(Constants.RESULT_DATA) || !intent.hasExtra(Constants.RESULT_IMAGE_URI)) {
            FailedNotification();
            StopService();
            return START_REDELIVER_INTENT;
        }

        // Adding foreground service notification
        StartForegroundNotification();

        // getting data from intent
        String gson = intent.getStringExtra(Constants.RESULT_DATA);
        if (gson == null || gson.trim().isEmpty()) {
            FailedNotification();
            StopService();
            return START_REDELIVER_INTENT;
        }

        // getting data from intent
        FaceResult faceResult = new Gson().fromJson(gson, FaceResult.class);
        if (faceResult == null) {
            FailedNotification();
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
                // Incrementing count[0] by 1 when 1 task is completed
                count[0]++;
                faceResult.updateLeftEyeResult(resultConfidence.getConfidence() + "% chances of " + resultConfidence.getResult() + "\n");

                // Updating result to server if both tasks are done
                if (count[0] >= 2) {
                    UploadData(faceResult, imageBitmap);
                }
            }

            @Override
            public void onExecutionFailed(Exception e) {
                // Incrementing count[0] by 1 when 1 task is failed
                count[0]++;

                // Updating result to server if both tasks are done
                if (count[0] >= 2) {
                    UploadData(faceResult, imageBitmap);
                }
            }
        });

        detectEyeDisease.setBitmap(this, displayRightEye);
        detectEyeDisease.getResult(new DetectEyeDisease.ExecutorListener() {
            @Override
            public void onExecutionComplete(ResultConfidence resultConfidence) {
                // Incrementing count[0] by 1 when 1 task is completed
                count[0]++;
                faceResult.updateRightEyeResult(resultConfidence.getConfidence() + "% chances of " + resultConfidence.getResult() + "\n");

                // Updating result to server if both tasks are done
                if (count[0] >= 2) {
                    UploadData(faceResult, imageBitmap);
                }
            }

            @Override
            public void onExecutionFailed(Exception e) {
                // Incrementing count[0] by 1 when 1 task is completed
                count[0]++;

                // Updating result to server if both tasks are done
                if (count[0] >= 2) {
                    UploadData(faceResult, imageBitmap);
                }
            }
        });

        return START_STICKY;
    }

    /**
     * Function to start foreground service with notification
     */
    private void StartForegroundNotification() {
        Intent notificationIntent = new Intent(this, SplashActivity.class);
        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        }
        else{
            pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        }

        Notification notification = new NotificationCompat.Builder(this, Constants.CHANNEL_ID)
                .setContentTitle("Result uploading")
                .setContentText("Please wait...")
                .setSmallIcon(R.mipmap.main_launcher)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(Constants.FOREGROUND_NOTIFICATION_ID, notification);
    }

    /**
     * Function to upload data
     * @param faceResult - Result to be stored as a document in Firestore
     * @param imageBimap - imagefile to be stored by bytes in FireStorage
     */
    private void UploadData(FaceResult faceResult, Bitmap imageBimap) {
        String imageFolder = "Result/" + faceResult.getResultId() + "/OriginalImage";
        String imageFileName = faceResult.getUploadTime() + "_image";

        // Uploading file to storage
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

                        // Uploading file to Firestore
                        FirestoreData firestoreData = new FirestoreData();
                        firestoreData.uploadRecord(faceResult, new FirestoreData.FirestoreListener() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "onSuccess: Success Upload");
                                // Sending success notification
                                SuccessNotification(imageBimap, faceResult);
                                StopService();
                            }

                            @Override
                            public void onFailure(String e) {
                                Log.e(TAG, "onFailure: " + e);
                                // Sending failed notification
                                FailedNotification();
                                StopService();
                            }
                        });
                    }

                    @Override
                    public void onErrorUpload(String err) {
                        // Sending failed notification
                        FailedNotification();
                        StopService();
                        Log.e(TAG, "onFailure: " + err);
                    }
                }
        );
    }

    /**
     * Function to self stop the service
     */
    private void StopService() {
        stopForeground(true);
        stopSelf();
    }

    /**
     * Function to send SuccessNotification
     * @param bitmap - Adding bitmap for pending intent click action
     * @param faceResult - Adding faceResult for pending intent click action
     */
    private void SuccessNotification(Bitmap bitmap, FaceResult faceResult){
        // Compressing Bitmap to bytes array
        bitmap = ImageResizer.reduceBitmapSize(bitmap, 240000);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();

        // Adding resultIntent data
        Intent resultIntent = new Intent(this, ResultPageActivity.class);
        resultIntent.putExtra(Constants.IMAGE_BITMAP_BYTES, bytes);
        resultIntent.putExtra(Constants.INTENT_RESULT, new Gson().toJson(faceResult));

        // Setting up pending Intent
        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_IMMUTABLE);
        }
        else{
            pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, 0);
        }

        // Sending Success notification
        Notification notification = new NotificationCompat.Builder(this, Constants.RESULT_CHANNEL_ID)
                .setContentTitle("Result upload successful")
                .setContentText("Tap to check results")
                .setSmallIcon(R.mipmap.main_launcher)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .build();
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, notification);
    }

    /**
     * Function to send Failed Notification
     */
    private void FailedNotification(){
        // Sending failed notification
        Notification notification = new NotificationCompat.Builder(this, Constants.RESULT_CHANNEL_ID)
                .setContentTitle("Result upload failed")
                .setContentText("An error occured")
                .setSmallIcon(R.mipmap.main_launcher)
                .setPriority(Notification.PRIORITY_HIGH)
                .build();

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, notification);
    }
}