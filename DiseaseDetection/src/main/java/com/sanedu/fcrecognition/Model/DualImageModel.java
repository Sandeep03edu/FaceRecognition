package com.sanedu.fcrecognition.Model;

public class DualImageModel {
    private String type;
    private String leftDisplayImgUri;
    private String rightDisplayImgUri;

    private String leftImgUri;
    private String rightImgUri;

    private String leftImgType;
    private String rightImgType;

    public DualImageModel(String type, String leftDisplayImgUri, String rightDisplayImgUri, String leftImgUri, String rightImgUri, String leftImgType, String rightImgType) {
        this.type = type;
        this.leftDisplayImgUri = leftDisplayImgUri;
        this.rightDisplayImgUri = rightDisplayImgUri;
        this.leftImgUri = leftImgUri;
        this.rightImgUri = rightImgUri;
        this.leftImgType = leftImgType;
        this.rightImgType = rightImgType;
    }

    public String getType() {
        return type;
    }

    public String getLeftDisplayImgUri() {
        return leftDisplayImgUri;
    }

    public String getRightDisplayImgUri() {
        return rightDisplayImgUri;
    }

    public String getLeftImgType() {
        return leftImgType;
    }

    public String getRightImgType() {
        return rightImgType;
    }

    public String getLeftImgUri() {
        return leftImgUri;
    }

    public String getRightImgUri() {
        return rightImgUri;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLeftDisplayImgUri(String leftDisplayImgUri) {
        this.leftDisplayImgUri = leftDisplayImgUri;
    }

    public void setRightDisplayImgUri(String rightDisplayImgUri) {
        this.rightDisplayImgUri = rightDisplayImgUri;
    }

    public void setLeftImgUri(String leftImgUri) {
        this.leftImgUri = leftImgUri;
    }

    public void setRightImgUri(String rightImgUri) {
        this.rightImgUri = rightImgUri;
    }

    public void setLeftImgType(String leftImgType) {
        this.leftImgType = leftImgType;
    }

    public void setRightImgType(String rightImgType) {
        this.rightImgType = rightImgType;
    }

    @Override
    public String toString() {
        return "DualImageModel{" +
                "type='" + type + '\'' +
                ", leftDisplayImgUri='" + leftDisplayImgUri + '\'' +
                ", rightDisplayImgUri='" + rightDisplayImgUri + '\'' +
                ", leftImgUri='" + leftImgUri + '\'' +
                ", rightImgUri='" + rightImgUri + '\'' +
                ", leftImgType='" + leftImgType + '\'' +
                ", rightImgType='" + rightImgType + '\'' +
                '}';
    }
}