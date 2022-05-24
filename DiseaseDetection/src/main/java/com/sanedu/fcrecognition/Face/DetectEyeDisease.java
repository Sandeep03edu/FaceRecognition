package com.sanedu.fcrecognition.Face;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.sanedu.fcrecognition.Model.ResultConfidence;
import com.sanedu.common.Utils.ImageResizer;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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

    public DetectEyeDisease() {
//        initTensorFlowAndLoadModel();
        Log.d(TAG, "DetectEyeDisease: ");
    }

    public void setBitmap(Context context, Bitmap bitmap) {
        Log.d(TAG, "setBitmap: ");
        this.context = context;
        this.bitmap = bitmap;
        this.bitmap = ImageResizer.reduceBitmapSize(bitmap, 240000);
    }

    private void initTensorFlowAndLoadModel() {
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
            } catch (final Exception e) {
                Log.e(TAG, "initTensorFlowAndLoadModel: Err", e);
                throw new RuntimeException("Error initializing TensorFlow!", e);
            }
        });
    }

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

                listener.onExecutionComplete(resultConfidence);

            } catch (final Exception e) {
                Log.e(TAG, "initTensorFlowAndLoadModel: Err", e);
                listener.onExecutionFailed(e);
                throw new RuntimeException("Error initializing TensorFlow!", e);
            }
        });

//        return new ResultConfidence(res, Double.parseDouble(perc));
    }

    private List<Classifier.Recognition> analyse() {
        Log.d(TAG, "analyse: 1st");
        if (bitmap == null) {
            Log.d(TAG, "analyse: Bitmap null");
        }
        bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);

        Log.d(TAG, "analyse: 2nd");
        if (bitmap == null) {
            Log.d(TAG, "analyse: Bitmap null after create");
        }
        if (classifier == null) {
            Log.d(TAG, "analyse: Classifier null after create");
        }
        final List<Classifier.Recognition> results = classifier.recognizeImage(bitmap);

        Log.d(TAG, "analyse: 3rd");
        return results;
    }

    public interface ExecutorListener {
        void onExecutionComplete(ResultConfidence resultConfidence);

        void onExecutionFailed(Exception e);
    }
}
