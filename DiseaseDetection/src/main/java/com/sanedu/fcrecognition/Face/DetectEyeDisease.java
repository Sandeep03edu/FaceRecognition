package com.sanedu.fcrecognition.Face;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.sanedu.fcrecognition.Model.ResultConfidence;
import com.sanedu.common.Utils.ImageResizer;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author Sandeep
 * DetectEyeDisease java class to detect eye diseases
 */
public class DetectEyeDisease {

    private static final String TAG = "DetectEyeDiseaseTag";
    private Executor executor = Executors.newSingleThreadExecutor();
    private Classifier classifier;
    private static final String MODEL_FILE = "file:///android_asset/graph.pb";
    private static final String LABEL_FILE = "file:///android_asset/labels.txt";
    private static final int INPUT_SIZE = 224;
    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128.0f;
    private static final String INPUT_NAME = "input";
    private static final String OUTPUT_NAME = "final_result";
    private Context context;
    private Bitmap bitmap;

    /**
     * Empty constructor
     */
    public DetectEyeDisease() {
        Log.d(TAG, "DetectEyeDisease: ");
    }

    /**
     * Method to initialise bitmao
     *
     * @param context - Context
     * @param bitmap  - Bitmap - ImageBitmap
     */
    public void setBitmap(Context context, Bitmap bitmap) {
        Log.d(TAG, "setBitmap: ");
        this.context = context;
        this.bitmap = bitmap;
        this.bitmap = ImageResizer.reduceBitmapSize(bitmap, 240000);
    }

    /**
     * Method to get eye disease result
     *
     * @param listener - Listener to get eye disease updates
     */
    public void getResult(ExecutorListener listener) {
        executor.execute(() -> {
            try {
                Log.d(TAG, "initTensorFlowAndLoadModel: try start");
                classifier = TensorFlowImageClassifier.create(
                        context.getAssets(),
                        MODEL_FILE,
                        LABEL_FILE,
                        INPUT_SIZE,
                        IMAGE_MEAN,
                        IMAGE_STD,
                        INPUT_NAME,
                        OUTPUT_NAME);
                Log.d(TAG, "initTensorFlowAndLoadModel: try end");

                Log.d(TAG, "getResult: 1st");
                List<Classifier.Recognition> results = analyse();

                Log.d(TAG, "getResult: 2nd");
                String res = (results.get(0).toString().split("] ")[1]).split(" \\(")[0];
                String perc = (results.get(0).toString().split(" \\(")[1]).split("%\\)")[0];
                ResultConfidence resultConfidence = new ResultConfidence(res, Double.parseDouble(perc));
                Log.d(TAG, "getResult: 3rd");

                // Updating listener on completing execution
                listener.onExecutionComplete(resultConfidence);
            } catch (final Exception e) {
                Log.e(TAG, "initTensorFlowAndLoadModel: Err", e);
                // Updating listener with failed execution
                listener.onExecutionFailed(e);
                throw new RuntimeException("Error initializing TensorFlow!", e);
            }
        });
    }

    /**
     * Method to analyse data
     *
     * @return -  List<Classifier.Recognition>
     */
    private List<Classifier.Recognition> analyse() {
        Log.d(TAG, "analyse: 1st");
        bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);
        return classifier.recognizeImage(bitmap);
    }

    /**
     * Interface used by DetectEyeDisease class  
     */
    public interface ExecutorListener {
        void onExecutionComplete(ResultConfidence resultConfidence);

        void onExecutionFailed(Exception e);
    }
}