package com.bakalris.example.basicandroidsample;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.KNearest;
import org.opencv.ml.Ml;
import org.opencv.ml.TrainData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Miroslav Laco
 * All rights reserved.
 */

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnTouchListener {

    private static final String TAG = "MainActivity";

    /* must functions for opencv
    starte
     */
    private View mCameraLayout;
    private View mProgressLayout;
    private CameraBridgeViewBase mOpenCvCameraView;

    private Boolean touched = false;
    private Boolean processing = false;
    private Boolean afterProcessing = false;

    private Mat mRgba;
    private Mat mIntermediateMat;
    private Mat mGray;
    private Mat mProcessedMat;
    private Size mFrameSize;


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(MainActivity.this);
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };


    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onDestroy() {
        // called after rotating the screen !
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    /* must functions for opencv
    ende
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // following two lines are must for each onCreate in activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.opencv_camera_view);
        mCameraLayout = findViewById(R.id.camera_layout);
        mProgressLayout = findViewById(R.id.camera_progress);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);


        try {

            OCRUtils.initAppDataPath(this);

        } catch (Throwable e) {
            e.printStackTrace();
            Log.e(TAG, "MainActivity:onCreate(): Tesseract not initialized!!");
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // save each local variable that has to be saved before pausing the activity in onPause
        // and will not be restored in onResume
        super.onSaveInstanceState(outState);
    }


    /**
     * Shows the progress UI and hides the login form.
     *
     * @param show true if progress bar is about to be shown, false if progress bar is about to be hidden
     *
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.

        /*
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);
        */

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                    int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

                    mCameraLayout.setVisibility(show ? View.GONE : View.VISIBLE);
                    mCameraLayout.animate().setDuration(shortAnimTime).alpha(
                            show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mCameraLayout.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });

                    mProgressLayout.setVisibility(show ? View.VISIBLE : View.GONE);
                    mProgressLayout.animate().setDuration(shortAnimTime).alpha(
                            show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mProgressLayout.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });

                    // TODO : enable the camera preview again
                } else {
                    // The ViewPropertyAnimator APIs are not available, so simply show
                    // and hide the relevant UI components.
                    mProgressLayout.setVisibility(show ? View.VISIBLE : View.GONE);
                    mCameraLayout.setVisibility(show ? View.GONE : View.VISIBLE);
                    // TODO : disable the camera preview to lighten the cpu usage
                }
            }
        }); // runOnUIthread
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        // -------------------
        //   IMPORTANT !!!
        // -------------------
        // Constructor of Mat() is little bit tricky
        // We can easily do :
        //      Mat newMat = Mat();
        // But this way we may run out of memory : OutOfMemoryError !!
        // >>> OpenCV Error: Insufficient memory (Failed to allocate #NUMBER_OF bytes) in void* cv::OutOfMemoryError(size_t), file /Volumes/Linux/builds/master_pack-android/opencv/modules/core/src/alloc.cpp, line 52
        // solution is to allocate values of Mat type with appropriate sizes and types before use
        // --------------------
        // We should allocate all globally used matrices in onCameraViewStarted.
        // This may prevent out of memory leakage.
        // (OpenCV for android has its bugs so don`t let your hopes too high)


        mRgba = new Mat(width, height, CvType.CV_8UC4);
        mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
        mGray = new Mat(width, height, CvType.CV_8UC1);

        mFrameSize = new Size(width, height);

        try {

            KNearestDigitRecognition.getInstance().setKnn(initDigitKnn());
            Log.e(TAG, "MainActivity:onCreate(): KNN inicialized successfuly.");

        } catch (Throwable e) {
            e.printStackTrace();
            Log.e(TAG, "MainActivity:onCreate(): KNN not initialized!!");
        }


    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
        mGray.release();
        mIntermediateMat.release();

    }


    @Override
    public Mat onCameraFrame(final CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        return processImage(inputFrame);

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (afterProcessing) {

            afterProcessing = false;

        } else if (!processing && !touched) {

            touched = true;

        }


        return false;
    }

    /**
     *
     * Method for processing captured image by device camera.
     * Handles UI like showing progress bar while processing,
     * showing processed image and showing camera capture.
     * Creates controller instance and calls processImgae() method of Controller to process given image.
     *
     * @param inputFrame captured image by device camera
     * @return matrix of image to be shown on phone screen
     */
    private Mat processImage(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        if (touched && !processing) {

            processing = true;
            showProgress(true);

            Mat matRgba = new Mat(inputFrame.rgba().rows(), inputFrame.rgba().cols(), inputFrame.rgba().type());
            inputFrame.rgba().copyTo(matRgba);
            Mat matGray = new Mat(inputFrame.gray().rows(), inputFrame.gray().cols(), inputFrame.gray().type());
            inputFrame.gray().copyTo(matGray);

            CustomMathOperations.rotateMat(matRgba, CustomMathOperations.ROTATE_RIGHT).copyTo(mRgba);
            CustomMathOperations.rotateMat(matGray, CustomMathOperations.ROTATE_RIGHT).copyTo(mGray);

            Controller controller = new Controller(mRgba, mGray);
            controller.processImage();

            processing = false;
            touched = false;
            afterProcessing = true;
            showProgress(false);

            mProcessedMat = new Mat(mRgba.height(), mRgba.width(), mRgba.type());
            mProcessedMat = controller.drawSudokuSquares(mRgba.height(), mRgba.width());
            return mProcessedMat;

        }

        if (afterProcessing)
            return mProcessedMat;
        else
            return inputFrame.rgba();


    }

    /**
     * Initialization of K-nearest classifier for number recognition.
     * Stores trained classifier in Singleton KNearestDigitRecognition class.
     * Uses digit-train-data.data located in Assets folder.
     * Data are made up of custom characteristic vectors of number images.
     *
     * @return trained K-nearest classifier for digit recogniton.
     * @throws IOException if can not find digit-train-data.data located in Assets folder
     */
    private KNearest initDigitKnn() throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("digit-train-data.data"), "UTF-8"));

        String line;

        ArrayList<String> character = new ArrayList<>();
        ArrayList<ArrayList<String>> portion = new ArrayList<>();

        int i = 0;
        while ((line = reader.readLine()) != null) {

            portion.add(new ArrayList<String>());

            String[] sep = line.split(",");
            character.add(sep[0]);

            for (int j = 1; j < sep.length; j++) {
                portion.get(i).add(sep[j]);
            }
            i++;

        }

        if (reader != null) {
            reader.close();
        }

        Mat trainData = new Mat(portion.size(), portion.get(0).size(), CvType.CV_32FC1);
        Mat trainClasses = new Mat(portion.size(), 1, CvType.CV_32FC1);

        for (i = 0; i < portion.size(); i++) {
            for (int j = 0; j < portion.get(i).size(); j++) {
                trainData.put(i, j, (float) Double.parseDouble(portion.get(i).get(j)));
            }
            trainClasses.put(i, 0, (float) Double.parseDouble(character.get(i)));
        }


        KNearest knn = KNearest.create();
        knn.train(trainData, Ml.ROW_SAMPLE, trainClasses);

        return knn;

    }

    /**
     *
     * KNN test with train data digit-train-data.data located in Assets folder.
     * Expects to classify characteristic vector as number given to classifier in training state.
     *
     * @param knn trained K-nearest neighbor classifier
     * @throws IOException if can not find digit-train-data.data located in Assets folder
     */
    private void testKnn(KNearest knn) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("digit-train-data.data"), "UTF-8"));

        String line;

        String character;
        ArrayList<String> portion = new ArrayList<>();


        if ((line = reader.readLine()) != null) {

            String[] sep = line.split(",");
            character = sep[0];

            for (int j = 1; j < sep.length; j++) {
                portion.add(sep[j]);
            }


        } else {
            return;
        }

        if (reader != null) {
            reader.close();
        }

        Mat trainData = new Mat(1, portion.size(), CvType.CV_32FC1);
        Mat trainClasses = new Mat(1, 1, CvType.CV_32FC1);

        for (int i = 0; i < portion.size(); i++) {
            trainData.put(0, i, (float) Double.parseDouble(portion.get(i)));
        }

        float predict = knn.findNearest(trainData, 1, trainClasses);

        double[] d = trainClasses.get(0, 0);

        Log.e(TAG, "testKnn: Expected: '" + character + "' Got: '" + Integer.toString((int) predict) + "'");

        if (character.equals(Integer.toString((int) predict))) {
            Log.e(TAG, "testKnn: KNN IS READY!");
        }

        return;

    }

}
