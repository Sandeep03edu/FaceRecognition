package com.sanedu.fcrecognition.AnalysisResult;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sanedu.common.Utils.Constants;
import com.sanedu.fcrecognition.Model.FaceResult;
import com.sanedu.fcrecognition.R;
import com.sanedu.common.Utils.LayoutUtils;

import java.util.ArrayList;

public class ResultUploadScreen extends AppCompatActivity {

    EditText nameEt, ageEt;
    Spinner genderSpinner;
    TextView uploadData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_upload_screen);

        _init();

        SetupSpinner();

        UploadData();
    }

    private void UploadData() {
        uploadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (LayoutUtils.checkFilled(new EditText[]{nameEt, ageEt})) {
                    if (genderSpinner != null && genderSpinner.getSelectedItem() != null) {
                        Intent uploadService = new Intent(ResultUploadScreen.this, ResultUploadService.class);
                        uploadService.putExtra(Constants.RESULT_IMAGE_URI, getIntent().getStringExtra(Constants.RESULT_IMAGE_URI));

                        FaceResult result = new FaceResult();
                        result.setInitData();
                        result.setPatientName(nameEt.getText().toString().trim());
                        result.setAge(Integer.parseInt(ageEt.getText().toString().trim()));
                        result.setGender(genderSpinner.getSelectedItem().toString());

                        uploadService.putExtra(Constants.RESULT_DATA, new Gson().toJson(result));
                        startService(uploadService);
                        finish();
                    }
                }
            }
        });
    }

    private void SetupSpinner() {
        ArrayList<String> genders = new ArrayList<>();
        genders.add(Constants.MALE);
        genders.add(Constants.FEMALE);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_item, genders);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(arrayAdapter);
    }

    private void _init() {
        setTitle("Save Result");
        nameEt = findViewById(R.id.result_upload_screen_name);
        ageEt = findViewById(R.id.result_upload_screen_age);
        genderSpinner = findViewById(R.id.result_upload_screen_gender);
        uploadData = findViewById(R.id.result_upload_screen_save_data);
    }
}