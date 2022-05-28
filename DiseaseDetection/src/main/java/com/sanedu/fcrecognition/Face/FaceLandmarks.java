package com.sanedu.fcrecognition.Face;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Log;

import com.tzutalin.dlib.VisionDetRet;

import java.util.ArrayList;

public class FaceLandmarks {
    private static final String TAG = "LandmarksTag";
    private static Bitmap bitmap;

    /**
     * Method to get faceLandmarks as ArrayList<Point>
     * @param path - String - Image file path
     * @param face - VisionDetRet - face
     * @return - ArrayList<Point> landmark points list
     */
    public static ArrayList<Point> getLandmarks(String path, VisionDetRet face) {
        Log.d(TAG, "getLandmarks: Path: " + path );
        if(face==null){
            Log.e(TAG, "getLandmarks: Null face");
        }

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
        bitmap= bm;
        // Loop result list
        // Get landmark
        ArrayList<Point> landmarks = face.getFaceLandmarks();
        Log.d(TAG, "drawRect: Size: " + landmarks.size());

        return landmarks;
    }

    /**
     * Method to get Face Bitmap
     * @param path - ImageFilePath
     * @param face - VisionDetRet face
     * @return - Bitmap- FaceBitmap
     */
    public static Bitmap getFaceBitmap(String path, VisionDetRet face){
        getLandmarks(path, face);
        return bitmap;
    }

    /**
     * Method to get Resized Bitmap
     * @param bm - Bitmap - imageBitmap
     * @param newWidth - int - new Width
     * @param newHeight - int - new Height
     * @return - Bitmap - resized Bitmap
     */
    private static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
        return resizedBitmap;
    }
}
