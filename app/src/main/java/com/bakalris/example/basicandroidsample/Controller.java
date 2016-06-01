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
 * @author Miroslav Laco
 * All rights reserved.
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

    /**
     *
     * Init controller with images. Otherwise is controller meaningless.
     *
     * @param mRgba color image of griddle
     * @param mGray gray image of griddle (must be the same as previous)
     *
     */
    Controller(Mat mRgba, Mat mGray) {

        file = new String();
        picture = new Picture();
        picture.setImage(mRgba);
        picture.setGrayscale(mGray);

    }

    /**
     * Starting automatized processing of given images in init state.
     */
    public void processImage() {

        preprocessImage();
        segmentImage();
        if(isSudoku) {
            computeCharacteristicVector();
            recognizeCharacters();
        }
        resolveProblem();

    }

    /**
     * Preprocessing consits of these states:
     *  1) blur grayscale image
     *  2) threshold image
     *  3) find lines
     *  4) merge lines
     *  5) find intersections of lines
     *  6) find griddle quad
     *  7) compute perspective matrix using found quad edges
     *  8) perform perspective transformation
     *
     */
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

    /**
     *
     * Classification of griddle and call for appropriate griddle segmentation.
     *
     */
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


    /**
     *
     * Segmenting word puzzle using Tesseract.
     *
     */
    private void segmentOsemsmerovka() {

        Bitmap bmpLeft = Bitmap.createBitmap(picture.grayscale.cols(), picture.grayscale.rows(), Bitmap.Config.ARGB_8888);

        Utils.matToBitmap(picture.grayscale, bmpLeft);

        String s1 = OCRUtils.getOCRText(bmpLeft);
        Log.e(TAG, "segmentOsemsmerovka: Rotated gray>" + s1 );


    }

    /**
     *
     * Segmenting Sudoku by lines and their intersections. Finding numbers in Sudoku squares.
     *
     */
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

    /**
     *
     * Compute custom characteristic vector for found characters.
     *
     */
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

    /**
     *
     * Recognition of characters based on KNN using characteristic vector computed by computeCharacteristicVector().
     * Not using computation of characteristic vector by computeCharacteristicVector() will result in recognition fail.
     * This is due to train data of classifier, which are made up of characteristics computed by computeCharacteristicVector().
     *
     */
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

    /**
     *
     * Solve griddle based on griddle type.
     *
     */
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


    /**
     *
     * This method is used if no Sudoku was detected on image.
     * Draws found merged lines on transformed image.
     *
     * @param width width of phone screen
     * @param height height of phone screen
     * @return matrix containing image to draw on screen with highlited merged lines found on image
     */
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

    /**
     *
     * This method is used to draw solved Sudoku on transformed image.
     *
     * @param width width of phone screen
     * @param height height of phone screen
     * @return matrix containing image to draw on screen with Solved Sudoku
     */
    public Mat drawSudokuSquares(int width, int height) {


        if(hlavolam == null || isSudoku != true) {
            return drawMergedLinesAfterTransform( width, height);
        }

        Mat rgba = new Mat(picture.image.size(),picture.image.type());
        picture.image.copyTo(rgba);


        for(int i = 0; i < 9; i++)
        {
            for(int j = 0; j < 9; j++) {

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
