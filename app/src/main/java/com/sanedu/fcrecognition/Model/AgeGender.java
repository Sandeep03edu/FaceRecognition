package com.sanedu.fcrecognition.Model;

public class AgeGender {
    private String ageGender;
    private double confidence;

    public AgeGender() {
        this.ageGender = "";
        this.confidence = 0;
    }

    public AgeGender(String ageGender, double confidence) {
        this.ageGender = ageGender;
        this.confidence = confidence;
    }

    public String getAgeGender() {
        return ageGender;
    }

    public double getConfidence() {
        return confidence;
    }

    @Override
    public String toString() {
        return "AgeGender{" +
                "ageGender='" + ageGender + '\'' +
                ", confidence=" + confidence +
                '}';
    }
}
