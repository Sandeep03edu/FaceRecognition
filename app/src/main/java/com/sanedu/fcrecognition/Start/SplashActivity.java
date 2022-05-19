package com.sanedu.fcrecognition.Start;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.sanedu.fcrecognition.MainActivity;
import com.sanedu.fcrecognition.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            // User already signed in
            Intent loginIntent = new Intent(this, MainActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
            finish();
        }
        else{
            // User not signed in
            Intent registrationIntent = new Intent(this, AuthenticationActivity.class);
            registrationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(registrationIntent);
            finish();
        }
    }
}