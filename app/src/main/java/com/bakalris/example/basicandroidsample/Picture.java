package com.bakalris.example.basicandroidsample;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Mirko on 16.4.2016.
 */
public class Picture {

    public Mat image;
    public Mat grayscale;
    public Mat thresholded;
    public ArrayList<MergedLine> finalHorizontal;
    public ArrayList<MergedLine> finalVertical;
    public MatOfPoint2f poi;
    public MatOfPoint destEdges;
    public Mat perspectiveMatrix;

    public Picture() {
        finalHorizontal = new ArrayList<>();
        finalVertical = new ArrayList<>();
    }

    public Mat getImage() {
        return image;
    }

    public void setImage(Mat image) {
        this.image = new Mat(image.rows(),image.cols(),image.type());
        image.copyTo(this.image);
    }

    public Mat getGrayscale() {
        return grayscale;
    }

    public void setGrayscale(Mat grayscale) {
        this.grayscale = new Mat(grayscale.rows(),grayscale.cols(),grayscale.type());
        grayscale.copyTo(this.grayscale);
    }

    public Mat getThresholded() {
        return thresholded;
    }

    public void setThresholded(Mat thresholded) {
        this.thresholded = new Mat(thresholded.rows(),thresholded.cols(),thresholded.type());
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


    public void mergeLines(Mat lines) {

        System.out.println("DEBUGGING-lines- cols:" + Integer.toString(lines.cols()));
        System.out.println("DEBUGGING-lines- rows:" + Integer.toString(lines.rows()));

        ArrayList<Point[]> vectorOfLines = new ArrayList<>();

        for (int x = 0; x < lines.rows(); x++) {

            Point[] p = new Point[2];

            double[] vec = lines.get(x, 0);
            double x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];
            p[0] = new Point(x1, y1);
            p[1] = new Point(x2, y2);

            vectorOfLines.add(p);

        }

        LinePartitioner partitioner = new LinePartitioner();

        int[] labels = partitioner.partition(vectorOfLines);

        System.out.println("DEBUGGING-- pocet ciar:" + Integer.toString(vectorOfLines.size()));
        System.out.println("DEBUGGING-- velkost pola labels:" + Integer.toString(labels.length));
        System.out.println("DEBUGGING-- premenna getNclasses:" + Integer.toString(partitioner.getNclasses()));

        ArrayList<Point[]> mergedLines = new ArrayList<>(partitioner.getNclasses());

        System.out.println("DEBUGGING-- mergedLines size:" + Integer.toString(mergedLines.size()));

        for(int i = 0; i<partitioner.getNclasses(); i++) {
            Point[] p = new Point[2];
            p[0] = new Point(Double.MAX_VALUE, Double.MAX_VALUE);
            p[1] = new Point(-1, -1);
            mergedLines.add(p);
        }



        for(int i=0; i<vectorOfLines.size();i++) {

            double x1 = vectorOfLines.get(i)[0].x; // lines[i][0];
            double y1 = vectorOfLines.get(i)[0].y; //lines[i][1];
            double x2 = vectorOfLines.get(i)[1].x; //lines[i][2];
            double y2 = vectorOfLines.get(i)[1].y; //lines[i][3];


            // int *minX = &mergedLines[labels[i]][0]; === mergedLines.get(labels[i])[0].x
            // int *minY = &mergedLines[labels[i]][1]; === mergedLines.get(labels[i])[0].y
            // int *maxX = &mergedLines[labels[i]][2]; === mergedLines.get(labels[i])[1].x
            // int *maxY = &mergedLines[labels[i]][3]; === mergedLines.get(labels[i])[1].y

            /***********AK NEBOLA MERGE LINE ESTE INICIALIZOVANA**********/


            System.out.println("DEBUGGING-- mergedLines size2:" + Integer.toString(mergedLines.size()));

            if(mergedLines.get(labels[i])[0].x == Double.MAX_VALUE && mergedLines.get(labels[i])[0].y == Double.MAX_VALUE || mergedLines.get(labels[i])[1].x == -1 && mergedLines.get(labels[i])[1].y == -1) {

                if(mergedLines.get(labels[i])[0].x == Double.MAX_VALUE && mergedLines.get(labels[i])[0].y == Double.MAX_VALUE) {
                    if(Math.abs(x2-x1) > Math.abs(y2-y1)) {
                        if(x1<x2) {
                            mergedLines.get(labels[i])[0].x = x1;
                            mergedLines.get(labels[i])[0].y = y1;
                        } else {
                            mergedLines.get(labels[i])[0].x = x2;
                            mergedLines.get(labels[i])[0].y = y2;
                        }

                    } else {
                        if(y1<y2) {
                            mergedLines.get(labels[i])[0].x = x1;
                            mergedLines.get(labels[i])[0].y = y1;
                        } else {
                            mergedLines.get(labels[i])[0].x = x2;
                            mergedLines.get(labels[i])[0].y = y2;
                        }

                    }
                }

                if(mergedLines.get(labels[i])[1].x == -1 && mergedLines.get(labels[i])[1].y == -1) {
                    if(Math.abs(x2-x1) > Math.abs(y2-y1)) {
                        if(x1>x2) {
                            mergedLines.get(labels[i])[1].x = x1;
                            mergedLines.get(labels[i])[1].y = y1;
                        } else {
                            mergedLines.get(labels[i])[1].x = x2;
                            mergedLines.get(labels[i])[1].y = y2;
                        }
                    } else {
                        if(y1<y2) {
                            mergedLines.get(labels[i])[1].x = x1;
                            mergedLines.get(labels[i])[1].y = y1;
                        } else {
                            mergedLines.get(labels[i])[1].x = x2;
                            mergedLines.get(labels[i])[1].y = y2;
                        }
                    }
                }

                continue;
            }

            /*****************************/

            if(Math.abs(mergedLines.get(labels[i])[1].x - mergedLines.get(labels[i])[0].x) > Math.abs(mergedLines.get(labels[i])[1].y - mergedLines.get(labels[i])[0].y)) {
                if(x1 < x2) {
                    if(x1 < mergedLines.get(labels[i])[0].x) {
                        mergedLines.get(labels[i])[0].x = x1;
                        mergedLines.get(labels[i])[0].y = y1;
                    }

                    if(x2 > mergedLines.get(labels[i])[1].x) {
                        mergedLines.get(labels[i])[1].x = x2;
                        mergedLines.get(labels[i])[1].y = y2;
                    }
                } else {
                    if(x2 < mergedLines.get(labels[i])[0].x) {
                        mergedLines.get(labels[i])[0].x = x2;
                        mergedLines.get(labels[i])[0].y = y2;
                    }

                    if(x1 > mergedLines.get(labels[i])[1].x) {
                        mergedLines.get(labels[i])[1].x = x1;
                        mergedLines.get(labels[i])[1].y = y1;
                    }
                }
            } else {
                if(y1 < y2) {
                    if(y1 < mergedLines.get(labels[i])[0].y) {
                        mergedLines.get(labels[i])[0].x = x1;
                        mergedLines.get(labels[i])[0].y = y1;
                    }

                    if(y2 > mergedLines.get(labels[i])[1].y) {
                        mergedLines.get(labels[i])[1].x = x2;
                        mergedLines.get(labels[i])[1].y = y2;
                    }
                } else {
                    if(y2 < mergedLines.get(labels[i])[0].y) {
                        mergedLines.get(labels[i])[0].x = x2;
                        mergedLines.get(labels[i])[0].y = y2;
                    }

                    if(y1 > mergedLines.get(labels[i])[1].y) {
                        mergedLines.get(labels[i])[1].x = x1;
                        mergedLines.get(labels[i])[1].y = y1;
                    }
                }
            }

        }



        /****************************************************************/

        finalHorizontal = new ArrayList<>();
        finalVertical = new ArrayList<>();

        for(int i = 0; i < mergedLines.size(); i++) {
            if(Math.abs(mergedLines.get(i)[1].x - mergedLines.get(i)[0].x) > Math.abs(mergedLines.get(i)[1].y - mergedLines.get(i)[0].y)) {
                if(CustomMathOperations.lineLength(mergedLines.get(i)[0],mergedLines.get(i)[1]) >= thresholded.cols()*0.3) {
                    finalHorizontal.add(new MergedLine(mergedLines.get(i)[0], mergedLines.get(i)[1]));
                }
            } else {
                if(CustomMathOperations.lineLength(mergedLines.get(i)[0],mergedLines.get(i)[1]) >= thresholded.rows()*0.3) {
                    finalVertical.add(new MergedLine(mergedLines.get(i)[0], mergedLines.get(i)[1]));
                }
            }
        }

        if(finalHorizontal.size() == 0 || finalVertical.size() == 0) {

            System.out.println("DEBUGGING-- Ziadne zmergovane ciary!");
            return;
        }

        Collections.sort(finalHorizontal, new CustomHorizontalLineComparator());
        Collections.sort(finalVertical, new CustomVerticalLineComparator());


    }


}
