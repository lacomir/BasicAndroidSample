package com.bakalris.example.basicandroidsample;

import org.opencv.core.Point;

import java.util.ArrayList;

/**
 * Created by Mirko on 16.4.2016.
 */
public class MergedLine {

    public Point start;
    public Point end;
    public ArrayList<Intersection> intersection;

    public MergedLine() {
        intersection = new ArrayList<>();
    }

    public MergedLine(Point start, Point end) {
        this.start = start;
        this.end = end;

        intersection = new ArrayList<>();
    }

    public Point getStart() {
        return start;
    }

    public void setStart(Point start) {
        this.start = start;
    }

    public Point getEnd() {
        return end;
    }

    public void setEnd(Point end) {
        this.end = end;
    }

    public ArrayList<Intersection> getIntersection() {
        return intersection;
    }

    public void setIntersection(ArrayList<Intersection> intersection) {
        this.intersection = intersection;
    }

    public void addIntersection(Intersection i) {
        intersection.add(i);
    }

}
