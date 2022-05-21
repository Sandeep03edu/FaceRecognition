package com.sanedu.fcrecognition.AnalysisResult;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sanedu.fcrecognition.Constants;
import com.sanedu.fcrecognition.Model.ResultConfidence;
import com.sanedu.fcrecognition.R;

public class AgeGenderResCard extends AppCompatActivity {

    public final String AGE_RESULT = "Age Group";
    public final String GENDER_RESULT = "Gender";


    ImageView imageView;
    TextView result, confidence;
    ResultConfidence resultConfidence;
    String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_age_gender_res_card);

        _init();

        // Get Intent Data
        GetIntentData();

        setTitle("Age Gender");

        // Set Intent Data
        SetIntentData();
    }

    private void SetIntentData() {
        if(resultConfidence !=null && type!=null && !type.trim().isEmpty()){
            if(type.equalsIgnoreCase(Constants.AGE)){
                // Age result Data
                SetAgeData();
            }
            else if(type.equalsIgnoreCase(Constants.GENDER)){
                // Gender result Data
                SetGenderData();
            }
        }
    }

    private void SetGenderData() {
        setTitle("Gender");
        if (resultConfidence.getResult().equalsIgnoreCase(Constants.MALE)) {
            imageView.setImageResource(R.drawable.ic_baseline_male_128);
        } else if (resultConfidence.getResult().equalsIgnoreCase(Constants.FEMALE)) {
            imageView.setImageResource(R.drawable.ic_baseline_female_128);
        }
        result.setText(resultConfidence.getResult());
        confidence.setText(Constants.decimalFormat2.format(resultConfidence.getConfidence()) + "% confidence");
    }

    private void SetAgeData() {
        setTitle("Age Group");
        imageView.setImageResource(R.drawable.age_vector);
        result.setText(resultConfidence.getResult());
        confidence.setText(Constants.decimalFormat2.format(resultConfidence.getConfidence()) + "% confidence");
    }

    private void GetIntentData() {
        if(getIntent()!=null && getIntent().hasExtra(Constants.AG_MODEL) && getIntent().hasExtra(Constants.AG_TYPE)){
            type = getIntent().getStringExtra(Constants.AG_TYPE);
            String gson = getIntent().getStringExtra(Constants.AG_MODEL);
            resultConfidence = new Gson().fromJson(gson, ResultConfidence.class);
        }
    }

    private void _init() {
        imageView = findViewById(R.id.age_gender_res_image);
        result = findViewById(R.id.age_gender_res_result);
        confidence = findViewById(R.id.age_gender_res_confidence);
    }
}