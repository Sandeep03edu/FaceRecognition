package com.sanedu.fcrecognition.Model;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

/**
 * @author Sandeep
 * FaceResult model class
 */
public class FaceResult {
    private String resultId;
    private String uId;
    private String patientName;
    private int age;
    private String gender;
    private long uploadTime;

    private String imageUrl;

    private String leftEyebrowResult;
    private String rightEyebrowResult;
    private String leftEyeResult;
    private String rightEyeResult;
    private String upperLipResult;
    private String lowerLipResult;


    /**
     * Empty constructor
     * Required by firebase
     */
    public FaceResult() {
    }

    /**
     * Setting initial data
     */
    public void setInitData(){
        this.uploadTime = System.currentTimeMillis();
        this.uId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        this.resultId = this.uId + this.uploadTime;
    }

    /**
     * Constructor
     * @param patientName - String - PatientName
     * @param age - int - user age
     * @param gender - String - user gender
     * @param imageUrl - String - imageUrl
     * @param leftEyebrowResult - String - leftEyebrow predicted Data
     * @param rightEyebrowResult - String - rightEyebrow predicted Data
     * @param leftEyeResult - String - leftEye predicted Data
     * @param rightEyeResult - String - rightEye predicted Data
     * @param upperLipResult - String - upperLip predicted Data
     * @param lowerLipResult - String - lowerLip predicted Data
     */
    public FaceResult( String patientName, int age, String gender, String imageUrl, String leftEyebrowResult, String rightEyebrowResult, String leftEyeResult, String rightEyeResult, String upperLipResult, String lowerLipResult) {
        this.uploadTime = System.currentTimeMillis();
        this.uId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        this.resultId = this.uId + this.uploadTime;
        this.patientName = patientName;
        this.age = age;
        this.gender = gender;
        this.imageUrl = imageUrl;
        this.leftEyebrowResult = leftEyebrowResult;
        this.rightEyebrowResult = rightEyebrowResult;
        this.leftEyeResult = leftEyeResult;
        this.rightEyeResult = rightEyeResult;
        this.upperLipResult = upperLipResult;
        this.lowerLipResult = lowerLipResult;
    }

    public String getResultId() {
        return resultId;
    }

    public void setResultId(String resultId) {
        this.resultId = resultId;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLeftEyebrowResult() {
        return leftEyebrowResult;
    }

    public void setLeftEyebrowResult(String leftEyebrowResult) {
        this.leftEyebrowResult = leftEyebrowResult;
    }

    public String getRightEyebrowResult() {
        return rightEyebrowResult;
    }

    public void setRightEyebrowResult(String rightEyebrowResult) {
        this.rightEyebrowResult = rightEyebrowResult;
    }

    public String getLeftEyeResult() {
        return leftEyeResult;
    }

    public void setLeftEyeResult(String leftEyeResult) {
        this.leftEyeResult = leftEyeResult;
    }

    public String getRightEyeResult() {
        return rightEyeResult;
    }

    public void setRightEyeResult(String rightEyeResult) {
        this.rightEyeResult = rightEyeResult;
    }

    public String getUpperLipResult() {
        return upperLipResult;
    }

    public void setUpperLipResult(String upperLipResult) {
        this.upperLipResult = upperLipResult;
    }

    public String getLowerLipResult() {
        return lowerLipResult;
    }

    public void setLowerLipResult(String lowerLipResult) {
        this.lowerLipResult = lowerLipResult;
    }

    public long getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(long uploadTime) {
        this.uploadTime = uploadTime;
    }

    public void updateLeftEyeResult(String result){
        if(this.leftEyeResult==null){
            this.leftEyeResult="";
        }
        this.leftEyeResult+=result;
    }

    public void updateRightEyeResult(String result){
        if(this.rightEyeResult==null){
            this.rightEyeResult="";
        }
        this.rightEyeResult+=result;
    }
}
