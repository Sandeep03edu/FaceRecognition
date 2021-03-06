package com.sanedu.smartadvertising.MlModel;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.sanedu.smartadvertising.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Sandeep
 * AgeGenderDetection java class to detect age and gender on the basis of image bitmap
 */
public class AgeGenderDetection {
    private final String TAG = "AgeGenderDetectionTag";

    // Variables
    private Activity activity;
    private Bitmap imageBitmap, facePart;
    private File faceFrontalCascadeFile;
    private CascadeClassifier faceCascadeClassifier;
    private static Scalar MODEL_MEAN_VALUES = new Scalar(104, 117, 123);
    private String[] ageList = {"(0-2)", "(4-6)", "(8-12)", "(15-20)", "(25-32)", "(38-43)", "(48-53)", "(60-100)"};
    private String[] genderList = {"Male", "Female"};
    BaseLoaderCallback baseLoaderCallback;
    private int ageMax = -1, genMax = -1;

    ResultConfidence ageGroup = new ResultConfidence(), gender = new ResultConfidence();
    private ArrayList<ResultConfidence> ageGroupList = new ArrayList<>(), genderResList = new ArrayList<>();

    /**
     * Constructor
     *
     * @param activity - Activity - used for context
     * @param bitmap   - Bitmap - image Bitmap
     */
    public AgeGenderDetection(Activity activity, Bitmap bitmap) {
        Log.d(TAG, "AgeGenderDetection: Const");
        this.activity = activity;
        this.imageBitmap = bitmap;
        SetBaseLoader();
        _init();
    }

    /**
     * Method to setup BaseLoaderCallback
     */
    private void SetBaseLoader() {
        Log.d(TAG, "SetBaseLoader: ");
        baseLoaderCallback = new BaseLoaderCallback(activity) {
            @Override
            public void onManagerConnected(int status) {
                super.onManagerConnected(status);
                Log.d(TAG, "onManagerConnected: ");
                switch (status) {
                    case BaseLoaderCallback.SUCCESS:
                        // Saving face detection "haarcascade_frontalface_alt2.xml" file
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

    /**
     * Initialising openCv and BaseLoaderCallback
     */
    private void _init() {
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "_init: Not init");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, activity.getApplicationContext(), baseLoaderCallback);
        } else if (baseLoaderCallback != null) {
            Log.d(TAG, "_init: init");
            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

        // Detecting faces
        DetectFace();
    }

    /**
     * Method to detect faces from bitmap using faceCascadeClassifier
     */
    private void DetectFace() {
        Mat inputFace = new Mat(imageBitmap.getHeight(), imageBitmap.getWidth(), CvType.CV_8UC1);
        Utils.bitmapToMat(imageBitmap, inputFace);

        // Detecting face
        MatOfRect faceRect = new MatOfRect();
        faceCascadeClassifier.detectMultiScale(inputFace, faceRect);
        Log.d(TAG, "FindFace: " + faceRect.toArray().length + " faces detected");

        ageGroupList = new ArrayList<>();
        genderResList = new ArrayList<>();

        Log.d(TAG, "DetectFace: Totalfaces: " + faceRect.toArray().length);

        if (faceRect.toArray().length > 0) {
            for (int i = 0; i < faceRect.toArray().length; ++i) {
                Rect rect = faceRect.toArray()[i];
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

                Log.d(TAG, "DetectFace: Age: " + ageGroup.getResult() + " Confidence: " + ageGroup.getConfidence());
                Log.d(TAG, "DetectFace: Gender: " + gender.getResult() + " Confidence: " + gender.getConfidence());

                ageGroupList.add(ageGroup);
                genderResList.add(gender);
            }
        }

        if (faceRect.toArray().length == 0) {
            ageMax = -1;
            genMax = -1;
            return;
        }

        // Age and Gender result for 1st face
        ageMax = getMax(8, ageGroupList, ageList);
        genMax = getMax(2, genderResList, genderList);
    }

    /**
     * Method to find Public maximum age
     *
     * @return - String - Age group
     */
    public String getPublicMaxAge() {
        if (ageMax == -1) {
            return "";
        }
        return ageList[ageMax];
    }

    /**
     * Method to find public maximum gender
     *
     * @return - String - Gender
     */
    public String getPublicMaxGender() {
        if (genMax == -1) {
            return "";
        }
        return genderList[genMax];
    }

    /**
     * Method to get maximum index of element
     *
     * @param n                 - int - Size
     * @param resultConfidences - ArrayList<ResultConfidence>
     * @param list              - String[] - String of list
     * @return - int - maximum index
     */
    private int getMax(int n, ArrayList<ResultConfidence> resultConfidences, String[] list) {
        double[] confiSum = new double[n];
        int[] count = new int[n];

        for (int i = 0; i < n; ++i) {
            count[i] = 0;
            confiSum[i] = 0;
        }

        for (ResultConfidence resConf : resultConfidences) {
            String res = resConf.getResult();
            double confi = resConf.getConfidence();

            int index = getIndex(list, res);
            confiSum[index] += confi;
            count[index]++;
        }

        int maxi = -1;
        for (int a : count) {
            maxi = Math.max(maxi, a);
        }

        ArrayList<Integer> maxIndex = new ArrayList<Integer>();
        for (int i = 0; i < n; ++i) {
            if (count[i] == maxi) {
                maxIndex.add(i);
            }
        }

        int maxiInd = -1;
        double maxConf = -1;

        for (int i : maxIndex) {
            if (confiSum[i] > maxConf) {
                maxConf = confiSum[i];
                maxiInd = i;
            }
        }
        return maxiInd;
    }

    /**
     * Method to get index of element in string array
     *
     * @param ageList - String[] - Array of String
     * @param age     - String - element to be found
     * @return - int - index of element
     */
    private int getIndex(String[] ageList, String age) {
        int ct = 0;
        for (String s : ageList) {
            if (s.equalsIgnoreCase(age)) {
                return ct;
            }
            ct++;
        }
        return -1;
    }


    /**
     * Method to get Age group
     *
     * @param faceMat - Mat - face cropped Mat
     * @param context - Context
     * @return - ResultConfidence - output storing result with confidence
     */
    private ResultConfidence ageGrp(Mat faceMat, Context context) {
        String ageGroup = "";
        double confidence = 0;

        // Checking whether faceMat exist or null
        if (faceMat != null) {
            Imgproc.resize(faceMat, faceMat, new Size(224, 224));
            Imgproc.cvtColor(faceMat, faceMat, Imgproc.COLOR_RGBA2RGB);

            // Saving model files
            String prototxtPath = com.sanedu.common.Utils.Utils.getRawFilePath(R.raw.age_deploy, context);
            String caffeModelPath = com.sanedu.common.Utils.Utils.getRawFilePath(R.raw.age_net, context);

            // Checking file stored successfully or not
            if (prototxtPath != null && caffeModelPath != null) {
                // Net Age model
                Net ageDnnNet = Dnn.readNetFromCaffe(prototxtPath, caffeModelPath);
                Mat blob = Dnn.blobFromImage(faceMat, 1, new Size(227, 227), MODEL_MEAN_VALUES, false);
                Log.d(TAG, "FindFace: Blob: " + blob);
                if (blob.empty()) {
                    Log.d(TAG, "FindFace: Empty blob");
                }
                // Working when blob is not empty
                else {
                    ageDnnNet.setInput(blob);
                    Mat agePred = ageDnnNet.forward();

                    int match = -1;
                    double maxi = -1;
                    double total = 0;

                    // Running loop over agePred.cols() to get maximum prediction
                    for (int j = 0; j < agePred.cols(); ++j) {
                        Log.d(TAG, "FindFace: Col: " + Arrays.toString(agePred.get(0, j)));
                        double ans = agePred.get(0, j)[0];
                        total += ans;
                        if (ans > maxi) {
                            maxi = ans;
                            match = j;
                        }
                    }

                    // Setting data into resultConfidence model
                    if (match != -1) {
                        ageGroup = ageList[match];
                        confidence = 100 * maxi;
                        confidence /= total;
                    }
                }
            }
        }
        return new ResultConfidence(ageGroup, confidence);
    }

    /**
     * Method to get gender
     *
     * @param faceMat - Mat - face cropped Mat
     * @param context - Context
     * @return - ResultConfidence - output storing result with confidence
     */
    private ResultConfidence genderGrp(Mat faceMat, Context context) {
        String genderGrp = "";
        double confidence = 0;

        // Checking whether faceMat exist or null
        if (faceMat != null) {
            Imgproc.resize(faceMat, faceMat, new Size(224, 224));
            Imgproc.cvtColor(faceMat, faceMat, Imgproc.COLOR_RGBA2RGB);

            // Saving model files
            String prottoxtPath = com.sanedu.common.Utils.Utils.getRawFilePath(R.raw.gender_deploy, context);
            String caffeModelPath = com.sanedu.common.Utils.Utils.getRawFilePath(R.raw.gender_net, context);

            // Checking file stored successfully or not
            if (prottoxtPath != null && caffeModelPath != null) {
                // Net Age model
                Net genderDnnNet = Dnn.readNetFromCaffe(prottoxtPath, caffeModelPath);
                Mat blob = Dnn.blobFromImage(faceMat, 1, new Size(227, 227), MODEL_MEAN_VALUES, false);
                Log.d(TAG, "FindFace: Blob: " + blob);
                if (blob.empty()) {
                    Log.d(TAG, "FindFace: Empty blob");
                }
                // Working when blob is not empty
                else {
                    genderDnnNet.setInput(blob);
                    Mat genderPred = genderDnnNet.forward();

                    int match = -1;
                    double maxi = -1;
                    double total = 0;
                    // Running loop over agePred.cols() to get maximum prediction
                    for (int j = 0; j < genderPred.cols(); ++j) {
                        Log.d(TAG, "FindFace: Col: " + Arrays.toString(genderPred.get(0, j)));
                        double ans = genderPred.get(0, j)[0];
                        total += ans;
                        if (ans > maxi) {
                            maxi = ans;
                            match = j;
                        }
                    }

                    // Setting data into resultConfidence model
                    if (match != -1) {
                        genderGrp = genderList[match];
                        confidence = 100 * maxi;
                        confidence /= total;
                    }
                }
            }
        }
        return new ResultConfidence(genderGrp, confidence);
    }
}