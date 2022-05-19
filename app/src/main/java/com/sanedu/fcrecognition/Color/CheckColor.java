package com.sanedu.fcrecognition.Color;

import android.graphics.Color;
import android.util.Log;

import com.sanedu.fcrecognition.Model.ColorCode;

public class CheckColor {
    private static final String TAG = "CheckColorTag";
    private static final ColorCode RED_Color = new ColorCode(255,0,0);

    public static boolean isRed(int c) {
        int r = Color.red(c);
        int g = Color.green(c);
        int b = Color.blue(c);

        ColorCode given = new ColorCode(r,g,b);
        double simi = ColorCodeConversion.Delta94Value(given, RED_Color);
        Log.d(TAG, "isRed: " + simi);
        return simi<=50;
    }
}
