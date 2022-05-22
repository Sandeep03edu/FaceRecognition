package com.sanedu.fcrecognition;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;

public class Constants {

    public static final String FIREBASE_USER_TABLE = "UserList";
    public static final String FIREBASE_RECORD_TABLE = "UserRecord";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String AVATAR = "Default";
    public static final DecimalFormat decimalFormat2 = new DecimalFormat("0.00");

    // Result upload service Constants
    public static final String RESULT_IMAGE_URI = "ResultImageUri";
    public static final String RESULT_DATA = "ResultData";
    public static final String CHANNEL_ID = "ResultUpload";
    public static final int FOREGROUND_NOTIFICATION_ID = 12;


    // Crop ImageView Constants
    public static final String DISABLE_ASPECT_CROP = "Disable";
    public static final String IMAGES = "Images";
    public static final String IMAGES_BUNDLE = "ImagesBundle";
    public static final int CROP_IMAGE_REQUEST_CODE = 2712;
    public static final String CROP_CAMERA = "CameraCrop";

    // Age Gender Constants
    public static final String AG_TYPE = "AgeGenderType";
    public static final String AGE = "Age";
    public static final String GENDER = "Gender";
    public static final String MALE = "Male";
    public static final String FEMALE = "Female";
    public static final String AG_MODEL = "AgeGenderModel";

    // Dual Image Constants
    public static final String DUAL_IMAGE_TEST = "DualImageTest";
    public static final String EYE_BROW_TEST = "Eyebrow Alopecia detection";
    public static final String EYE_RED_TEST = "Eye Redness detection";
    public static final String LIPS_TEST = "Chapped Lips detection";

    // Intents Constants
    public static final String IMAGE_URI = "ImageUri";
    public static final String IMAGE_BITMAP_BYTES = "ImageBitmapBytes";

    // Start Screen Constants
    public static final int START_LOGIN = 0;
    public static final int START_REGISTRATION = 1;

    // Shared Pref Constants
    public static final String SHARED_PREF = "FcRSharePref";
    public static final String USER_DETAILS = "UserDetails";

    // Failure reasons
    public static final String USER_DNE = "User doesn't exist";
    public static final String USER_ALR_EXIST = "User already exist";
    public static final String ALL_COMP = "All fields are compulsory";
    public static final String AN_ERROR = "An error occurred : ";

    public static final String SHAPE_LOCATION = "ShapeLocation";


    public static String getFaceShapeModelPath() {
//        File sdcard = Environment.getExternalStorageDirectory();
        File sdcard = Environment.getExternalStorageDirectory();
        String targetPath = sdcard.getAbsolutePath() + File.separator + "shape_predictor_68_face_landmarks.dat";
        return targetPath;
    }

    public static void saveShape(Activity activity){
        InputStream shapePredictorIs = activity.getResources().openRawResource(R.raw.shape_predictor_68_face_landmarks);
        File cascadeDir = activity.getDir("cascadeDir", Context.MODE_PRIVATE);
        File shapePredictorFile = new File(cascadeDir, "shape_predictor_68_face_landmarks.dat");

        try {
            FileOutputStream shapePredictorFos = new FileOutputStream(shapePredictorFile);
            byte[] faceFrontalBuffer = new byte[4096];
            int byteRead;
            while ((byteRead = shapePredictorIs.read(faceFrontalBuffer)) != -1) {
                shapePredictorFos.write(faceFrontalBuffer, 0, byteRead);
            }


            shapePredictorIs.close();
            shapePredictorFos.close();

        }catch (Exception e){
            
        }


    }

}
