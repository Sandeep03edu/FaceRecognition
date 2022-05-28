package com.sanedu.fcrecognition.Color;

import android.graphics.Color;
import android.util.Log;

import com.sanedu.fcrecognition.Model.ColorCode;

/**
 * Class to check color similarity
 */

public class CheckColor {
    private static final String TAG = "CheckColorTag";

    // Static fixed ColorCode
    private static final ColorCode RED_Color = new ColorCode(218, 63, 61);
    private static final ColorCode BLACK_Color = new ColorCode(0, 0, 0);
    private static final ColorCode WHITE_Color = new ColorCode(255, 255, 255);
    private static final ColorCode PINK_Color = new ColorCode(235, 181, 184);

    /**
     * @param c  - Given color
     * @param n  - Index of precision
     * @return - Boolean, is color c similar to Red color or not
     */
    public static boolean isRed(int c, int n) {
        return comp(c, RED_Color, n);
    }

    /**
     * @param c  - Given color
     * @param n  - Index of precision
     * @return - Boolean, is color c similar to Black color or not
     */
    public static boolean isBlack(int c, int n) {
        return comp(c, BLACK_Color, n);
    }

    /**
     * @param c  - Given color
     * @param n  - Index of precision
     * @return - Boolean, is color c similar to Pink color or not
     */
    public static boolean isPink(int c, int n) {
        return comp(c, PINK_Color, n);
    }

    /**
     * @param c  - Given color
     * @param n  - Index of precision
     * @return - Boolean, is color c similar to White color or not
     */
    public static boolean isWhite(int c, int n) {
        return comp(c, WHITE_Color, n);
    }

    /**
     * Comparing two colorCode
     * @param c - Given Color Pixel
     * @param com - Comparable ColorCode
     * @param n - Index of precision
     * @return - Boolean, is color c similar to com or not
     */
    private static boolean comp(int c, ColorCode com, int n) {
        int r = Color.red(c);
        int g = Color.green(c);
        int b = Color.blue(c);

        ColorCode given = new ColorCode(r, g, b);
        double simi = ColorCodeConversion.Delta94Value(given, com);
        if(simi<=n){
            Log.d(TAG, "comp: detectRedness Simi: " + simi);
        }
        return simi <= n;
    }
}
