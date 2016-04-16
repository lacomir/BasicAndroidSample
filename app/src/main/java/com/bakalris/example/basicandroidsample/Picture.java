package com.bakalris.example.basicandroidsample;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;

import java.util.ArrayList;

/**
 * Created by Mirko on 16.4.2016.
 */
public class Picture {

    public Mat image;
    public Mat grayscale;
    public Mat thresholded;
    ArrayList<MergedLine> finalHorizontal;
    ArrayList<MergedLine> finalVertical;
    MatOfPoint2f poi;
    MatOfPoint destEdges;
    Mat perspectiveMatrix;

    public Mat getImage() {
        return image;
    }

    public void setImage(Mat image) {
        image.copyTo(this.image);
    }

    public Mat getGrayscale() {
        return grayscale;
    }

    public void setGrayscale(Mat grayscale) {
        grayscale.copyTo(this.grayscale);
    }

    public Mat getThresholded() {
        return thresholded;
    }

    public void setThresholded(Mat thresholded) {
        thresholded.copyTo(this.thresholded);
    }

    public ArrayList<MergedLine> getFinalHorizontal() {
        return finalHorizontal;
    }

    public void setFinalHorizontal(ArrayList<MergedLine> finalHorizontal) {
        this.finalHorizontal = finalHorizontal;
    }

    public ArrayList<MergedLine> getFinalVertical() {
        return finalVertical;
    }

    public void setFinalVertical(ArrayList<MergedLine> finalVertical) {
        this.finalVertical = finalVertical;
    }

    public MatOfPoint2f getPoi() {
        return poi;
    }

    public void setPoi(MatOfPoint2f poi) {
        this.poi = poi;
    }

    public MatOfPoint getDestEdges() {
        return destEdges;
    }

    public void setDestEdges(MatOfPoint destEdges) {
        this.destEdges = destEdges;
    }

    public Mat getPerspectiveMatrix() {
        return perspectiveMatrix;
    }

    public void setPerspectiveMatrix(Mat perspectiveMatrix) {
        perspectiveMatrix.copyTo(this.perspectiveMatrix);
    }
}
