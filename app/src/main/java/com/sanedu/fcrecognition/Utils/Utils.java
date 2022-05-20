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

import com.sanedu.fcrecognition.Model.Edges;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    private static final String TAG = "UtilsTag";
    private static double gap = 20;
    private static double MAXI = 100000000;
    private static double MINI = -1;

    public static Bitmap Uri2Bitmap(Context context, Uri uri) {
        try {
            InputStream is = context.getContentResolver().openInputStream(uri);
            return BitmapFactory.decodeStream(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

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
//                    path.moveTo(x, y);
                }
//                else if (i < faceLandmarks.size() - 1) {
//                    Point next = faceLandmarks.get(i + 1);
//                    path.quadTo(x, y, getGapedCoord(next.x, avgX), getGapedCoord(next.y, avgY));
//                } else {
                    path.lineTo(x, y);
//                }
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

//    public static Bitmap getEdgedBitmap(Bitmap imageBitmap, Edges corners) {
//        return Bitmap.createBitmap(imageBitmap, corners.getLeftCoord(), corners.getBottomCoord(), corners.getWidth(), corners.getHeight());
//    }
//
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

        left -= gap;
        bottom -= gap;
        top += gap;
        right += gap;
        Edges edges = new Edges(left, right, top, bottom);
        Log.d(TAG, "getEdge: Edges: " + edges.toString());
        return edges;
    }

    public static Mat convertBitmap2Mat(Bitmap rgbaBitmap) {
        Mat rgbaMat = new Mat(rgbaBitmap.getHeight(), rgbaBitmap.getWidth(), CvType.CV_8UC4);
        Bitmap bmp32 = rgbaBitmap.copy(Bitmap.Config.ARGB_8888, true);
        org.opencv.android.Utils.bitmapToMat(bmp32, rgbaMat);

        Mat rgbMat = new Mat(rgbaBitmap.getHeight(), rgbaBitmap.getWidth(), CvType.CV_8UC3);
        Imgproc.cvtColor(rgbaMat, rgbMat, Imgproc.COLOR_RGBA2RGB, 3);
        return rgbMat;
    }

    private static int getGapedCoord(int coord, int avg) {
        return (int) (coord < avg ? coord - gap : coord + gap);
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

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

            return Uri.parse(mypath.toString());
        } catch (Exception e) {
            Log.e("SAVE_IMAGE", e.getMessage(), e);
        }
        return null;
    }
}