package com.sanedu.fcrecognition;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.sanedu.common.Utils.Constants;

/**
 * Base application for FaceRecognition app
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Creating notification channel
        createNotificationChannel();
    }

    /**
     * Creating notification channel in Base application
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    Constants.CHANNEL_ID,
                    "Face result upload service",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
