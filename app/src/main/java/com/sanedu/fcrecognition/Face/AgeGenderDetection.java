package com.sanedu.fcrecognition.Face;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.sanedu.fcrecognition.MainActivity;
import com.sanedu.fcrecognition.Model.AgeGender;
import com.sanedu.fcrecognition.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class AgeGenderDetection {
    private static final String TAG = "AgeGenderDetectionTag";

    private Activity activity;
    private Bitmap imageBitmap, facePart;

    private File faceFrontalCascadeFile;
    private CascadeClassifier faceCascadeClassifier;
    private static Scalar MODEL_MEAN_VALUES = new Scalar(78.4263377603, 87.7689143744, 114.895847746);
    private static String[] ageList = {"(0-2)", "(4-6)", "(8-12)", "(15-20)", "(25-32)", "(38-43)", "(48-53)", "(60-100)"};
    private static String[] genderList = {"Male", "Female"};
    BaseLoaderCallback baseLoaderCallback;

    AgeGender ageGroup = new AgeGender(), gender = new AgeGender();

    public AgeGenderDetection(Activity activity, Bitmap bitmap) {
        Log.d(TAG, "AgeGenderDetection: Const");
        this.activity = activity;
        this.imageBitmap = bitmap;
        SetBaseLoader();
        _init();
    }

    private void SetBaseLoader() {
        Log.d(TAG, "SetBaseLoader: ");
        baseLoaderCallback = new BaseLoaderCallback(activity) {
            @Override
            public void onManagerConnected(int status) {
                super.onManagerConnected(status);
                Log.d(TAG, "onManagerConnected: ");
                switch (status) {
                    case BaseLoaderCallback.SUCCESS:
                        InputStream faceFrontalIs = activity.getResources().openRawResource(R.raw.haarcascade_frontalface_alt2);

                        File cascadeDir = activity.getDir("cascadeDir", Context.MODE_PRIVATE);
                        faceFrontalCascadeFile = new File(cascadeDir, "haarcascade_frontalface_alt2.xml");

                        try {
                            FileOutputStream faceFrontalFos = new FileOutputStream(faceFrontalCascadeFile);
                            byte[] faceFrontalBuffer = new byte[4096];
                            int byteRead;
                            while ((byteRead = faceFrontalIs.read(faceFrontalBuffer)) != -1) {
                                faceFrontalFos.write(faceFrontalBuffer, 0, byteRead);
                            }
                            faceFrontalIs.close();
                            faceFrontalFos.close();

                            faceCascadeClassifier = new CascadeClassifier(faceFrontalCascadeFile.getAbsolutePath());
                            if (faceCascadeClassifier.empty()) {
                                faceCascadeClassifier = null;
                            } else {
                                cascadeDir.delete();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "onManagerConnected: Err: " + e.getMessage());
                            e.printStackTrace();
                        }

                        Log.d(TAG, "onManagerConnected: Load Successful");
                        break;
                    default:
                        super.onManagerConnected(status);
                        Log.d(TAG, "onManagerConnected: Status : " + status);
                        break;
                }
            }
        };
    }

    private void _init() {
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "_init: Not init");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, activity.getApplicationContext(), baseLoaderCallback);
        } else if (baseLoaderCallback != null) {
            Log.d(TAG, "_init: init");
            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

        DetectFace();
    }

    private void DetectFace() {
        Mat inputFace = new Mat(imageBitmap.getHeight(), imageBitmap.getWidth(), CvType.CV_8UC1);
        Utils.bitmapToMat(imageBitmap, inputFace);

        // Detecting face
        MatOfRect faceRect = new MatOfRect();
        faceCascadeClassifier.detectMultiScale(inputFace, faceRect);
        Log.d(TAG, "FindFace: " + faceRect.toArray().length + " faces detected");

        if (faceRect.toArray().length > 0) {
            Rect rect = faceRect.toArray()[0];
            Imgproc.rectangle(
                    inputFace,
                    new Point(rect.x, rect.y),
                    new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0, 0, 255),
                    3
            );

            facePart = Bitmap.createBitmap(imageBitmap, rect.x, rect.y, rect.width, rect.height);
            Mat faceMat = new Mat(facePart.getHeight(), facePart.getWidth(), CvType.CV_8UC1);
            Utils.bitmapToMat(facePart, faceMat);

            ageGroup = ageGrp(faceMat, activity);
            gender = genderGrp(faceMat, activity);
        }

    }

    public static AgeGender ageGrp(Mat faceMat, Context context) {
        String ageGroup = "";
        double confidence = 0;
        if (faceMat != null) {
            Imgproc.resize(faceMat, faceMat, new Size(224, 224));
            Imgproc.cvtColor(faceMat, faceMat, Imgproc.COLOR_RGBA2RGB);

            String prototxtPath = getPath(R.raw.age_deploy, context);
            String caffeModelPath = getPath(R.raw.age_net, context);
            if (prototxtPath != null && caffeModelPath != null) {
                Net ageDnnNet = Dnn.readNetFromCaffe(prototxtPath, caffeModelPath);
                Mat blob = Dnn.blobFromImage(faceMat, 1, new Size(227, 227), MODEL_MEAN_VALUES, false);
                Log.d(TAG, "FindFace: Blob: " + blob);
                if (blob.empty()) {
                    Log.d(TAG, "FindFace: Empty blob");
                } else {
                    ageDnnNet.setInput(blob);
                    Mat agePred = ageDnnNet.forward();

                    int match = -1;
                    double maxi = -1;
                    double total = 0;
                    for (int j = 0; j < agePred.cols(); ++j) {
                        Log.d(TAG, "FindFace: Col: " + Arrays.toString(agePred.get(0, j)));
                        double ans = agePred.get(0, j)[0];
                        total += ans;
                        if (ans > maxi) {
                            maxi = ans;
                            match = j;
                        }
                    }
                    if (match != -1) {
                        ageGroup = ageList[match];
                        confidence = 100 * maxi;
                        confidence /= total;
                    }
                }
            }
        }
        return new AgeGender(ageGroup, confidence);
    }

    public static AgeGender genderGrp(Mat faceMat, Context context) {
        String genderGrp = "";
        double confidence = 0;

        if (faceMat != null) {
            Imgproc.resize(faceMat, faceMat, new Size(224, 224));
            Imgproc.cvtColor(faceMat, faceMat, Imgproc.COLOR_RGBA2RGB);

            String prottoxtPath = getPath(R.raw.gender_deploy, context);
            String caffeModelPath = getPath(R.raw.gender_net, context);
            if (prottoxtPath != null && caffeModelPath != null) {
                Net genderDnnNet = Dnn.readNetFromCaffe(prottoxtPath, caffeModelPath);
                Mat blob = Dnn.blobFromImage(faceMat, 1, new Size(227, 227), MODEL_MEAN_VALUES, false);
                Log.d(TAG, "FindFace: Blob: " + blob);
                if (blob.empty()) {
                    Log.d(TAG, "FindFace: Empty blob");
                } else {
                    genderDnnNet.setInput(blob);
                    Mat genderPred = genderDnnNet.forward();

                    int match = -1;
                    double maxi = -1;
                    double total = 0;
                    for (int j = 0; j < genderPred.cols(); ++j) {
                        Log.d(TAG, "FindFace: Col: " + Arrays.toString(genderPred.get(0, j)));
                        double ans = genderPred.get(0, j)[0];
                        total += ans;
                        if (ans > maxi) {
                            maxi = ans;
                            match = j;
                        }
                    }
                    if (match != -1) {
                        genderGrp = genderList[match];
                        confidence = 100 * maxi;
                        confidence /= total;
                    }
                }
            }
        }
        return new AgeGender(genderGrp, confidence);
    }

    private static String getPath(int rawFile, Context context) {
        String path = null;
        try {
            InputStream is = context.getResources().openRawResource(rawFile);
            byte[] data = new byte[is.available()];
            is.read(data);
            is.close();

//            String fileName = getResources().getResourceName(rawFile);
            String fileName = String.valueOf(rawFile);
            File outFile = new File(context.getFilesDir(), fileName);
            FileOutputStream os = new FileOutputStream(outFile);
            os.write(data);
            os.close();

            path = outFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "getPath: Err: " + e.getMessage());
        }

        return path;
    }

    public AgeGender getAgeGroup() {
        return ageGroup;
    }

    public AgeGender getGender() {
        return gender;
    }

    public Bitmap getFacePart() {
        return facePart;
    }
}