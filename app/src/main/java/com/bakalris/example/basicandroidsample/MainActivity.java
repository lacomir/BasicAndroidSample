package com.bakalris.example.basicandroidsample;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = "MainActivity";
    public int mInt = 0;


    /* must functions for opencv
    starte
     */
    private View mCameraLayout;
    private View mProgressLayout;
    private CameraBridgeViewBase mOpenCvCameraView;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };


    private Mat mRgba;
    private Mat mIntermediateMat;
    private Mat mGray;
    private int mViewMode = -1;
    private MenuItem mMenuItem_DEMO;
    private MenuItem mMenuItem_NORMAL;
    private MenuItem mMenuItem_GAUS_7_GRAY;
    private MenuItem mMenuItem_CANNY;
    private MenuItem mMenuItem_CONTOURS;
    private MenuItem mMenuItem_HOUGH_LINES;
    private Size mFrameSize;
    // 8U : 8 unsigned ints per color = 256 values
    // C1 : only one color channel
    // C3 : three channels for colors like BGR, RGB, YUV etc.
    private static final int COLOR_TYPE = CvType.CV_8UC4;
    private static final int GRAY_TYPE = CvType.CV_8UC1;


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

        // set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        /* init layouts for opencv
                START
         */
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.opencv_camera_view);
        mCameraLayout = findViewById(R.id.camera_layout);
        mProgressLayout = findViewById(R.id.camera_progress);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);


        /* init layouts for opencv
                END
         */

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final TextView textView = (TextView) findViewById(R.id.hello);

        // reference activity for embeded function calls
        final Activity a =  this;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // CONVENIENT MESSAGE TYPE 1
                // only if using com.android.support:design:x.y.z library
                // it has action possibility :
                // more after self study :
                // http://www.androidhive.info/2015/09/android-material-design-snackbar-example/
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                // CONVENIENT MESSAGE TYPE 2
                // This is in since the droid walked the earth side by side with Google
                Toast.makeText(a, "Replace with your own action", Toast.LENGTH_LONG)
                        .show();

                // HOW TO SLEEP AND RUN SOMETHING AFTER THE TIME :
                //
                int SLEEP_INTERVAL_MS = 5000;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        fab.setVisibility(View.VISIBLE);
                    }
                }, SLEEP_INTERVAL_MS);

                // SET VISIBILITY OF BUTTON
                fab.setVisibility(View.GONE);


                // HOW TO RUN SOMETHING ON ANOTHER THAN UI THREAD :
                //
                Handler handler2 = new Handler();
                handler2.post(new Runnable() {
                    public void run() {
                        textView.append("\n wof wof");
                    }
                });

                // CALL ANOTHER ACTIVITY
                showAllKofolas();
            }
        });
        FloatingActionButton fabLogin = (FloatingActionButton) findViewById(R.id.fabLogin);
        fabLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(a, LoginActivity.class);
                //intent.putExtra(.BUNDLE_DATA, mItemResult.getUser_login());
                startActivity(intent);
            }
        });

        // get string from resources
        String appName = getResources().getString(R.string.app_name);

        // get int from resources
        mInt = getResources().getInteger(R.integer.vyska);

        // set text to text view
        textView.setText(textView.getText() + " " + mInt);

        // get variables from persistence layer : Shared Preferences approach
        // Access shared preferences space
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        // write into it
        // indexing is based upon string key words
        // don`t forget to commit changes, after changing local SharedPreferences instance
        String somethingSmallKey = "somethingSmall";
        prefs.edit().putInt(somethingSmallKey, mInt).commit();
        // read from it
        // indexing is the same
        // after writing in some type of object the same type of object has to be read
        // we can choose from various types : Boolean, Int, Long, Float, Double, String, StringSet
        int readPrefsVal = prefs.getInt(somethingSmallKey, 0);
        textView.setText(textView.getText() + "\n Prefs Val : " + readPrefsVal);
        // check if the value is already in
        textView.append("" + prefs.contains(somethingSmallKey));

    }

    private void showAllKofolas() {
        // open new activity from class reference
        // it has to be in manifest
        Intent intent = new Intent(this, DbViewActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // add custom menu items without tempering with pre-compiled menu.xml file
        mMenuItem_DEMO = menu.add("DEMO");
        mMenuItem_NORMAL = menu.add("NORMAL");
        mMenuItem_GAUS_7_GRAY = menu.add("GAUS_7_GRAY");
        mMenuItem_CANNY = menu.add("CANNY");
        mMenuItem_CONTOURS = menu.add("CONTOURS");
        mMenuItem_HOUGH_LINES = menu.add("HOUGH_LINES");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (item == mMenuItem_DEMO) {
            mViewMode = DEMO;
        } else if (item == mMenuItem_NORMAL) {
            mViewMode = NORMAL;
        } else if (item == mMenuItem_GAUS_7_GRAY) {
            mViewMode = GAUS_7_GRAY;
        } else if (item == mMenuItem_CANNY) {
            mViewMode = CANNY;
        } else if (item == mMenuItem_CONTOURS) {
            mViewMode = CONTOURS;
        } else if (item == mMenuItem_HOUGH_LINES) {
            mViewMode = HOUGH_LINES;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // save each local variable that has to be saved before pausing the activity in onPause
        // and will not be restored in onResume
        super.onSaveInstanceState(outState);
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.

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



        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
        mGray = new Mat(height, width, CvType.CV_8UC1);

        mFrameSize = new Size(width, height);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
        mGray.release();
        mIntermediateMat.release();

    }

    @Override
    public Mat onCameraFrame(final CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        // first of all think of what you need to do before you do it
//        if (mViewMode == DEMO) {
//            return littleBitOfPreprocessing(inputFrame, roundRobinMode(COUNT_OF_DEMO_MODES, WHOLE_DEMO_TIME));
//        }
//        return littleBitOfPreprocessing(inputFrame, mViewMode);

        System.out.println("CONTROLLER: preprocessing started!");

        inputFrame.rgba().copyTo(mRgba);
        inputFrame.gray().copyTo(mGray);

        Controller controller = new Controller(mRgba,mGray);
        controller.preprocessImage();
        controller.segmentImage();
        controller.computeCharacteristicVector();

        System.out.println("CONTROLLER: preprocessing passed!");

        return controller.drawSudokuSquares(mRgba.width(),mRgba.height());
    }

    public static final int COUNT_OF_DEMO_MODES = 5;
    public static final int WHOLE_DEMO_TIME = 50;
    public static final int DEMO = -1;
    public static final int NORMAL = 0;
    public static final int GAUS_7_GRAY = 1;
    public static final int CANNY = 2;
    public static final int CONTOURS = 3;
    public static final int HOUGH_LINES = 4;
    public static final int FIND_LETTERS = 5;


    private Mat littleBitOfPreprocessing(CameraBridgeViewBase.CvCameraViewFrame inputFrame, int mode) {
        // in here just scramble the egs and paint the beauty



/*
        switch (mode) {
            case NORMAL:
                mRgba = inputFrame.rgba();
                break;
            case GAUS_7_GRAY:
                Imgproc.GaussianBlur(inputFrame.gray(), mGray, new Size(7, 7), 0.3);
                Imgproc.cvtColor(mGray, mRgba, Imgproc.COLOR_GRAY2RGBA, 4);
                break;
            case CANNY:
                Imgproc.Canny(inputFrame.gray(), mIntermediateMat, 80, 100);
                Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2RGBA, 4);
                break;
            case CONTOURS:
                mGray = inputFrame.gray();
                List<MatOfPoint> contours = new ArrayList<>();
                Mat hierarchy = new Mat();
                Imgproc.Canny(mGray, mIntermediateMat, 70, 223);
                Imgproc.findContours(mIntermediateMat, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
                hierarchy.release();
                clearRGBAtoBlack();
                Imgproc.drawContours(mRgba, contours, -1, new Scalar(App.getRandom().nextInt(255), App.getRandom().nextInt(255), App.getRandom().nextInt(255))); // -1 for drawing all of them
                break;
            case HOUGH_LINES:

                // this processing should be done without previews,
                // because it is way too slow to count and draw all the lines .. under 3 FPS

//                Imgproc.Sobel(inputFrame.gray(), mGray, inputFrame.gray().depth(), 0, 1, 3, 1, 0);
//                Imgproc.threshold(mGray, mGray, 0, 255, Imgproc.THRESH_OTSU | Imgproc.THRESH_BINARY);
                Imgproc.Canny(inputFrame.gray(), mIntermediateMat, 80, 100);

                Mat lines = new Mat();
                int threshold = 100;
                int minLineSize = 10;
                int lineGap = 2;

                Imgproc.HoughLinesP(mIntermediateMat, lines, 1, Math.PI / 180, threshold, minLineSize, lineGap);


                List <Mat> toBeMerged = new ArrayList<>();
                toBeMerged.add(mIntermediateMat);
                toBeMerged.add(mIntermediateMat);
                toBeMerged.add(mIntermediateMat);
                toBeMerged.add(Mat.ones(mFrameSize, GRAY_TYPE));
                Core.merge(toBeMerged, mRgba);

                // check the lines in output - dimensions does not copy cpp implementation
                Log.e(TAG, "littleBitOfPreprocessing: lines.toString() :"+ lines.toString());
                Log.e(TAG, "littleBitOfPreprocessing: lines.rows() :"+ lines.rows());
                Log.e(TAG, "littleBitOfPreprocessing: lines.cols() :"+ lines.cols());

                int maxDrawnLines = 20;
                for (int row = 0; row < lines.rows() && row < maxDrawnLines; row++)
                {
                    double[] vec = lines.get(row, 0);

                    double x1 = vec[0],
                            y1 = vec[1],
                            x2 = vec[2],
                            y2 = vec[3];
                    Point start = new Point(x1, y1);
                    Point end = new Point(x2, y2);
                    Log.e(TAG, "littleBitOfPreprocessing: line ["+row+"] : "+start+" "+end );
                    Imgproc.line(mRgba, start, end, new Scalar(0, 255, 0), 3);
                }
                break;
            case FIND_LETTERS:
                // TODO : detect letters with cascade filter
                // TODO : stop camera preview and show only progress bar
                // TODO : run new async task for background processing of found objects
                //           and don`t forget to restore preview and hide progress bar
                //
                //
                // TODO : set some timer to kill the task if not done under 30 sec
                //
            default:
                mRgba = inputFrame.rgba();
        }
        // In here only fast preprocessing should be done !!!
        //
        // After we find our object of interest in the image,
        //  take the image and do the real processing on background thread,
        //  while user see only the progress bar.
        //
        // Otherwise our screen freeze until processing is done, which is bad UI pattern.
        //  Thus something like contours, exhausting search etc. is not suitable here.
        */

        return mRgba;
    }

    private void clearRGBAtoBlack() {
        mRgba = Mat.zeros(mFrameSize,COLOR_TYPE);
    }

    /*
    *   @param modesCount - positive integer
    *   @return index of the mode in between 0 and modesCount
     */
    private int roundRobinMode(int modesCount, int roundTime) {
        long timestamp = System.currentTimeMillis() / 1000;
        int current = (int) (timestamp % roundTime);
        int step = (int) (roundTime / modesCount);
        return current / step;
    }
}
