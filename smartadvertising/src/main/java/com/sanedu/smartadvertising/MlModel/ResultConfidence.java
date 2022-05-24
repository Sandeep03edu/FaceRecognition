package com.sanedu.smartadvertising.MlModel;

public class ResultConfidence {
    private String result;
    private double confidence;

    public ResultConfidence() {
        this.result = "";
        this.confidence = 0;
    }

    public ResultConfidence(String result, double confidence) {
        this.result = result;
        this.confidence = confidence;
    }

    public String getResult() {
        return result;
    }

    public double getConfidence() {
        return confidence;
    }

    @Override
    public String toString() {
        return "AgeGender{" +
                "ageGender='" + result + '\'' +
                ", confidence=" + confidence +
                '}';
    }
}
