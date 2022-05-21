package com.sanedu.fcrecognition.Color;

import android.graphics.Color;
import android.util.Log;

import com.sanedu.fcrecognition.Model.ColorCode;

public class CheckColor {
    private static final String TAG = "CheckColorTag";
    private static final ColorCode RED_Color = new ColorCode(255, 0, 0);
    private static final ColorCode BLACK_Color = new ColorCode(0, 0, 0);
    private static final ColorCode WHITE_Color = new ColorCode(255, 255, 255);
    private static final ColorCode PINK_Color = new ColorCode(235, 181, 184);

    public static boolean isRed(int c, int n) {
        return comp(c, RED_Color, n);
    }

    public static boolean isBlack(int c, int n) {
        return comp(c, BLACK_Color, n);
    }

    public static boolean isPink(int c, int n){
        return comp(c, PINK_Color, n);
    }

    public static boolean isWhite(int c, int n) {
        return comp(c, WHITE_Color, n);
    }

    private static boolean comp(int c, ColorCode com, int n) {
        int r = Color.red(c);
        int g = Color.green(c);
        int b = Color.blue(c);

        ColorCode given = new ColorCode(r, g, b);
        double simi = ColorCodeConversion.Delta94Value(given, com);
        return simi <= n;
    }
}
