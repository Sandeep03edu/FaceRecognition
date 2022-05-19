package com.sanedu.fcrecognition;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.sanedu.fcrecognition.Color.CheckColor;
import com.sanedu.fcrecognition.Utils.Utils;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.util.Arrays;

public class FaceSymptomScorer {
    private static final String TAG = "FaceSymptomScorerTag";
    Bitmap bitmap;

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    private Scalar getMeanRGB() {
        int redColors = 0;
        int greenColors = 0;
        int blueColors = 0;
        int pixelCount = 0;
        for (int y = 0; y < bitmap.getHeight(); y++) {
            for (int x = 0; x < bitmap.getWidth(); x++) {
                int c = bitmap.getPixel(x, y);
                pixelCount++;
                redColors += Color.red(c);
                greenColors += Color.green(c);
                blueColors += Color.blue(c);
            }
        }

        // calculate average of bitmap r,g,b values
        int red = (redColors / pixelCount);
        int green = (greenColors / pixelCount);
        int blue = (blueColors / pixelCount);

        Scalar mean = new Scalar(red, green, blue);

        return mean;
    }

    private Scalar getDispersion() {
        Scalar mean = getMeanRGB();
        int redColors = 0;
        int greenColors = 0;
        int blueColors = 0;
        int pixelCount = 0;
        for (int y = 0; y < bitmap.getHeight(); y++) {
            for (int x = 0; x < bitmap.getWidth(); x++) {
                int c = bitmap.getPixel(x, y);
                pixelCount++;
                redColors += Math.pow(Color.red(c) - mean.val[0], 2);
                greenColors += Math.pow(Color.green(c) - mean.val[1], 2);
                blueColors += Math.pow(Color.blue(c) - mean.val[2], 2);
            }
        }

        redColors = (int) Math.pow(redColors / pixelCount, 0.5);
        greenColors = (int) Math.pow(greenColors / pixelCount, 0.5);
        blueColors = (int) Math.pow(blueColors / pixelCount, 0.5);


        Scalar dispersion = new Scalar(redColors, greenColors, blueColors);
        Log.d(TAG, "dispersion: Disp: " + dispersion.toString());
        return dispersion;
    }

    private Scalar getLowerBound() {
        Scalar mean = getMeanRGB();
        Scalar disp = getDispersion();
        Scalar lower = new Scalar(mean.val[0] - disp.val[0], mean.val[1] - disp.val[1], mean.val[2] - disp.val[2]);
        Log.d(TAG, "lower: " + lower.toString());
        return lower;
    }

    private Scalar getUpperBound() {
        Scalar mean = getMeanRGB();
        Scalar disp = getDispersion();
        Scalar upper = new Scalar(mean.val[0] + disp.val[0], mean.val[1] + disp.val[1], mean.val[2] + disp.val[2]);
        Log.d(TAG, "Upper: " + upper.toString());
        return upper;
    }

    public int detectColor() {
        Mat detectMat = Utils.convertBitmap2Mat(bitmap);
        Core.inRange(detectMat, getLowerBound(), getUpperBound(), detectMat);
        return getNonZero(detectMat);
    }

    private int getNonZero(Mat mat) {
        Log.d(TAG, "getNonZero: ");
        int ct = 0;

        if (mat != null && !mat.empty()) {

            for (int i = 0; i < mat.cols(); ++i) {
                for (int j = 0; j < mat.rows(); ++j) {
                    double[] b = mat.get(i, j);
                    Log.d(TAG, "getNonZero: B: " + Arrays.toString(b));
                    if (b != null && b[0] != 0) {
                        ct++;
                    }
                }
            }
            Log.d(TAG, "getNonZero: Count: " + ct);
        } else {
            Log.d(TAG, "getNonZero: Null mat");
        }
        return ct;
    }

    public int detectRedness() {
        int redColors = 0;
        int pixelCount = 0;
        for (int y = 0; y < bitmap.getHeight(); y++) {
            for (int x = 0; x < bitmap.getWidth(); x++) {
                if (CheckColor.isRed(bitmap.getPixel(x, y))) {
                    redColors++;
                }
                pixelCount++;
                int c = bitmap.getPixel(x, y);
                Log.d(TAG, "detectRedness: Red: " + redColors + " Pixel: " + pixelCount);
//                Log.d(TAG, "detectRedness: R: " + Color.red(c) + " G: " + Color.green(c) + " B: " + Color.blue(c));
            }
        }

        Log.d(TAG, "detectRedness: RedCount: " + redColors + " TotalPix: " + pixelCount);

        redColors *= 100;
        return redColors / pixelCount;

    }
}
