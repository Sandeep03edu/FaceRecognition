package com.sanedu.fcrecognition.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.sanedu.common.Utils.Constants;
import com.sanedu.fcrecognition.Model.User;

public class SharedPrefData {

    public static void addUser(Context context , User user){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String userGson = new Gson().toJson(user);
        editor.putString(Constants.USER_DETAILS, userGson);
        editor.apply();
    }

    public static User getUser(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        String userGson = sharedPreferences.getString(Constants.USER_DETAILS, "");
        if(userGson.trim().isEmpty()){
            return null;
        }
        User user = new Gson().fromJson(userGson, User.class);
        return user;
    }

    public static void clearSharedPref(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public static void saveString(Context context, String label,  String s){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(label, s);
        editor.apply();
    }

    public static String getString(Context context, String label){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        String data = sharedPreferences.getString(label, "");
        if(data==null){
            data = "";
        }
        return data;
    }
}
