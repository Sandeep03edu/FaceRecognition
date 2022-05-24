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

public class FaceDetection {

    private static final String TAG = "FaceDetectionTag";
    private Uri imageUri;
    private Context activity;
    private VisionDetRet face;
    FaceDet mFaceDet;
    PedestrianDet mPersonDet;
    List<VisionDetRet> faceList;
    BaseLoaderCallback baseLoaderCallback;

    public FaceDetection(Context activity, Uri imageUri) {
        this.activity = activity;
        this.imageUri = imageUri;
        setBaseLoaderCallback();
        init();
        detectFace();
    }

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

    private void init() {
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, activity, baseLoaderCallback);
        } else {
            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

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

    public int faceCount() {
        if (faceList != null) {
            return faceList.size();
        }
        return -1;
    }

    public VisionDetRet getFace() {
        if (faceList.size() > 0) {
            this.face = faceList.get(0);
            return face;
        }
        return null;
    }
}
