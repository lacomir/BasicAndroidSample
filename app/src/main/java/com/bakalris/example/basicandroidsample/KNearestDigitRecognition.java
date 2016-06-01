package com.bakalris.example.basicandroidsample;

import org.opencv.ml.KNearest;

/**
 * @author Miroslav Laco
 * All rights reserved.
 *
 * Singleton class for storing K-nearest neighbors trained classifier.
 * Uses K-nearest implementation from OpenCV.
 *
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
