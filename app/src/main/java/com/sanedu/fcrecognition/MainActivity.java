/*
 *  Copyright (C) 2015-present TzuTaLin
 */

package com.sanedu.fcrecognition;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tzutalin.dlib.Constants;
import com.tzutalin.dlib.FaceDet;
import com.tzutalin.dlib.PedestrianDet;
import com.tzutalin.dlib.VisionDetRet;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final int RESULT_LOAD_IMG = 1;
    private static final int REQUEST_CODE_PERMISSION = 2;

    private static final String TAG = "MainActivityTag";

    // Storage Permissions
    private static String[] PERMISSIONS_REQ = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    protected String mTestImgPath;
    protected FloatingActionButton mFabActionBt;
    FaceDet mFaceDet;
    PedestrianDet mPersonDet;
    ImageView imageView;

    BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(MainActivity.this) {
        @Override
        public void onManagerConnected(int status) {
            super.onManagerConnected(status);
            switch (status) {
                case BaseLoaderCallback.SUCCESS:
                    break;
                default:
                    super.onManagerConnected(status);
                    Log.d(TAG, "onManagerConnected: Status : " + status);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFabActionBt = findViewById(R.id.fab);
        imageView = findViewById(R.id.main_image);


        // Testing
        /*
        if(true){
            startActivity(new Intent(getApplicationContext(), FreeCropTest.class));
            finish();
            return;
        }
        */

        isExternalStorageWritable();
        isExternalStorageReadable();

        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, baseLoaderCallback);
        }
        else {
            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

        // For API 23+ you need to request the read/write permissions even if they are already in your manifest.
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.M) {
            verifyPermissions(this);
        }

        mFabActionBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchGallery();
            }
        });
    }


    protected void launchGallery() {
        Toast.makeText(MainActivity.this, "Pick one image", Toast.LENGTH_SHORT).show();
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    /**
     * Checks if the app has permission to write to device storage or open camera
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    
    private static boolean verifyPermissions(Activity activity) {
        // Check if we have write permission
        int write_permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int read_persmission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (write_permission != PackageManager.PERMISSION_GRANTED ||
                read_persmission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_REQ,
                    REQUEST_CODE_PERMISSION
            );
            return false;
        } else {
            return true;
        }
    }

    /* Checks if external storage is available for read and write */

    private void isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
        }
    }

    /* Checks if external storage is available to at least read */

    private void isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSION) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
                // Get the Image from data
                Uri selectedImage = data.getData();

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mTestImgPath = cursor.getString(columnIndex);
                cursor.close();
                if (mTestImgPath != null) {
                    runDetectAsync(mTestImgPath, selectedImage);
                }
            } else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong\n" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private ProgressDialog mDialog;
    @NonNull
    protected void runDetectAsync(@NonNull String imgPath, @NonNull Uri imageUri) {

        final String targetPath = Constants.getFaceShapeModelPath();
        if (!new File(targetPath).exists()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "Copy landmark model to " + targetPath, Toast.LENGTH_SHORT).show();
                }
            });
            FileUtils.copyFileFromRawToOthers(getApplicationContext(), R.raw.shape_predictor_68_face_landmarks, targetPath);
        }

        // Init
        if (mPersonDet == null) {
            mPersonDet = new PedestrianDet();
        }
        if (mFaceDet == null) {
            mFaceDet = new FaceDet(Constants.getFaceShapeModelPath());
        }

        List<VisionDetRet> faceList = mFaceDet.detect(imgPath);
        Bitmap bitmap = null;
        if (faceList.size() > 0) {
            bitmap = drawRect(imgPath, faceList, Color.GREEN);
            FaceParts faceParts = new FaceParts(imgPath, faceList.get(0), Utils.Uri2Bitmap(this, imageUri));
//            ArrayList<Point> landMarks = faceParts.getFaceLandmarks();
//            Bitmap bm = faceParts.getLeftEye();
//            if(bm!=null) {
//                imageView.setImageBitmap(bm);
//            }
//            else{
//                Log.d(TAG, "runDetectAsync: Null bitmap");
//            }
            imageView.setImageBitmap(faceParts.getRightEye());
            CheckEyeRedness(faceParts);
            // Calculating Eyebrow color
//            CheckEyebrow(faceParts);


        }
        else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "No face", Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    private void CheckEyeRedness(FaceParts faceParts) {
        FaceSymptomScorer eyeRednessScorer = new FaceSymptomScorer();
        eyeRednessScorer.setBitmap(faceParts.getLeftEye());
        int redScore = eyeRednessScorer.detectRedness();
        eyeRednessScorer.setBitmap(faceParts.getRightEye());
        redScore+= eyeRednessScorer.detectRedness();
        redScore/=2;

        Log.d(TAG, "CheckEyeRedness: Redness: " + redScore);
        Toast.makeText(this, "Red: " + redScore, Toast.LENGTH_SHORT).show();

        imageView.setImageBitmap(faceParts.getRightEye());
    }

    private void CheckEyebrow(FaceParts faceParts) {
        Bitmap leftEyeBrow = faceParts.getLeftEyebrow();
        Bitmap rightEyeBrow = faceParts.getRightEyebrow();

        int nonZero = 0;

        FaceSymptomScorer faceSymptomScorer = new FaceSymptomScorer();
        faceSymptomScorer.setBitmap(leftEyeBrow);
        nonZero = faceSymptomScorer.detectColor();
        faceSymptomScorer.setBitmap(rightEyeBrow);
        nonZero += faceSymptomScorer.detectColor();

        Log.d(TAG, "CheckEyebrow: NonZero: " + nonZero);

        Toast.makeText(this, "Count: " + nonZero, Toast.LENGTH_SHORT).show();
    }


    @UiThread

    protected void showDiaglog() {
        mDialog = ProgressDialog.show(MainActivity.this, "Wait", "Face detection", true);
    }

    @UiThread
    protected void dismissDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    
    protected Bitmap drawRect(String path, List<VisionDetRet> results, int color) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        Bitmap bm = BitmapFactory.decodeFile(path, options);
        android.graphics.Bitmap.Config bitmapConfig = bm.getConfig();
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bm = bm.copy(bitmapConfig, true);
        int width = bm.getWidth();
        int height = bm.getHeight();
        // By ratio scale
        float aspectRatio = bm.getWidth() / (float) bm.getHeight();

        final int MAX_SIZE = 512;
        int newWidth = MAX_SIZE;
        int newHeight = MAX_SIZE;
        float resizeRatio = 1;
        newHeight = Math.round(newWidth / aspectRatio);
        if (bm.getWidth() > MAX_SIZE && bm.getHeight() > MAX_SIZE) {
            bm = getResizedBitmap(bm, newWidth, newHeight);
            resizeRatio = (float) bm.getWidth() / (float) width;
        }

        // Create canvas to draw
        Canvas canvas = new Canvas(bm);
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
        // Loop result list
        for (VisionDetRet ret : results) {
            Rect bounds = new Rect();
            bounds.left = (int) (ret.getLeft() * resizeRatio);
            bounds.top = (int) (ret.getTop() * resizeRatio);
            bounds.right = (int) (ret.getRight() * resizeRatio);
            bounds.bottom = (int) (ret.getBottom() * resizeRatio);
            canvas.drawRect(bounds, paint);
            // Get landmark
            ArrayList<Point> landmarks = ret.getFaceLandmarks();
            String TAG = "LandmarksTag";
            Log.d(TAG, "drawRect: Size: " + landmarks.size());
            int count = 0;
            for (Point point : landmarks) {
                int pointX = (int) (point.x * resizeRatio);
                int pointY = (int) (point.y * resizeRatio);
                if (count < 22 && count>=17) {
                    canvas.drawCircle(pointX, pointY, 2, paint);
                }
                count++;
            }
        }

        return bm;
    }

    
    protected Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
        return resizedBitmap;
    }
}
