package com.sanedu.fcrecognition;

public class Constants {

    public static final String FIREBASE_USER_TABLE = "UserList";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String AVATAR = "Default";

    // Age Gender Constants
    public static final String AG_TYPE = "AgeGenderType";
    public static final String AGE = "Age";
    public static final String GENDER = "Gender";
    public static final String MALE = "Male";
    public static final String FEMALE = "Female";
    public static final String AG_MODEL = "AgeGenderModel";


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
}
