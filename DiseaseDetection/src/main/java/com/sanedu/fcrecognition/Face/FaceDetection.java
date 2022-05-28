package com.sanedu.fcrecognition.Face;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.sanedu.fcrecognition.R;
import com.sanedu.fcrecognition.Utils.Utils;
import com.tzutalin.dlib.FaceDet;
import com.tzutalin.dlib.PedestrianDet;
import com.tzutalin.dlib.VisionDetRet;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.util.List;

/**
 * @author Sandeep
 * FaceDetection java class to detect
 */
public class FaceDetection {

    private static final String TAG = "FaceDetectionTag";
    private Uri imageUri;
    private Context activity;
    private VisionDetRet face;
    FaceDet mFaceDet;
    PedestrianDet mPersonDet;
    List<VisionDetRet> faceList;
    BaseLoaderCallback baseLoaderCallback;

    /**
     * Constructor
     * @param activity - Activity - Used as context
     * @param imageUri - Uri - imageUri
     */
    public FaceDetection(Context activity, Uri imageUri) {
        this.activity = activity;
        this.imageUri = imageUri;

        // Initialising baseLoaderCallback
        setBaseLoaderCallback();

        // initialising openCv
        init();

        // Detecting faces
        detectFace();
    }

    /**
     * Setting base loader callback
     */
    private void setBaseLoaderCallback() {
        baseLoaderCallback = new BaseLoaderCallback(activity) {
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
    }

    /**
     * Initialising openCv and baseLoaderCallback
     */
    private void init() {
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, activity, baseLoaderCallback);
        } else {
            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    /**
     * Method to detecting faces
     */
    public void detectFace() {
        // Init
        if (mPersonDet == null) {
            mPersonDet = new PedestrianDet();
        }
        if (mFaceDet == null) {
            mFaceDet = new FaceDet(Utils.getRawFilePath(R.raw.shape_predictor_68_face_landmarks, activity));
        }
        this.faceList = mFaceDet.detect(imageUri.getPath());
    }

    /**
     * Counting faces
     * @return int - number of found faces
     */
    public int faceCount() {
        if (faceList != null) {
            return faceList.size();
        }
        return -1;
    }

    /**
     * Method to getFace VisionDetRet
     * @return - VisionDetRet
     */
    public VisionDetRet getFace() {
        if (faceList.size() > 0) {
            this.face = faceList.get(0);
            return face;
        }
        return null;
    }
}
