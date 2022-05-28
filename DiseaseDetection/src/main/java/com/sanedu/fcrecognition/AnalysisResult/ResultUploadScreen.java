package com.sanedu.fcrecognition.AnalysisResult;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sanedu.common.Utils.BackgroundWork;
import com.sanedu.common.Utils.Constants;
import com.sanedu.fcrecognition.Model.FaceResult;
import com.sanedu.fcrecognition.R;
import com.sanedu.common.Utils.LayoutUtils;

import java.util.ArrayList;

/**
 * Dialog Activity to ask name, gender and age to upload faceResult
 */
public class ResultUploadScreen extends AppCompatActivity {

    EditText nameEt, ageEt;
    Spinner genderSpinner;
    TextView uploadData;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_upload_screen);

        // Initialising views
        _init();

        // Setting up gender spinner
        SetupGenderSpinner();

        // Uploading data to server
        UploadData();
    }

    /**
     * Function to upload data to server
     */
    private void UploadData() {
        uploadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Checking all fields are filled or not
                if (LayoutUtils.checkFilled(new EditText[]{nameEt, ageEt})) {
                    if (genderSpinner != null && genderSpinner.getSelectedItem() != null) {
                        showDialog();

                        // Performing background task
                        new BackgroundWork(ResultUploadScreen.this){
                            @Override
                            public void doInBackground() {
                                super.doInBackground();
                                // Setting data to service intent
                                Intent uploadService = new Intent(ResultUploadScreen.this, ResultUploadService.class);
                                uploadService.putExtra(Constants.RESULT_IMAGE_URI, getIntent().getStringExtra(Constants.RESULT_IMAGE_URI));

                                FaceResult result = new FaceResult();
                                result.setInitData();
                                result.setPatientName(nameEt.getText().toString().trim());
                                result.setAge(Integer.parseInt(ageEt.getText().toString().trim()));
                                result.setGender(genderSpinner.getSelectedItem().toString());

                                // Starting foreground service
                                uploadService.putExtra(Constants.RESULT_DATA, new Gson().toJson(result));
                                startService(uploadService);
                            }

                            @Override
                            public void onPostExecute() {
                                super.onPostExecute();
                                // Moving to ResultPageActivity.java class with RESULT_OK status
                                dismissDialog();
                                Intent resultIntent = new Intent();
                                setResult(RESULT_OK, resultIntent);
                                finish();
                            }
                        }.execute();
                    }
                }
            }
        });
    }

    /**
     * Setting up gender spinner
     */
    private void SetupGenderSpinner() {
        // ArrayList of genders
        ArrayList<String> genders = new ArrayList<>();
        genders.add(Constants.MALE);
        genders.add(Constants.FEMALE);

        // Setting adapter
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_item, genders);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(arrayAdapter);
    }


    /**
     * Displaying dialog if not null
     */
    private void showDialog() {
        if (progressDialog != null) {
            progressDialog.show();
        }
    }

    /**
     * Removing dialog if not null
     */
    private void dismissDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * Initialising views
     */
    private void _init() {
        setTitle("Save Result");
        nameEt = findViewById(R.id.result_upload_screen_name);
        ageEt = findViewById(R.id.result_upload_screen_age);
        genderSpinner = findViewById(R.id.result_upload_screen_gender);
        uploadData = findViewById(R.id.result_upload_screen_save_data);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading data");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
    }
}