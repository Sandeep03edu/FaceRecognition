package com.sanedu.fcrecognition.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.sanedu.fcrecognition.Constants;
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
            return new User();
        }
        User user = new Gson().fromJson(userGson, User.class);
        if(user==null){
            return new User();
        }
        return user;
    }
}
