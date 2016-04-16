package com.bakalris.example.basicandroidsample;

import org.opencv.core.Mat;
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

        picture.mergeLines(lines);

    }


}
