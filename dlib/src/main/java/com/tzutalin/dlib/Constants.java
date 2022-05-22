package com.tzutalin.dlib;

import android.content.Context;
import android.os.Environment;

import com.sanedu.dlib.R;

import java.io.File;
import java.io.InputStream;

/**
 * Created by darrenl on 2016/4/22.
 */
public final class Constants {
    private Constants() {
        // Constants should be prive
    }

    /**
     * getFaceShapeModelPath
     * @return default face shape model path
     */
    public static String getFaceShapeModelPath() {
//        File sdcard = Environment.getExternalStorageDirectory();
        File sdcard = Environment.getExternalStorageDirectory();
        String targetPath = sdcard.getAbsolutePath() + File.separator + "shape_predictor_68_face_landmarks.dat";
        return targetPath;
    }

    public static void SaveShapeToStorage() {
//        InputStream faceFrontalIs = activity.getResources().openRawResource(R.raw.haarcascade_frontalface_alt2);
//        File cascadeDir = activity.getDir("cascadeDir", Context.MODE_PRIVATE);
//        faceFrontalCascadeFile = new File(cascadeDir, "haarcascade_frontalface_alt2.xml");

    }
}
