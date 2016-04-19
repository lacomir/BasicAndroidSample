package com.bakalris.example.basicandroidsample;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mirko on 17.4.2016.
 */
public class Sudoku extends Hlavolam {

    Letter[][] numbers;
    public static final int SUDOKU_LENGTH = 9;
    public static final int CV_FILLED = -1;

    public Sudoku() {
        numbers = new Letter[SUDOKU_LENGTH][SUDOKU_LENGTH];

        for(int i = 0; i < SUDOKU_LENGTH; i++) {
            for(int j = 0; j < SUDOKU_LENGTH; j++) {
                numbers[i][j] = new Letter();
            }
        }

    }

    public Letter[][] getNumbers() {
        return numbers;
    }

    public void setNumbers(Letter[][] numbers) {
        this.numbers = numbers;
    }


    public void findNumbers(Picture picture) {
        int index = -1;

        for(int i = 0; i < SUDOKU_LENGTH; i++)
        {
            for(int j = 0; j < SUDOKU_LENGTH; j++ )
            {
                index++;
                numbers[i][j].hasChar = false;

                Mat roi = new Mat(picture.thresholded, numbers[i][j].rect);

                List<MatOfPoint> contours = new ArrayList<>();
                Mat hierarchy = new Mat();
                Imgproc.findContours(roi, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
                hierarchy.release();

                if(contours.size() > 0) {


                    int biggestContour = 0;
                    double biggestContourArea = Imgproc.contourArea(contours.get(biggestContour));

                    for(int k = 1; k < contours.size(); k++) {

                        double contourArea = Imgproc.contourArea(contours.get(k));

                        if(contourArea > biggestContourArea) {
                            biggestContour = k;
                            biggestContourArea = contourArea;
                        }

                    }


                    if(biggestContourArea < (roi.rows()*roi.cols()*0.05)) {
                        continue; // too small contour, shall not be number
                    }


                    numbers[i][j].boundRect = Imgproc.boundingRect(contours.get(biggestContour));

                    Mat mask = Mat.zeros(roi.rows(), roi.cols(),roi.type());
                    numbers[i][j].mask = Mat.zeros(numbers[i][j].boundRect.height, numbers[i][j].boundRect.width, roi.type());

                    Scalar color = new Scalar( 255, 255, 255 );
                    Imgproc.drawContours(mask, contours, biggestContour, color, CV_FILLED);

                    //ulozit si konturu?


                    contours.remove(biggestContour);

                    for(int k = 0; k < contours.size(); k++) {
                        color = new Scalar( 0, 0, 0 );
                        Imgproc.drawContours(mask, contours, k, color, CV_FILLED);
                    }

                    numbers[i][j].mask = new Mat(mask, numbers[i][j].boundRect);

                    contours = new ArrayList<>();
                    hierarchy = new Mat();
                    Imgproc.findContours(numbers[i][j].mask, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
                    hierarchy.release();

                    if(contours.size() < 1) {
                        System.out.println("No cont inside! n." + (i*10+j));
                        continue;
                    }

                    biggestContour = 0;
                    biggestContourArea = Imgproc.contourArea(contours.get(biggestContour));

                    for(int k = 1; k < contours.size(); k++) {

                        double contourArea = Imgproc.contourArea(contours.get(k));

                        if(contourArea > biggestContourArea) {
                            biggestContour = k;
                            biggestContourArea = contourArea;
                        }

                    }


                    for( int k = 0; k < numbers[i][j].mask.cols(); k++ ) {
                        for( int l = 0; l < numbers[i][j].mask.rows(); l++ ) {

                            double inOutter;
                            double outInner = 0;

                            inOutter = Imgproc.pointPolygonTest(new MatOfPoint2f( contours.get(biggestContour).toArray() ), new Point(k, l), false);

                            for(int x = 0; x < contours.size() ; x++ ) {

                                if(x == biggestContour)
                                    continue;

                                outInner += Imgproc.pointPolygonTest(new MatOfPoint2f( contours.get(x).toArray() ), new Point(k, l), false);
                            }

                            float g = contours.size()-1;
                            g*= -1;

                            if(inOutter > -1 && outInner == g) {
                                numbers[i][j].points.add(new Point(k, l));
                            }
                        }
                    }

                    numbers[i][j].hasChar = true;
                    System.out.println("HASCHAR: [" + (i+1) + "][" + (j+1) + "]");
                }

            }
        }

        return;
    }

    @Override
    public Letter[][] getLetters() {
        return getNumbers();
    }
}
