package com.bakalris.example.basicandroidsample;

import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;

/**
 * Created by Mirko on 16.4.2016.
 */
public class Intersection {

    public int id;
    public Point poi;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Point getPoi() {
        return poi;
    }

    public void setPoi(Point poi) {
        this.poi = poi;
    }
}
