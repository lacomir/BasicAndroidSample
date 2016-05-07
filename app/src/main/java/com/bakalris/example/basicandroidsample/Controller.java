package com.bakalris.example.basicandroidsample;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Size;
import org.opencv.ml.KNearest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mirko on 16.4.2016.
 */
public class Controller {

    public String file;
    public Picture picture;
    public Hlavolam hlavolam;
    public boolean isSudoku = true;

    //Sudoku solutions
    ArrayList<int[][]> sols = null;

    //WordPuzzle solution
    String puzzleSol = null;

    private static final String TAG = "Controller";

    Controller(Mat mRgba, Mat mGray) {

        file = new String();
        picture = new Picture();
        picture.setImage(mRgba);
        picture.setGrayscale(mGray);

    }


    public void processImage() {

        Log.e(TAG, "processImage: preprocessImage()");
        preprocessImage();
        Log.e(TAG, "processImage: segmentImage()");
        segmentImage();
        if(isSudoku) {
            Log.e(TAG, "processImage: computeCharacteristicVector()");
            computeCharacteristicVector();
            Log.e(TAG, "processImage: resolveProblem()");
            recognizeCharacters();
        }
        resolveProblem();

    }

    public void preprocessImage() {

        Mat temp = new Mat(picture.grayscale.rows(),picture.grayscale.cols(),picture.grayscale.type());
        Imgproc.GaussianBlur(picture.grayscale, temp, new Size(5, 5), 0);

        Mat thresholded = new Mat(picture.grayscale.rows(),picture.grayscale.cols(),picture.grayscale.type());
        Mat thresholdedInv = new Mat(picture.grayscale.rows(),picture.grayscale.cols(),picture.grayscale.type());

        Imgproc.adaptiveThreshold(temp,thresholded, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 11, 2);
        Imgproc.adaptiveThreshold(temp,thresholdedInv, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2);

        picture.setThresholded(thresholded);
        picture.setThresholdedInv(thresholdedInv);

        Mat lines = new Mat();
        int threshold = 50;
        double minLineSize = picture.getThresholded().cols()*0.1;
        int lineGap = 10;

        Imgproc.HoughLinesP(picture.getThresholded(), lines, 1, Math.PI / 180, threshold, minLineSize, lineGap);

        if(lines.cols() < 1)
            return; //TO-DO: exception here


        try {
            picture.mergeLines(lines);
        } catch (NullPointerException e) {
            System.err.println("DEBUGING-- mergeLines was null!! Another frame...");
        }

        try {
            picture.findPointsOfIntersection();
        } catch (NullPointerException e) {
            System.err.println("DEBUGING-- findPointsOfIntersection was null!! Another frame...");
        }

        try {
            picture.findIntersectionQuad();
        } catch (NullPointerException e) {
            System.err.println("DEBUGING-- findIntersectionQuad was null!! Another frame...");
        }

        try {
            picture.createPerspectiveMatrix();
        } catch (NullPointerException e) {
            System.err.println("DEBUGING-- createPerspectiveMatrix was null!! Another frame...");
        }



    }

    public void segmentImage()
    {

        if(picture.poi == null)
            return;

        try {
            picture.removeIntersectionsOutOfSudokuRect();
        } catch (Throwable e) {
            e.printStackTrace();
            Log.e(TAG, "segmentImage: removeIntersectionsOutOfSudokuRect() unsuccessful." );
            return;
        }


        //if(picture.poi != null)
            //System.out.println(picture.poi.toList().toString());

        if(picture.poi.rows() < 100) { //throw exception
            segmentOsemsmerovka();
            System.out.println("Segmentation of possible Sudoku failed. Can not find all sudoku squares.");
            return;
        }
        else if (picture.poi.rows() == 100){
            isSudoku = true;
            segmentSudoku();
        } else {
            return;
        }



        return;



    }

    private void segmentOsemsmerovka() {


        Bitmap bmpLeft = Bitmap.createBitmap(picture.grayscale.cols(), picture.grayscale.rows(), Bitmap.Config.ARGB_8888);
        Bitmap bmpRight = Bitmap.createBitmap(picture.thresholdedInv.cols(),picture.thresholdedInv.rows(), Bitmap.Config.ARGB_8888);

        Utils.matToBitmap(picture.grayscale, bmpLeft);
        Utils.matToBitmap(picture.thresholdedInv, bmpRight);

        String s1 = OCRUtils.getOCRText(bmpLeft);
        Log.e(TAG, "segmentOsemsmerovka: Rotated gray>" + s1 );

        String s2 = OCRUtils.getOCRText(bmpRight);
        Log.e(TAG, "segmentOsemsmerovka: Rotated thresholded>" + s2 );

    }

    public void segmentSudoku() {

        List<Point> pts = picture.poi.toList();


        Sudoku sudoku = new Sudoku();

        double sudokuRectWidth = picture.getDestEdges().get(1).x - picture.getDestEdges().get(0).x;
        double sudokuRectHeight = picture.getDestEdges().get(2).y - picture.getDestEdges().get(1).y;

        double shiftX = sudokuRectWidth / 80;
        double shiftY = sudokuRectHeight / 80;

        for(int i = 0; i < 9; i++)
        {
            for(int j = 0; j < 9; j++)
            {

                Point[] futureRect = new Point[4];
                futureRect[0] = new Point((pts.get(i*10+j).x + shiftX), (pts.get(i*10+j).y + shiftY));
                futureRect[1] = new Point((pts.get(i*10+(j+1)).x - shiftX), (pts.get(i*10+(j+1)).y + shiftY));
                futureRect[2] = new Point((pts.get((i+1)*10+j).x + shiftX), (pts.get((i+1)*10+j).y - shiftY));
                futureRect[3] = new Point((pts.get((i+1)*10+(j+1)).x - shiftX), (pts.get((i+1)*10+(j+1)).y - shiftY));


                MatOfPoint futureRectMat = new MatOfPoint();
                futureRectMat.fromArray(futureRect);

                sudoku.getNumbers()[i][j].setSegment(futureRectMat);

                sudoku.getNumbers()[i][j].setRect(Imgproc.boundingRect(sudoku.getNumbers()[i][j].segment));
            }
        }


        sudoku.findNumbers(picture);


        hlavolam = sudoku;

        hlavolam.rows = 9;
        hlavolam.cols = 9;

        return;


    }


    public void computeCharacteristicVector() {

        if(hlavolam == null)
            return;

        for(int i = 0; i < hlavolam.rows; i++) {
            for(int j = 0; j < hlavolam.cols; j++) {

                System.out.println("DEBUGING > [" + i +"][" + j + "] has char:" + hlavolam.getLetters()[i][j].getHasChar());
                        hlavolam.getLetters()[i][j].computeCharacteristics();

            }
        }

        return;
    }


    public void recognizeCharacters() {

        KNearest knn = KNearestDigitRecognition.getInstance().getKnn();

        if(hlavolam == null)
            return;

        for(int i = 0; i < hlavolam.rows; i++) {
            for(int j = 0; j < hlavolam.cols; j++) {

                hlavolam.getLetters()[i][j].recognizeChar(knn);

            }
        }

        return;

    }


    public void resolveProblem() {

        if (hlavolam == null)
            return;

        hlavolam.solveProblem();

        if(isSudoku) {

            Sudoku sudoku = (Sudoku) hlavolam;
            sols = sudoku.getSolutions();

        } else {

            Osemsmerovka osemsmerovka = (Osemsmerovka) hlavolam;
            puzzleSol = osemsmerovka.getSolution();

        }

    }



    public Mat drawMergedLinesAfterTransform(int width, int height) {

        Mat rgba = new Mat(picture.image.size(),picture.image.type());
        picture.image.copyTo(rgba);

        for(int i = 0; i < picture.finalHorizontal.size(); i++) {

            Imgproc.line(rgba, picture.finalHorizontal.get(i).getStart(), picture.finalHorizontal.get(i).getEnd(), new Scalar(255, 0, 0), 3);

        }

        for(int i = 0; i < picture.finalVertical.size(); i++) {

            Imgproc.line(rgba, picture.finalVertical.get(i).getStart(), picture.finalVertical.get(i).getEnd(), new Scalar(255, 0, 0), 3);

        }


        rgba = CustomMathOperations.rotateMat(rgba,CustomMathOperations.ROTATE_LEFT);
        Mat resizeimage = new Mat(width,height,rgba.type());
        Size sz = new Size(width,height);
        Imgproc.resize( rgba, resizeimage, sz );


        return resizeimage;

    }

    public Mat drawSudokuSquares(int width, int height) {


        if(hlavolam == null || isSudoku != true) {
            return drawMergedLinesAfterTransform( width, height);
        }

        Mat rgba = new Mat(picture.image.size(),picture.image.type());
        picture.image.copyTo(rgba);


        for(int i = 0; i < 9; i++)
        {
            for(int j = 0; j < 9; j++) {
                Scalar color;
                if(hlavolam.getLetters()[i][j].getHasChar())
                   color = new Scalar(255,0,0);
                else
                    color = new Scalar(0,255,0);

                //Imgproc.rectangle( rgba, hlavolam.getLetters()[i][j].rect.tl(), hlavolam.getLetters()[i][j].rect.br(), color, 2, 8, 0 );

                Log.e(TAG, "drawSudokuSquares: Size>" + sols.size() + " IsEmpty> " + sols.isEmpty() + " String> " +sols.toString());

                if(sols.isEmpty()) {
                    if(hlavolam.getLetters()[i][j].getHasChar()) {
                        Point segment[] = hlavolam.getLetters()[i][j].getSegment().toArray();
                        Imgproc.putText(rgba, hlavolam.getLetters()[i][j].character, segment[2], Core.FONT_HERSHEY_SIMPLEX, 1.5, new Scalar(255,0,0), 5);

                    } else {

                        Point segment[] = hlavolam.getLetters()[i][j].getSegment().toArray();
                        Imgproc.putText(rgba, "X", segment[2], Core.FONT_HERSHEY_SIMPLEX, 3, new Scalar(0,0,255), 5);
                    }


                } else {

                    int sol[][] = sols.get(0);

                    if(hlavolam.getLetters()[i][j].getHasChar()) {
                        Point segment[] = hlavolam.getLetters()[i][j].getSegment().toArray();
                        Imgproc.putText(rgba, hlavolam.getLetters()[i][j].character, segment[2], Core.FONT_HERSHEY_SIMPLEX, 1.5, new Scalar(255,0,0), 5);

                    } else {

                        Point segment[] = hlavolam.getLetters()[i][j].getSegment().toArray();
                        Imgproc.putText(rgba, Integer.toString(sol[i][j]), segment[2], Core.FONT_HERSHEY_SIMPLEX, 3, new Scalar(0,0,255), 5);
                    }

                }


            }
        }

        rgba = CustomMathOperations.rotateMat(rgba,CustomMathOperations.ROTATE_LEFT);
        Mat resizeimage = new Mat(width,height,rgba.type());
        Size sz = new Size(width,height);
        Imgproc.resize( rgba, resizeimage, sz );


        return resizeimage;

    }



}
