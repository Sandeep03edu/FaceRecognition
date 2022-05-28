package com.sanedu.smartadvertising;

import static org.opencv.core.Core.flip;

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

        initOpenCvCamera();

        if (!Permission.CheckPermission(this, Manifest.permission.CAMERA) ||
                !Permission.CheckPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Permission.RequestPermission(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE});
            return;
        } else {
            initializeCamera((CameraBridgeViewBase) cameraBridgeViewBase, activeBackCamera);
        }

        findViewById(R.id.home_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRgbaT.empty()) {
                    return;
                }

                Bitmap bitmap = Bitmap.createBitmap(mRgbaT.cols(), mRgbaT.rows(), Bitmap.Config.RGB_565);
                Utils.matToBitmap(mRgbaT, bitmap);
                bitmap = com.sanedu.common.Utils.Utils.rotateBitmap(bitmap, -90);
                StartAdvertisement();
                Bitmap finalBitmap = bitmap;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(finalBitmap);
                    }
                });

            }
        });


        StartAdvertisement();

//        UploadData();

    }

    private void UploadData() {
        String[] ageList = {"(0-2)", "(4-6)", "(8-12)", "(15-20)", "(25-32)", "(38-43)", "(48-53)", "(60-100)"};
        int[] genderList = {-1, 1, 0};

        String[] urls = new String[]{
                "https://i.ibb.co/41m2mHN/Female-1.png",
                "https://i.ibb.co/xCGL84K/Female-2.png",
                "https://i.ibb.co/bPM1CRd/Female-3.png",
                "https://i.ibb.co/m6j2B3V/Female-4.png",
                "https://i.ibb.co/F3KKc77/Female-5.png",
                "https://i.ibb.co/T0Tnpk5/Female-6.png",
                "https://i.ibb.co/0thkwKH/Female-7.png",
                "https://i.ibb.co/2dKBj9n/Female-8.png",

                "https://i.ibb.co/tHq4W67/Male-1.png",
                "https://i.ibb.co/fMcjhMT/Male-2.png",
                "https://i.ibb.co/PM1S5hB/Male-3.png",
                "https://i.ibb.co/F5BzBgN/Male-4.png",
                "https://i.ibb.co/LYBJYdp/Male-5.png",
                "https://i.ibb.co/Wn01yHt/Male-6.png",
                "https://i.ibb.co/JySC4nR/Male-7.png",
                "https://i.ibb.co/Pw9nqrX/Male-8.png",

                "https://i.ibb.co/kyWbYYf/Neutral-1.png",
                "https://i.ibb.co/Fbqdz20/Neutral-2.png",
                "https://i.ibb.co/5YsbrMR/Neutral-3.png",
                "https://i.ibb.co/cwTP6d0/Neutral-4.png",
                "https://i.ibb.co/KG8N0y5/Neutral-5.png",
                "https://i.ibb.co/XWrSPy1/Neutral-6.png",
                "https://i.ibb.co/nQY0bQC/Neutral-7.png",
                "https://i.ibb.co/yW4mwCf/Neutral-8.png"
        };

        for (int i = 0; i < 24; ++i) {
            ArrayList<String> ageArrayList = new ArrayList<>();
            ageArrayList.add(ageList[i % 8]);
            Advertisement advertisement = new Advertisement(ageArrayList, genderList[i/8], urls[i], -1);
            advertisement.setId(System.currentTimeMillis());
            FirebaseFirestore.getInstance().collection(Constants.FIREBASE_ADVERTISEMENT_TABLE)
                    .document(advertisement.getId() + "")
                    .set(advertisement);
        }
    }

    private void _init() {
        // Initializing Cameraview
        cameraBridgeViewBase = (CameraBridgeViewBase) findViewById(R.id.home_camera_camera_view);
        imageView = findViewById(R.id.home_img);
    }

    private void initOpenCvCamera() {
        // Initializing BaseLoaderCallBack
        baseLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                super.onManagerConnected(status);
                switch (status) {
                    case BaseLoaderCallback.SUCCESS:
//                        Toast.makeText(mAppContext, "Load Successful", Toast.LENGTH_SHORT).show();
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

    private void initializeCamera(CameraBridgeViewBase myCameraView, int activeCamera) {
        myCameraView.setCameraPermissionGranted();
        myCameraView.setCameraIndex(activeCamera);
        myCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        myCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
    }

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

    private void StartAdvertisement() {
        if (mRgbaT != null && mRgbaT.empty()) {
            return;
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mRgbaT != null && mRgbaT.cols()>0 && mRgbaT.rows()>0) {
                    Bitmap bitmap = Bitmap.createBitmap(mRgbaT.cols(), mRgbaT.rows(), Bitmap.Config.RGB_565);
                    Utils.matToBitmap(mRgbaT, bitmap);
                    bitmap = com.sanedu.common.Utils.Utils.rotateBitmap(bitmap, -90);
                    Bitmap finalBitmap = bitmap;
                    new BackgroundWork(HomeActivity.this) {
                        @Override
                        public void doInBackground() {
                            super.doInBackground();
                            ageGenderDetection = new AgeGenderDetection(HomeActivity.this, finalBitmap);
                        }

                        @Override
                        public void onPostExecute() {
                            super.onPostExecute();

                            Log.d(TAG, "onPostExecute: DetectFace Public Gender: " + ageGenderDetection.getPublicMaxGender());
                            Log.d(TAG, "onPostExecute: DetectFace Public Age: " + ageGenderDetection.getPublicMaxAge());

                            GetAdvertisement();
                        }
                    }.execute();
                }
            }
        }, 5000);
    }

    private void GetAdvertisement() {
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

        query = query.orderBy("lastDisplayed", Query.Direction.ASCENDING);
        query = query.limit(2);
        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots != null && queryDocumentSnapshots.size() != 0) {
                            DocumentSnapshot snapshot = queryDocumentSnapshots.getDocuments().get(0);
                            Advertisement advertisement = snapshot.toObject(Advertisement.class);
                            Log.d(TAG, "onSuccess: DetectFace Adv: " + advertisement);
                            if (advertisement != null) {
                                String adUrl = advertisement.getAdvUrl();
                                if (!adUrl.trim().isEmpty()) {
                                    Picasso.get()
                                            .load(adUrl)
                                            .placeholder(R.mipmap.ic_launcher)
                                            .into(imageView, new Callback() {
                                                @Override
                                                public void onSuccess() {
                                                    FirebaseFirestore.getInstance().collection(Constants.FIREBASE_ADVERTISEMENT_TABLE)
                                                            .document(advertisement.getId() + "")
                                                            .update("lastDisplayed", System.currentTimeMillis())
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    StartAdvertisement();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    StartAdvertisement();
                                                                }
                                                            });

                                                }

                                                @Override
                                                public void onError(Exception e) {
                                                    StartAdvertisement();
                                                }
                                            });
                                } else {
                                    StartAdvertisement();
                                }
                            } else {
                                StartAdvertisement();
                            }
                        } else {
                            StartAdvertisement();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        StartAdvertisement();
                        Log.e(TAG, "onFailure: " + e.getMessage());
                    }
                });


    }

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