package com.sanedu.fcrecognition.Utils;

import android.view.View;
import android.widget.EditText;

public class LayoutUtils {

    public static boolean checkFilled(EditText[] editTexts){
        for(EditText editText : editTexts){
            if(editText==null || editText.getText()==null || editText.getText().toString().trim().isEmpty()){
                return false;
            }
        }
        return true;
    }

    public static void disableViews(View[] views){
        for(View view : views){
            if(view!=null) {
                view.setEnabled(false);
            }
        }
    }

    public static void enableViews(View[] views){
        for(View view : views){
            if(view!=null) {
                view.setEnabled(true);
            }
        }
    }
}
