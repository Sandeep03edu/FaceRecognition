package com.sanedu.fcrecognition;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.sanedu.fcrecognition.Home.HomeActivity;
import com.sanedu.fcrecognition.Profile.MyProfileActivity;
import com.sanedu.fcrecognition.ScanHistory.ScannedResults;
import com.sanedu.fcrecognition.Start.SplashActivity;
import com.sanedu.fcrecognition.Utils.SharedPrefData;

public class Navigation {
    private static final String TAG = "NavigationTag";
    Activity activity;
    DrawerLayout drawerLayout;

    public Navigation(Activity activity, DrawerLayout drawerLayout) {
        this.activity = activity;
        this.drawerLayout = drawerLayout;
    }

    public NavigationView.OnNavigationItemSelectedListener listener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    if (activity.getClass().getSimpleName().equalsIgnoreCase(HomeActivity.class.getSimpleName())) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    } else {
                        Intent homeIntent = new Intent(activity, HomeActivity.class);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        activity.startActivity(homeIntent);
                        activity.finish();
                        return true;
                    }
                case R.id.nav_profile:
                    if (activity.getClass().getSimpleName().equalsIgnoreCase(MyProfileActivity.class.getSimpleName())) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    } else {
                        Intent myProfileIntent = new Intent(activity, MyProfileActivity.class);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        activity.startActivity(myProfileIntent);
                        return true;
                    }
                case R.id.nav_history:
                    if (activity.getClass().getSimpleName().equalsIgnoreCase(ScannedResults.class.getSimpleName())) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    } else {
                        Intent scannedResultIntent = new Intent(activity, ScannedResults.class);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        activity.startActivity(scannedResultIntent);
                        return true;
                    }
                case R.id.nav_logout:
                    DisplayLogoutDialog();

            }
            return false;
        }
    };

    private void DisplayLogoutDialog() {
        new AlertDialog.Builder(activity)
                .setTitle("Are you sure")
                .setMessage("You want to logout?")
                .setNeutralButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        FirebaseAuth.getInstance().signOut();
                        SharedPrefData.clearSharedPref(activity);

                        Intent homeIntent = new Intent(activity, SplashActivity.class);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        activity.startActivity(homeIntent);
                        activity.finish();
                    }
                })
                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();


    }
}