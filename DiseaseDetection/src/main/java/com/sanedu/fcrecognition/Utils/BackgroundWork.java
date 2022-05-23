package com.sanedu.fcrecognition.Utils;

import android.app.Activity;

import com.sanedu.fcrecognition.AnalysisResult.ResultUploadScreen;
import com.sanedu.fcrecognition.Start.SplashActivity;

public class BackgroundWork {
    private Activity activity;

    public BackgroundWork(Activity activity) {
        this.activity = activity;
    }

    public void startBackground() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                doInBackground();
                if(activity==null){
                    onPostExecute();
                }
                else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onPostExecute();
                        }
                    });
                }
            }
        }).start();
    }

    public void execute() {
        startBackground();
    }

    public void doInBackground() {
    }

    public void onPostExecute() {
    }
}