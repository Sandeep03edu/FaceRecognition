package com.sanedu.fcrecognition.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.sanedu.common.Utils.Constants;
import com.sanedu.fcrecognition.Model.User;

public class SharedPrefData {

    /**
     * Method to add user to shared preference
     * @param context - Context - Activity context
     * @param user - User - My user model object
     */
    public static void addUser(Context context , User user){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String userGson = new Gson().toJson(user);
        editor.putString(Constants.USER_DETAILS, userGson);
        editor.apply();
    }

    /**
     * Method to get User
     * @param context - Context - Activity context
     * @return - User - Saved user details
     */
    public static User getUser(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        String userGson = sharedPreferences.getString(Constants.USER_DETAILS, "");
        if(userGson.trim().isEmpty()){
            return null;
        }
        User user = new Gson().fromJson(userGson, User.class);
        return user;
    }

    /**
     * Method to erase Shared pref data
     * @param context - Context - Activity context
     */
    public static void clearSharedPref(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
