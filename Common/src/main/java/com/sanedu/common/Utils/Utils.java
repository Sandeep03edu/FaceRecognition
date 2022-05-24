package com.sanedu.common.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Utils {

    private static final String TAG = "UtilsTag";

    public static String getRawFilePath(int rawFile, Context context) {
        String path = null;
        try {
            InputStream is = context.getResources().openRawResource(rawFile);
            byte[] data = new byte[is.available()];
            is.read(data);
            is.close();

//            String fileName = getResources().getResourceName(rawFile);
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

    public static Bitmap rotateBitmap(Bitmap orgBitmap, int degree){
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(orgBitmap, orgBitmap.getWidth(), orgBitmap.getHeight(), true);
        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        return rotatedBitmap;
    }

}
