package com.sanedu.smartadvertising;

import static org.opencv.core.Core.flip;

import static java.lang.Thread.sleep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sanedu.common.Utils.BackgroundWork;
import com.sanedu.common.Utils.Constants;
import com.sanedu.common.Utils.Permission;
import com.sanedu.smartadvertising.MlModel.AgeGenderDetection;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "HomeActivityTag";
    CameraBridgeViewBase cameraBridgeViewBase;
    BaseLoaderCallback baseLoaderCallback;
    int activeBackCamera = CameraBridgeViewBase.CAMERA_ID_FRONT;
    Mat mRgbaT;
    Mat frame = null;
    AgeGenderDetection ageGenderDetection;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialising views
        _init();

        // Initialising openCv camera
        initOpenCvCamera();

        // Checking camera permission
        if (!Permission.CheckPermission(this, Manifest.permission.CAMERA) ||
                !Permission.CheckPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Permission.RequestPermission(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE});
            return;
        } else {
            // Initialising JavaCameraView
            initializeCamera((CameraBridgeViewBase) cameraBridgeViewBase, activeBackCamera);
        }

        // Starting advertisement display
        StartAdvertisement();
    }

    /**
     * Initialising Activity Views
     */
    private void _init() {
        // Initializing Cameraview
        cameraBridgeViewBase = (CameraBridgeViewBase) findViewById(R.id.home_camera_camera_view);
        imageView = findViewById(R.id.home_img);
    }

    /**
     * Initialising openCv camera baseLoaderCallback
     */
    private void initOpenCvCamera() {
        // Initializing BaseLoaderCallBack
        baseLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                super.onManagerConnected(status);
                switch (status) {
                    case BaseLoaderCallback.SUCCESS:
                        // Enabling cameraView
                        cameraBridgeViewBase.enableView();
                        mRgbaT = new Mat();
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }
            }
        };
    }

    /**
     * Method used to initialize cameraView
     *
     * @param myCameraView - CameraBridgeViewBase - CameraView
     * @param activeCamera
     */
    private void initializeCamera(CameraBridgeViewBase myCameraView, int activeCamera) {
        myCameraView.setCameraPermissionGranted();
        myCameraView.setCameraIndex(activeCamera);
        myCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        myCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
    }

    /**
     * Method working when cameraView stopped
     */
    @Override
    public void onCameraViewStopped() {
        if (mRgbaT != null) {
            mRgbaT.release();
            mRgbaT = null;
        }
        if (frame != null) {
            frame.release();
        }
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        frame = inputFrame.rgba();
        mRgbaT = frame;
        return frame;
    }

    /**
     * Method to start Advertisement
     */
    private void StartAdvertisement() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Checking whether mat exist or not
                if (mRgbaT != null && mRgbaT.cols() > 0 && mRgbaT.rows() > 0) {
                    Bitmap bitmap = Bitmap.createBitmap(mRgbaT.cols(), mRgbaT.rows(), Bitmap.Config.RGB_565);
                    Utils.matToBitmap(mRgbaT, bitmap);
                    bitmap = com.sanedu.common.Utils.Utils.rotateBitmap(bitmap, -90);
                    Bitmap finalBitmap = bitmap;
                    new BackgroundWork(HomeActivity.this) {
                        @Override
                        public void doInBackground() {
                            super.doInBackground();
                            // fetching age and gender in background thread
                            ageGenderDetection = new AgeGenderDetection(HomeActivity.this, finalBitmap);
                        }

                        @Override
                        public void onPostExecute() {
                            super.onPostExecute();

                            Log.d(TAG, "onPostExecute: DetectFace Public Gender: " + ageGenderDetection.getPublicMaxGender());
                            Log.d(TAG, "onPostExecute: DetectFace Public Age: " + ageGenderDetection.getPublicMaxAge());

                            // Fetching advertisement based on ageGenderDetection
                            GetAdvertisement();
                        }
                    }.execute();
                }
            }
        }, 5000);
    }

    /**
     * Method to fetch advertisement from Firestore
     */
    private void GetAdvertisement() {
        // Firestore query fpr Advertisement
        Query query = FirebaseFirestore.getInstance().collection(Constants.FIREBASE_ADVERTISEMENT_TABLE);
        String ageGrp = ageGenderDetection.getPublicMaxAge().trim();
        if (!ageGrp.isEmpty()) {
            query = query.whereArrayContains("ageGroup", ageGrp);
        }

        /**
         * gender
         *  0 -> Neutral
         *  1 -> Male
         * -1 -> Female
         */

        // Checking gender
        String gender = ageGenderDetection.getPublicMaxGender().trim();
        if (!gender.isEmpty()) {
            if (gender.equalsIgnoreCase(Constants.MALE)) {
                query = query.whereGreaterThanOrEqualTo("gender", 0);
                query = query.orderBy("gender", Query.Direction.DESCENDING);
            } else if (gender.equalsIgnoreCase(Constants.FEMALE)) {
                query = query.whereLessThanOrEqualTo("gender", 0);
                query = query.orderBy("gender", Query.Direction.ASCENDING);
            }
        } else {
            query = query.whereEqualTo("gender", 0);
        }

        // Ordering queries
        query = query.orderBy("lastDisplayed", Query.Direction.ASCENDING);
        query = query.limit(2);

        // Fetching results
        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Checking whether document exist or not
                        if (queryDocumentSnapshots != null && queryDocumentSnapshots.size() != 0) {
                            DocumentSnapshot snapshot = queryDocumentSnapshots.getDocuments().get(0);
                            Advertisement advertisement = snapshot.toObject(Advertisement.class);
                            Log.d(TAG, "onSuccess: DetectFace Adv: " + advertisement);

                            // Checking whether advertisement exist or not
                            if (advertisement != null) {
                                String adUrl = advertisement.getAdvUrl();

                                // Checking whether advertisement url exist or not
                                if (!adUrl.trim().isEmpty()) {

                                    // Loading advertisement url
                                    Picasso.get()
                                            .load(adUrl)
                                            .placeholder(R.mipmap.ic_launcher)
                                            .into(imageView, new Callback() {
                                                @Override
                                                public void onSuccess() {
                                                    // Updating lastDisplayed
                                                    FirebaseFirestore.getInstance().collection(Constants.FIREBASE_ADVERTISEMENT_TABLE)
                                                            .document(advertisement.getId() + "")
                                                            .update("lastDisplayed", System.currentTimeMillis())
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    // Sleeping code for 5 seconds
                                                                    try {
                                                                        sleep(5000);
                                                                    } catch (InterruptedException e) {
                                                                        e.printStackTrace();
                                                                    } finally {
                                                                        // Restarting advertisement
                                                                        StartAdvertisement();
                                                                    }
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    // Restarting advertisement onFailure
                                                                    StartAdvertisement();
                                                                }
                                                            });

                                                }

                                                @Override
                                                public void onError(Exception e) {
                                                    // Restarting advertisement onFailure
                                                    StartAdvertisement();
                                                }
                                            });
                                } else {
                                    // Restarting advertisement onFailure
                                    StartAdvertisement();
                                }
                            } else {
                                // Restarting advertisement onFailure
                                StartAdvertisement();
                            }
                        } else {
                            // Restarting advertisement onFailure
                            StartAdvertisement();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Restarting advertisement onFailure
                        StartAdvertisement();
                        Log.e(TAG, "onFailure: " + e.getMessage());
                    }
                });
    }

    /**
     * Re-initialising openCvLoader and baseLoaderCallback
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "_init: Not init");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, baseLoaderCallback);
        } else if (baseLoaderCallback != null) {
            baseLoaderCallback.onManagerConnected(baseLoaderCallback.SUCCESS);
        }
    }

    /**
     * Releasing mats and disabling cameraBridgeViewBase
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (cameraBridgeViewBase != null) {
            cameraBridgeViewBase.disableView();
        }
        if (mRgbaT != null) {
            mRgbaT.release();
            mRgbaT = null;
        }
        if (frame != null) {
            frame.release();
        }
    }

    /**
     * Releasing mats and disabling cameraBridgeViewBase
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraBridgeViewBase != null) {
            cameraBridgeViewBase.disableView();
        }
        if (mRgbaT != null) {
            mRgbaT.release();
            mRgbaT = null;
        }
        if (frame != null) {
            frame.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent i = getIntent();
            finish();
            startActivity(i);
        }
    }
}