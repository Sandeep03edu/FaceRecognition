package com.sanedu.fcrecognition.AnalysisResult;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sanedu.common.Utils.Constants;
import com.sanedu.fcrecognition.Model.ResultConfidence;
import com.sanedu.fcrecognition.R;

/**
 * @desc : Dialog activity to display predicted Age and Gender with confidence
 */
public class AgeGenderResCard extends AppCompatActivity {

    ImageView imageView;
    TextView result, confidence;
    ResultConfidence resultConfidence;
    String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_age_gender_res_card);

        // Initialing views
        _init();

        // Get Intent Data
        GetIntentData();

        // Set Intent Data
        SetIntentData();
    }

    /**
     * Setting Fetched intent data into Activity views
     */
    private void SetIntentData() {
        // Checking whether data exist or not
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

    /**
     * Displaying Gender type result data
     */
    private void SetGenderData() {
        // Setting Activity title
        setTitle("Gender");

        // Setting Male or Female icon
        if (resultConfidence.getResult().equalsIgnoreCase(Constants.MALE)) {
            imageView.setImageResource(R.drawable.ic_baseline_male_128);
        } else if (resultConfidence.getResult().equalsIgnoreCase(Constants.FEMALE)) {
            imageView.setImageResource(R.drawable.ic_baseline_female_128);
        }

        // Setting gender confidence value
        result.setText(resultConfidence.getResult());
        if(resultConfidence.getConfidence()!=-1) {
            confidence.setText(Constants.decimalFormat2.format(resultConfidence.getConfidence()) + "% confidence");
        }
    }

    /**
     * Displaying Age type result data
     */
    private void SetAgeData() {
        // Setting Activity title
        setTitle("Age Group");

        // Setting Age group icon
        imageView.setImageResource(R.drawable.age_vector);

        // Setting age confidence value
        result.setText(resultConfidence.getResult());
        if(resultConfidence.getConfidence()!=-1) {
            confidence.setText(Constants.decimalFormat2.format(resultConfidence.getConfidence()) + "% confidence");
        }
    }

    /**
     * Getting intent data from prev activity
     */
    private void GetIntentData() {
        // Checking whether data exist or not
        if(getIntent()!=null && getIntent().hasExtra(Constants.AG_MODEL) && getIntent().hasExtra(Constants.AG_TYPE)){
            type = getIntent().getStringExtra(Constants.AG_TYPE);
            String gson = getIntent().getStringExtra(Constants.AG_MODEL);
            resultConfidence = new Gson().fromJson(gson, ResultConfidence.class);
        }
    }

    /**
     * Initialising views
     */
    private void _init() {
        imageView = findViewById(R.id.age_gender_res_image);
        result = findViewById(R.id.age_gender_res_result);
        confidence = findViewById(R.id.age_gender_res_confidence);
    }
}