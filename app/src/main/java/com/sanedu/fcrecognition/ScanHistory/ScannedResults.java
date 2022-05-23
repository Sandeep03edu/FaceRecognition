package com.sanedu.fcrecognition.ScanHistory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.sanedu.fcrecognition.Constants;
import com.sanedu.fcrecognition.Firebase.FirestoreData;
import com.sanedu.fcrecognition.Model.FaceResult;
import com.sanedu.fcrecognition.Navigation;
import com.sanedu.fcrecognition.R;

import java.util.ArrayList;

public class ScannedResults extends AppCompatActivity {

    DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    RecyclerView recyclerView;
    ScannedResultAdapter adapter;
    ArrayList<FaceResult> faceResultArrayList = new ArrayList<>();
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_results);
        _init();

        SetupDrawerLayout();

        SetNavigationListener();

        GetResultsData();
    }

    private void GetResultsData() {
        FirestoreData data = new FirestoreData();
        showDialog();
        data.getPastScannedHistory(new FirestoreData.ResultListener() {
            @Override
            public void onSuccess(ArrayList<FaceResult> faceResults) {
                dismissDialog();
                if (faceResults != null) {
                    faceResultArrayList = faceResults;
                    adapter = new ScannedResultAdapter(ScannedResults.this, faceResultArrayList);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(String err) {
                dismissDialog();
                Toast.makeText(ScannedResults.this, Constants.AN_ERROR + err, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void SetNavigationListener() {
        Navigation navigation = new Navigation(this, drawerLayout);
        navigationView.setNavigationItemSelectedListener(navigation.listener);
    }

    private void SetupDrawerLayout() {
        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialog() {
        if (progressDialog != null) {
            progressDialog.show();
        }
    }

    private void dismissDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void _init() {
        setTitle("Scan History");
        drawerLayout = findViewById(R.id.scanned_res_drawer_layout);
        navigationView = findViewById(R.id.scanned_res_navigation_view);

        recyclerView = findViewById(R.id.scanned_res_rcv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Fetching record");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
    }
}