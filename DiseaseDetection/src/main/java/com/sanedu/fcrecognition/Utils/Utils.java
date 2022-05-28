package com.sanedu.fcrecognition.Utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.util.Log;


import com.sanedu.common.Utils.ImageResizer;
import com.sanedu.fcrecognition.Model.Edges;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    private static final String TAG = "UtilsTag";
    private static double gap = 20;
    private static double MAXI = 100000000;
    private static double MINI = -1;

    /**
     * Method to convert Uri to Bitmap
     * @param context - Context - Activity context
     * @param uri - Uri - image file uri
     * @return - Bitmap - converted bitmap from uri
     */
    public static Bitmap Uri2Bitmap(Context context, Uri uri) {
        try {
            InputStream is = context.getContentResolver().openInputStream(uri);
            return BitmapFactory.decodeStream(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Method to get Edged bitmap
     * @param imageBitmap - Bitmap - Bitmap of image file
     * @param list - List<Integer> - list of points over which edge has to be taken
     * @param faceLandmarks - FaceLandmarks detected from face image
     * @return - Bitmap - return croppped detected bitmap
     */
    public static Bitmap getEdgedBitmap(Bitmap imageBitmap, List<Integer> list, ArrayList<Point> faceLandmarks) {
        Bitmap finalBitmap = Bitmap.createBitmap(imageBitmap.getWidth(), imageBitmap.getHeight(), imageBitmap.getConfig());

        // Checking avg value for adding gap
        int minX = (int) MAXI, minY = (int) MAXI;
        int maxX = (int) MINI, maxY = (int) MINI;
        for (int i = 0; i < faceLandmarks.size(); ++i) {
            if (list.contains(i)) {
                int x = faceLandmarks.get(i).x;
                int y = faceLandmarks.get(i).y;

                minX = Math.min(minX, x);
                minY = Math.min(minY, y);
                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y);
            }
        }
        int avgX = (maxX + minX) / 2;
        int avgY = (maxY + minY) / 2;


        Canvas canvas = new Canvas(finalBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Path path = new Path();
        Point firstPoint = null;
        for (int i = 0; i < faceLandmarks.size(); ++i) {
            if (list.contains(i)) {
                int x = getGapedCoord(faceLandmarks.get(i).x, avgX);
                int y = getGapedCoord(faceLandmarks.get(i).y, avgY);

                if (firstPoint == null) {
                    firstPoint = faceLandmarks.get(i);
                }
                    path.lineTo(x, y);
                Log.d(TAG, "getEdgedBitmap: X: " + x + " Y: " + y);
            }
        }
        if(firstPoint!=null) {
            int x = getGapedCoord(firstPoint.x, avgX);
            int y = getGapedCoord(firstPoint.y, avgY);
            path.lineTo(x, y);
            Log.d(TAG, "getEdgedBitmap: X: " + x + " Y: " + y);
        }

        canvas.drawPath(path, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(imageBitmap, 0, 0, paint);

        Bitmap croppedBitmap = Bitmap.createBitmap(finalBitmap,minX,minY,maxX-minX,maxY-minY);
        return croppedBitmap;
    }

    /**
     * Method to get edges of Bitmap
     * @param list - List<Integer> - List of points where image bitmap has to be cropped
     * @param faceLandmarks - FaceLandmarks detected from face image
     * @return - Edges - Edge class to getEdge from list and faceLandmarks
     */
    public static Edges getEdge(List<Integer> list, ArrayList<Point> faceLandmarks) {
        Log.d(TAG, "getEdge: list " + list.toString());
        double left = MAXI, right = MINI, top = MINI, bottom = MAXI;
        for (int i = 0; i < faceLandmarks.size(); ++i) {
            if (list.contains(i)) {
                left = Math.min(faceLandmarks.get(i).x, left);
                bottom = Math.min(faceLandmarks.get(i).y, bottom);
                right = Math.max(faceLandmarks.get(i).x, right);
                top = Math.max(faceLandmarks.get(i).y, top);
            }
        }

        int gap=10;
        left -= gap;
        bottom -= gap;
        top += gap;
        right += gap;
        Edges edges = new Edges(left, right, top, bottom);
        Log.d(TAG, "getEdge: Edges: " + edges.toString());
        return edges;
    }

    /**
     * Method to convert Bitmap to Mat
     * @param rgbaBitmap - Bitmap - Input rgba Bitmap
     * @return - Mat - output Mat
     */
    public static Mat convertBitmap2Mat(Bitmap rgbaBitmap) {
        Mat rgbaMat = new Mat(rgbaBitmap.getHeight(), rgbaBitmap.getWidth(), CvType.CV_8UC4);
        Bitmap bmp32 = rgbaBitmap.copy(Bitmap.Config.ARGB_8888, true);
        org.opencv.android.Utils.bitmapToMat(bmp32, rgbaMat);

        Mat rgbMat = new Mat(rgbaBitmap.getHeight(), rgbaBitmap.getWidth(), CvType.CV_8UC3);
        Imgproc.cvtColor(rgbaMat, rgbMat, Imgproc.COLOR_RGBA2RGB, 3);
        return rgbMat;
    }

    /**
     * Method to add gap in coordinates
     * @param coord - Given coordinate
     * @param avg - Average value
     * @return - gaped coordinate
     */
    private static int getGapedCoord(int coord, int avg) {
        return (int) (coord < avg ? coord - gap : coord + gap);
    }

    /**
     * Method to convert Bitmap to Uri
     * @param context - Context - Activity context
     * @param bitmap - Bitmap - imageBitmap
     * @return - Uri - Bitmap converted uri
     */
    public static Uri Bitmap2Uri(Context context , Bitmap bitmap){
        bitmap = ImageResizer.reduceBitmapSize(bitmap, 240000);
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("image", Context.MODE_PRIVATE);
        if (!directory.exists()) {
            directory.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".png";
        File mypath = new File(directory, fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

            return Uri.fromFile(mypath);
        } catch (Exception e) {
            Log.e("SAVE_IMAGE", e.getMessage(), e);
        }
        return null;
    }

    /**
     * Method to save raw resource file to storage
     * @param rawFile - int - Raw resource file location
     * @param context - Context - Activity context
     * @return - String - raw file save location
     */
    public static String getRawFilePath(int rawFile, Context context) {
        String path = null;
        try {
            InputStream is = context.getResources().openRawResource(rawFile);
            byte[] data = new byte[is.available()];
            is.read(data);
            is.close();

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

}