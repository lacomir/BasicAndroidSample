package com.bakalris.example.basicandroidsample;

import org.opencv.ml.KNearest;

/**
 * Created by Mirko on 19.4.2016.
 */
public class KNearestDigitRecognition {

    private static KNearestDigitRecognition ourInstance = new KNearestDigitRecognition();


    public KNearest getKnn() {
        return knn;
    }

    public void setKnn(KNearest knn) {
        this.knn = knn;
    }

    private KNearest knn;

    public static KNearestDigitRecognition getInstance() {
        return ourInstance;
    }

    private KNearestDigitRecognition() {
    }

}
