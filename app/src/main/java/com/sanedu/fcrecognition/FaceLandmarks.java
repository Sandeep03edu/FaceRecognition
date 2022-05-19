package com.sanedu.fcrecognition;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Log;

import com.tzutalin.dlib.VisionDetRet;

import java.util.ArrayList;

public class FaceLandmarks {

    public static ArrayList<Point> getLandmarks(String path, VisionDetRet face) {
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
            resizeRatio = (float)
                    bm.getWidth() / (float) width;
        }

        // Loop result list
        // Get landmark
        ArrayList<Point> landmarks = face.getFaceLandmarks();
        String TAG = "LandmarksTag";
        Log.d(TAG, "drawRect: Size: " + landmarks.size());

        return landmarks;
    }


    private static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
        return resizedBitmap;
    }

}
