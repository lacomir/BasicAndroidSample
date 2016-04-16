package com.bakalris.example.basicandroidsample;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;

import java.util.ArrayList;

/**
 * Created by Mirko on 16.4.2016.
 */
public class Letter {

    MatOfPoint segment;
    Rect rect;
    Rect boundRect;
    Mat mask;
    MatOfPoint points;
    ArrayList<Double> characteristics;
    Boolean hasChar;
    String character;

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

    public MatOfPoint getPoints() {
        return points;
    }

    public void setPoints(MatOfPoint points) {
        this.points = points;
    }

    public ArrayList<Double> getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(ArrayList<Double> characteristics) {
        this.characteristics = characteristics;
    }

    public Boolean getHasChar() {
        return hasChar;
    }

    public void setHasChar(Boolean hasChar) {
        this.hasChar = hasChar;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }



}
