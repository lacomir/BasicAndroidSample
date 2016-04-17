package com.bakalris.example.basicandroidsample;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Mirko on 16.4.2016.
 */
public class Picture {

    public Mat image;
    public Mat grayscale;
    public Mat thresholded;
    public ArrayList<MergedLine> finalHorizontal;
    public ArrayList<MergedLine> finalVertical;
    public MatOfPoint poi;
    public ArrayList<Point> intersectionQuad;
    public ArrayList<Point> destEdges;
    public int transformedWidth;
    public int transformedHeight;
    public Mat perspectiveMatrix;

    public Picture() {
        finalHorizontal = new ArrayList<>();
        finalVertical = new ArrayList<>();
        destEdges = new ArrayList<>();
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

    public MatOfPoint getPoi() {
        return poi;
    }

    public void setPoi(MatOfPoint poi) {
        this.poi = poi;
    }

    public ArrayList<Point> getDestEdges() {
        return destEdges;
    }

    public void setDestEdges(ArrayList<Point> destEdges) {
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

    public void findPointsOfIntersection() {

        List<Point> points = new ArrayList<Point>();

        for(int i = 0;i<finalHorizontal.size();i++) {
            for(int j = 0; j<finalVertical.size();j++) {

                Point[] ab = new Point[2];
                ab[0] = finalHorizontal.get(i).getStart();
                ab[1] = finalHorizontal.get(i).getEnd();

                Point[] cd = new Point[2];
                cd[0] = finalVertical.get(j).getStart();
                cd[1] = finalVertical.get(j).getEnd();

                Point r = CustomMathOperations.intersection(ab, cd, grayscale.cols(), grayscale.rows());

                if(r.x != -1 || r.y != -1) {

                    points.add(r);

                    Intersection i1 = new Intersection();
                    i1.setId(j);
                    i1.setPoi(r);
                    finalHorizontal.get(i).getIntersection().add(i1);

                    Intersection i2 = new Intersection();
                    i2.setId(i);
                    i2.setPoi(r);
                    finalVertical.get(j).getIntersection().add(i2);

                }
            }
        }

        poi.fromList(points);

        return;

    }


    public void findIntersectionQuad() {

        int padLeft = -1;
        int padRight = -1;
        int padTop = -1;
        int padBottom = -1;

        int pom1 = 0;
        int pom2 = 0;
        for(int i = 0; i < finalVertical.size(); i++) {

            if(pom1 + pom2 > finalVertical.size())
                return;

            if(padLeft == -1 && finalVertical.get(i).intersection.size() > 0)
                padLeft = i;

            if(padRight == -1 && finalVertical.get(finalVertical.size() - i - 1).intersection.size() > 0)
                padRight = i;

            if(padLeft != -1 && padRight != -1)
                break;

            if(padLeft != -1)
                pom1++;

            if(padRight != -1)
                pom2++;

        }

        pom1 = 0;
        pom2 = 0;
        for(int i = 0; i < finalHorizontal.size(); i++) {

            if(pom1 + pom2 > finalHorizontal.size())
                return;

            if(padTop == -1 && finalHorizontal.get(i).intersection.size() > 0)
                padTop = i;

            if(padBottom == -1 && finalHorizontal.get(finalHorizontal.size() - i - 1).intersection.size() > 0)
                padBottom = i;

            if(padTop != -1 && padBottom != -1)
                break;

            if(padTop != -1)
                pom1++;

            if(padBottom != -1)
                pom2++;

        }

        int endOfCycle = Math.min((finalHorizontal.size()/2),(finalVertical.size()/2));

        for(int i=0; i < endOfCycle;i++) {

            intersectionQuad = findIntersectionQuadVar(i,0+padTop,0+padLeft,0+padRight,0+padBottom); //tie inty dat do vector a podla cyklu indexu ich posielat
            if(intersectionQuad != null)
                return;

            intersectionQuad = findIntersectionQuadVar(i,1+padTop,0+padLeft,0+padRight,0+padBottom);
            if(intersectionQuad != null)
                return;
            intersectionQuad = findIntersectionQuadVar(i,0+padTop,1+padLeft,0+padRight,0+padBottom);
            if(intersectionQuad != null)
                return;
            intersectionQuad = findIntersectionQuadVar(i,0+padTop,0+padLeft,1+padRight,0+padBottom);
            if(intersectionQuad != null)
                return;
            intersectionQuad = findIntersectionQuadVar(i,0+padTop,0+padLeft,0+padRight,1+padBottom);
            if(intersectionQuad != null)
                return;
            intersectionQuad = findIntersectionQuadVar(i,0+padTop,0+padLeft,1+padRight,1+padBottom);
            if(intersectionQuad != null)
                return;
            intersectionQuad = findIntersectionQuadVar(i,0+padTop,1+padLeft,1+padRight,0+padBottom);
            if(intersectionQuad != null)
                return;
            intersectionQuad = findIntersectionQuadVar(i,1+padTop,1+padLeft,0+padRight,0+padBottom);
            if(intersectionQuad != null)
                return;
            intersectionQuad = findIntersectionQuadVar(i,1+padTop,1+padLeft,1+padRight,0+padBottom);
            if(intersectionQuad != null)
                return;
            intersectionQuad = findIntersectionQuadVar(i,0+padTop,1+padLeft,1+padRight,1+padBottom);
            if(intersectionQuad != null)
                return;



        }

        return;


    }

    private ArrayList<Point> findIntersectionQuadVar(int i, int a, int b, int c, int d) {
        Point got1 = new Point(-1,-1);
        Point got2 = new Point(-1,-1);

        for(int j=0;j< finalHorizontal.get(i + a).intersection.size();j++) {

            if(finalHorizontal.get(i + a).intersection.get(j).id == i+b)
                got1 = finalHorizontal.get(i + a).intersection.get(j).poi;
            if(finalHorizontal.get(i + a).intersection.get(j).id == (finalVertical.size()-1-i-c))
                got2 = finalHorizontal.get(i + a).intersection.get(j).poi;

            if(got1.x != -1 && got2.x != -1)
                break;

        }

        if(got1.x == -1 || got2.x == -1)
            return null;

        Point got3 = new Point(-1,-1);
        Point got4 = new Point(-1,-1);

        int horizontalMax = finalHorizontal.size()-1-i-d;

        for(int j=0;j< finalHorizontal.get(horizontalMax).intersection.size();j++) {

            if(finalHorizontal.get(horizontalMax).intersection.get(j).id == i+b)
                got3 = finalHorizontal.get(horizontalMax).intersection.get(j).poi;
            if(finalHorizontal.get(horizontalMax).intersection.get(j).id == (finalVertical.size()-1-i-c))
                got4 = finalHorizontal.get(horizontalMax).intersection.get(j).poi;

            if(got3.x != -1 && got4.x != -1) {

                ArrayList<Point> result = new ArrayList<>();
                result.add(got1);
                result.add(got2);
                result.add(got3);
                result.add(got4);

                return result;
            }
        }

        return null;
    }


    public void createPerspectiveMatrix() {

        Point leftmin = intersectionQuad.get(0);
        Point rightmin = intersectionQuad.get(1);
        Point leftmax = intersectionQuad.get(2);
        Point rightmax = intersectionQuad.get(3);


        double longerX = Math.max(rightmax.x,rightmin.x) - Math.min(leftmax.x,leftmin.x);
        double shorterX = Math.min(rightmax.x,rightmin.x) - Math.max(leftmax.x,leftmin.x);
        double deltaX = longerX - shorterX;

        double longerY = Math.max(leftmax.y,rightmax.y) - Math.min(leftmin.y,rightmin.y);
        double shorterY = Math.min(leftmax.y,rightmax.y) - Math.max(leftmin.y,rightmin.y);
        double deltaY = longerY - shorterY;

        double deltaLeftPts = 1.5 * Math.abs(leftmin.x-leftmax.x); //1.5 nahradit premennou zavisejucou od vzdialenosti laveho horneho rohu a horneho okraja obrazku?

        Point destLeftMin = new Point(Math.min(leftmin.x,leftmax.x) + deltaLeftPts, Math.min(leftmin.y, rightmin.y) + deltaY);
        Point destRightMin = new Point(Math.max(rightmin.x, rightmax.x) + deltaLeftPts,Math.min(leftmin.y, rightmin.y) + deltaY);
        Point destLeftMax = new Point(Math.min(leftmin.x, leftmax.x) + deltaLeftPts,Math.max(leftmax.y, rightmax.y) + deltaY + deltaX);
        Point destRightMax = new Point(Math.max(rightmin.x, rightmax.x) + deltaLeftPts,Math.max(leftmax.y, rightmax.y) + deltaY + deltaX);


        Mat destIntersections = new Mat(4,1, CvType.CV_32FC2);
        destIntersections.put(0,0,destLeftMin.x,destLeftMin.y,destRightMin.x,destRightMin.y, destLeftMax.x,destLeftMax.y,destRightMax.x,destRightMax.y);

        /*
        destIntersections.push_back(destLeftMin);
        destIntersections.push_back(destRightMin);
        destIntersections.push_back(destLeftMax);
        destIntersections.push_back(destRightMax);
        */

        Mat intersections = new Mat(4,1, CvType.CV_32FC2);
        intersections.put(0,0,leftmin.x,leftmin.y,rightmin.x,rightmin.y, leftmax.x,leftmax.y,rightmax.x,rightmax.y);

        //Mat perspectiveMatrix = Mat::zeros(picture.grayscale.rows, picture.grayscale.cols, picture.grayscale.type());
        perspectiveMatrix = Imgproc.getPerspectiveTransform(intersections, destIntersections);


        transformedHeight = (grayscale.rows() + (int) deltaX + ((int) deltaY << 1));
        transformedWidth = (grayscale.cols() + (int) deltaX +  + (int) deltaLeftPts);

        destEdges.add(new Point(destLeftMin.x,destLeftMin.y));
        destEdges.add(new Point(destRightMin.x,destRightMin.y));
        destEdges.add(new Point(destLeftMax.x,destLeftMax.y));
        destEdges.add(new Point(destRightMax.x,destRightMax.y));


        performPerspectiveTransformation();

        return;

    }


    public void performPerspectiveTransformation() //perspektivna transformacia zatial iba pre grayscale image, thresholded image, points of intersections ciar a pre koncove body ciar
    {
        Mat transformedGray = Mat.zeros(transformedHeight,transformedWidth,grayscale.type());
        Mat transformedThresh = Mat.zeros(transformedHeight,transformedWidth,grayscale.type());

        Imgproc.warpPerspective(grayscale, transformedGray, perspectiveMatrix, transformedGray.size());
        Imgproc.warpPerspective(thresholded,transformedThresh,perspectiveMatrix,transformedThresh.size());

        grayscale =transformedGray;
        thresholded=transformedThresh;

        MatOfPoint transformedPoi = new MatOfPoint(poi);

        Imgproc.warpPerspective(poi, transformedPoi, perspectiveMatrix,poi.size());

        poi = transformedPoi;

        for(int i = 0; i < finalHorizontal.size(); i++)
        {

            Point[] tempLineArr = new Point[2];
            tempLineArr[0] = finalHorizontal.get(i).getStart();
            tempLineArr[1] = finalHorizontal.get(i).getEnd();
            MatOfPoint tempLineMat = new MatOfPoint();
            tempLineMat.fromArray(tempLineArr);

            MatOfPoint line = new MatOfPoint(tempLineMat);

            Imgproc.warpPerspective(tempLineMat, line, perspectiveMatrix,tempLineMat.size());

            Point[] lineArr = line.toArray();

            finalHorizontal.get(i).setStart(lineArr[0]);
            finalHorizontal.get(i).setEnd(lineArr[1]);

        }

        for(int i = 0; i < finalVertical.size(); i++)
        {

            Point[] tempLineArr = new Point[2];
            tempLineArr[0] = finalVertical.get(i).getStart();
            tempLineArr[1] = finalVertical.get(i).getEnd();
            MatOfPoint tempLineMat = new MatOfPoint();
            tempLineMat.fromArray(tempLineArr);

            MatOfPoint line = new MatOfPoint(tempLineMat);

            Imgproc.warpPerspective(tempLineMat, line, perspectiveMatrix,tempLineMat.size());

            Point[] lineArr = line.toArray();

            finalVertical.get(i).setStart(lineArr[0]);
            finalVertical.get(i).setEnd(lineArr[1]);
        }


    }


}
