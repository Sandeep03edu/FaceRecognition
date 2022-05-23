package com.sanedu.fcrecognition.Utils;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class LayoutUtils {

    public static boolean checkFilled(EditText[] editTexts) {
        for (EditText editText : editTexts) {
            if (editText == null || editText.getText() == null || editText.getText().toString().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static void disableViews(View[] views) {
        for (View view : views) {
            if (view != null) {
                view.setEnabled(false);
            }
        }
    }

    public static void enableViews(View[] views) {
        for (View view : views) {
            if (view != null) {
                view.setEnabled(true);
            }
        }
    }

    public static void fixRatioImageView(Context context, int n, View[] imageViews) {
        int displayWidth = context.getResources().getDisplayMetrics().widthPixels;
        int width = displayWidth / n;
        int height = displayWidth / n;

        for (View imageView : imageViews) {
            imageView.getLayoutParams().width = width;
            imageView.getLayoutParams().height = height;
        }
    }

    public static void setAspectRation(View[] views){
        for(View view : views){
            view.getLayoutParams().height = view.getLayoutParams().width;
        }
    }

    public static boolean checkNonNull(Object[] objects){
        for(Object object : objects){
            if(object==null){
                return false;
            }
        }
        return true;
    }
}
