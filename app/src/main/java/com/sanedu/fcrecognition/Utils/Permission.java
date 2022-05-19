package com.sanedu.fcrecognition.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class Permission {

    public static boolean CheckPermission(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    public static void RequestPermission(Activity activity, String[] permissions){
        ActivityCompat.requestPermissions(activity, permissions, 0);
    }
}
