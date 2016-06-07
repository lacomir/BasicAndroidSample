package com.bakalris.example.basicandroidsample;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.ml.KNearest;

import java.util.ArrayList;

/**
 * @author Miroslav Laco
 * All rights reserved.
 *
 * Class for storing single found letter/character on image with belonging area and characteristics of letter.
 *
 */

public class Letter {

    MatOfPoint segment;
    Rect rect;
    Rect boundRect;
    Mat mask;
    ArrayList<Point> points;
    ArrayList<Double> characteristics;
    boolean hasChar;
    String character;

    public Letter() {

        points = new ArrayList<>();
        characteristics = new ArrayList<>();
        segment = new MatOfPoint();
        rect = new Rect();
        boundRect = new Rect();
        mask = new Mat();
        hasChar = false;

    }

    public MatOfPoint getSegment() {
        return segment;
    }

    public void setSegment(MatOfPoint segment) {
        this.segment = segment;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public Rect getBoundRect() {
        return boundRect;
    }

    public void setBoundRect(Rect boundRect) {
        this.boundRect = boundRect;
    }

    public Mat getMask() {
        return mask;
    }

    public void setMask(Mat mask) {
        this.mask = mask;
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Point> points) {
        this.points = points;
    }

    public ArrayList<Double> getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(ArrayList<Double> characteristics) {
        this.characteristics = characteristics;
    }

    public boolean getHasChar() {
        return hasChar;
    }

    public void setHasChar(boolean hasChar) {
        this.hasChar = hasChar;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }


    public void computeCharacteristics() {

        if(!hasChar)
            return;

        characteristics = new ArrayList<>();

        characteristics.add(CustomMathOperations.boundingRectAspectRatio(mask));
        characteristics.add(CustomMathOperations.foregroundToBackgroundRatio(mask, points.size()));
        characteristics.addAll(CustomMathOperations.computeMoments(mask,points));

        System.out.println("DEBUGGING-characteristics- " + characteristics.toString());

        return;

    }

    public String recognizeChar(KNearest knn) {

        if(hasChar) {

            Mat trainData = new Mat(1, characteristics.size(), CvType.CV_32FC1);
            Mat trainClasses = new Mat(1, 1, CvType.CV_32FC1);

            for (int i = 0; i < characteristics.size(); i++) {
                trainData.put(0, i, (float) characteristics.get(i).doubleValue());
            }

            float predict = knn.findNearest(trainData, 3, trainClasses);

            character = Integer.toString((int) predict);

            return character;
        } else {
            return null;
        }

    }


}
