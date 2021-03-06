package com.sanedu.fcrecognition.Model;

/**
 * @author Sandeep
 * DualImageModel class
 */
public class DualImageModel {
    private String type;
    private String leftDisplayImgUri;
    private String rightDisplayImgUri;

    private String leftImgUri;
    private String rightImgUri;

    private String leftImgType;
    private String rightImgType;

    /**
     * Constructor
     * @param type - String - DualImageModel type
     * @param leftDisplayImgUri - String - left rectangular image bitmap uri
     * @param rightDisplayImgUri - String - right rectangular image bitmap uri
     * @param leftImgUri - String - left detailed image bitmap uri
     * @param rightImgUri - String - right detailed image bitmap uri
     * @param leftImgType - String - left image type
     * @param rightImgType - String - right image type
     */
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

    /**
     *
     * @return String - DualImageModel object data to string
     */
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