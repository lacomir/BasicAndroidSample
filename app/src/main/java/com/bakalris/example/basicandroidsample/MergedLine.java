package com.bakalris.example.basicandroidsample;

import org.opencv.core.Point;

import java.util.ArrayList;

/**
 * Created by Mirko on 16.4.2016.
 */
public class MergedLine {

    public Point[] line = new Point[2];
    public ArrayList<Intersection> intersection;

    public Point[] getLine() {
        return line;
    }

    public void setLine(Point[] line) {
        this.line = line;
    }

    public ArrayList<Intersection> getIntersection() {
        return intersection;
    }

    public void setIntersection(ArrayList<Intersection> intersection) {
        this.intersection = intersection;
    }
}
