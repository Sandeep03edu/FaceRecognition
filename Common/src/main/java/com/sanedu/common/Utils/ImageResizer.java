package com.sanedu.common.Utils;

import android.graphics.Bitmap;
import android.util.Log;

public class ImageResizer {

    public static Bitmap reduceBitmapSize(Bitmap bitmap, int MAX_SIZE){
        double ratioSq;
        int bitmapHeight, bitmapWidth;
        bitmapHeight = bitmap.getHeight();
        bitmapWidth = bitmap.getWidth();
        ratioSq = bitmapHeight*bitmapWidth;
        ratioSq = ratioSq/MAX_SIZE;

        if(ratioSq<=1){
            return bitmap;
        }

        double ratio = Math.sqrt(ratioSq);
        Log.d("imageResizer", "ratio: "+ ratio);

        int reqHeight = (int) Math.round(bitmapHeight/ratio);
        int reqWidth = (int) Math.round(bitmapWidth/ratio);

        return Bitmap.createScaledBitmap(bitmap, reqWidth, reqHeight, true);
    }
}
