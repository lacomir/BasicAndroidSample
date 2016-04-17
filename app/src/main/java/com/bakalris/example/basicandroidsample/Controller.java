package com.bakalris.example.basicandroidsample;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Size;

import java.util.ArrayList;

/**
 * Created by Mirko on 16.4.2016.
 */
public class Controller {

    public String file;
    public Picture picture;
    public ArrayList<ArrayList<Letter>> hlavolam;


    Controller(Mat mRgba, Mat mGray) {

        file = new String();
        picture = new Picture();
        picture.setImage(mRgba);
        picture.setGrayscale(mGray);

    }

    /*
    public void loadImage(String filename)
    {

        image = imread(filename + ".jpg", IMREAD_COLOR); // Read the file

        if( image.empty() ) // Check for invalid input
        {
            cout << "Could not open or find the image" << std::endl ;
            exit;
        }

        float ratioHeight = 760.0 / (float) image.rows;
        float ratioWidth = 1280.0 / (float) image.cols;

        cout << ratioHeight << endl;
        cout << ratioWidth << endl;

        cout << (image.cols * min(ratioHeight,ratioWidth)) << endl;
        cout << (image.rows * min(ratioHeight,ratioWidth)) << endl;

        if(min(ratioHeight,ratioWidth) < 1) {
            resize(image,image,Size((image.cols * min(ratioHeight,ratioWidth)),(image.rows * min(ratioHeight,ratioWidth))));
        }

        file = filename;

    }
    */

    public void preprocessImage() {

        Mat temp = new Mat(picture.grayscale.rows(),picture.grayscale.cols(),picture.grayscale.type());
        Imgproc.GaussianBlur(picture.grayscale, temp, new Size(5, 5), 0);

        Mat thresholded = new Mat(picture.grayscale.rows(),picture.grayscale.cols(),picture.grayscale.type());

        Imgproc.adaptiveThreshold(temp,thresholded, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 11, 2);

        picture.setThresholded(thresholded);

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

        picture.removeIntersectionsOutOfSudokuRect();

        if(picture.poi != null)
            System.out.println(picture.poi.toList().toString());

        if(picture.poi.rows() != 100) { //throw exception
            System.out.println("Segmentation of possible Sudoku failed. Can not find all sudoku squares.");
            return;
        }
        else {
            //isSudoku = true;
            //segmentSudoku();
        }



        return;



    }

    /*public void segmentSudoku() {
        vector<vector<Point2f>> field;

        for(int i = 0; i < 10; i++)
        {
            vector<Point2f> vec(picture.poi.begin() + (i*10), picture.poi.begin() + (i+1)*10);
            field.push_back(vec);
        }

        vector<vector<Letter>> sudoku;

        for(int i = 0; i < 9; i++)
        {
            sudoku.push_back(vector<Letter>());
            for(int j = 0; j < 9; j++)
            {
                int shiftX = picture.grayscale.cols / 50;
                int shiftY = picture.grayscale.rows / 100;

                sudoku[i].push_back(Letter());
                sudoku[i][j].segment.push_back(Point2f(field[i][j].x + shiftX, field[i][j].y + shiftY)); // posunut hodnoty dovnutra stvorceka
                sudoku[i][j].segment.push_back(Point2f(field[i][j+1].x - shiftX, field[i][j+1].y + shiftY));
                sudoku[i][j].segment.push_back(Point2f(field[i+1][j].x + shiftX, field[i+1][j].y - shiftY));
                sudoku[i][j].segment.push_back(Point2f(field[i+1][j+1].x - shiftX, field[i+1][j+1].y - shiftY));

                sudoku[i][j].rect = boundingRect( Mat(sudoku[i][j].segment) );
            }
        }

        #ifdef DEBUG

        RNG rng(12345);

        for(int i = 0; i < 9; i++)
        {
            for(int j = 0; j < 9; j++)
            {
                Scalar color = Scalar( rng.uniform(0, 255), rng.uniform(0,255), rng.uniform(0,255) );
                rectangle( picture.grayscale, sudoku[i][j].rect.tl(), sudoku[i][j].rect.br(), color, 1, 8, 0 );

            }
        }

        tosave.push_back(new ToSave(picture.grayscale, "Bounding Rects"));


        #endif

            sudoku = findNumbers(sudoku, picture.thresholded);

        hlavolam = sudoku;

    }
*/
    public Mat drawMergedLines(Mat mRgba) {

        for(int i = 0; i < picture.finalHorizontal.size(); i++) {

            Imgproc.line(mRgba, picture.finalHorizontal.get(i).getStart(), picture.finalHorizontal.get(i).getEnd(), new Scalar(255, 0, 0), 3);

        }

        for(int i = 0; i < picture.finalVertical.size(); i++) {

            Imgproc.line(mRgba, picture.finalVertical.get(i).getStart(), picture.finalVertical.get(i).getEnd(), new Scalar(255, 0, 0), 3);

        }

        return mRgba;

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


        Mat resizeimage = new Mat(width,height,rgba.type());
        Size sz = new Size(width,height);
        Imgproc.resize( rgba, resizeimage, sz );


        return resizeimage;

    }


}
