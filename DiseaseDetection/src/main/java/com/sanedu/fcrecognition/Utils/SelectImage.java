package com.sanedu.fcrecognition.Utils;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.sanedu.fcrecognition.R;

/**
 * @author Sandeep
 * SelectImage class to display PopupWindow
 */
public class SelectImage {

    private Context context;
    private View view;

    /**
     * Constructor
     * @param context - Context - Activity context
     * @param view - View - View where
     */
    public SelectImage(Context context, View view) {
        this.context = context;
        this.view = view;
    }

    /**
     * Method to display popupWindow
     * @param selectImageListener - Listener to listen Gallery or Camera pick action
     */
    public void GetImage(SelectImageListener selectImageListener) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.image_selector, null);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        params.setMargins(20, 10, 20, 10);
        popupView.setLayoutParams(params);

        boolean focusable = true; // lets taps outside the popup also dismiss it
        PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.setElevation(20);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        LinearLayout gallery, camera;
        gallery = popupView.findViewById(R.id.image_selector_gallery);
        camera = popupView.findViewById(R.id.image_selector_camera);

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                selectImageListener.onGalleryPick();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                selectImageListener.onCameraPick();
            }
        });
    }

    public interface SelectImageListener {
        void onGalleryPick();

        void onCameraPick();
    }
}
