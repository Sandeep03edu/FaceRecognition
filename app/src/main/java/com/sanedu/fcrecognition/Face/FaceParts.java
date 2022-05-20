package com.sanedu.fcrecognition.Face;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;

import com.sanedu.fcrecognition.Face.FaceLandmarks;
import com.sanedu.fcrecognition.Model.Edges;
import com.sanedu.fcrecognition.Model.Face68Coordinates;
import com.sanedu.fcrecognition.Utils.Utils;
import com.tzutalin.dlib.VisionDetRet;

import java.util.ArrayList;
import java.util.List;

public class FaceParts {
    private static final String TAG = "FaceLandmarksTag";
    private ArrayList<Point> faceLandmarks = null;
    private Bitmap imageBitmap;

    public FaceParts(String path, VisionDetRet face, Bitmap bitmap) {
        this.faceLandmarks = FaceLandmarks.getLandmarks(path, face);
        this.imageBitmap = bitmap;
    }

    public ArrayList<Point> getFaceLandmarks() {
        return faceLandmarks;
    }

    public Bitmap getDisplayLeftEye() {
        return getRectBitmap(Face68Coordinates.LEFT_EYE);
    }
    public Bitmap getDisplayRightEye(){
        return getRectBitmap(Face68Coordinates.RIGHT_EYE);
    }
    public Bitmap getDisplayLeftEyebrow(){
        return getRectBitmap(Face68Coordinates.LEFT_EYEBROW);
    }
    public Bitmap getDisplayRightEyebrow(){
        return getRectBitmap(Face68Coordinates.RIGHT_EYEBROW);
    }
    public Bitmap getDisplayUpperLip(){
        return getRectBitmap(Face68Coordinates.UPPER_LIP);
    }
    public Bitmap getDisplayLowerLip(){
        return getRectBitmap(Face68Coordinates.LOWER_LIP);
    }
    public Bitmap getDisplayNose(){return getRectBitmap(Face68Coordinates.NOSE);}


    public Bitmap getLeftEye() {
        return getListBitmap(Face68Coordinates.LEFT_EYE);
    }

    public Bitmap getRightEye(){
        return getListBitmap(Face68Coordinates.RIGHT_EYE);
    }

    public Bitmap getLeftEyebrow(){
        return getListBitmap(Face68Coordinates.LEFT_EYEBROW);
    }

    public Bitmap getRightEyebrow(){
        return getListBitmap(Face68Coordinates.RIGHT_EYEBROW);
    }

    public Bitmap getUpperLip(){
        return getListBitmap(Face68Coordinates.UPPER_LIP);
    }

    public Bitmap getLowerLip(){
        return getListBitmap(Face68Coordinates.LOWER_LIP);
    }

    public Bitmap getNose(){return getListBitmap(Face68Coordinates.NOSE);}


    private Bitmap getRectBitmap(List<Integer> list){
        Edges corners = Utils.getEdge(list, faceLandmarks);
        return Bitmap.createBitmap(imageBitmap, corners.getLeftCoord(), corners.getBottomCoord(), corners.getWidth(), corners.getHeight());
    }

    private Bitmap getListBitmap(List<Integer> list){
        Log.d(TAG, "getListBitmap: List: " + list.toString());
//        Edges corners = Utils.getEdge(list, faceLandmarks);
//        Log.d(TAG, "getLeftEye: Corner: " + corners.toString());
        return Utils.getEdgedBitmap(imageBitmap, list, faceLandmarks);
    }
}