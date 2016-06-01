package com.bakalris.example.basicandroidsample;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Miroslav Laco
 * All rights reserved.
 *
 * Utils static class for storing math and image processing methods.
 *
 */

public class CustomMathOperations {

    public static final int ROTATE_LEFT = 0;
    public static final int ROTATE_RIGHT = 1;


    /**
     *
     * Method for point subtraction.
     *
     * @param p1 First point
     * @param p2 Second point
     * @return p1-p2
     */
    public static Point subtractPoints(Point p1, Point p2) {

        return new Point(p1.x - p2.x, p1.y - p2.y);

    }

    /**
     *
     * Method for point addition.
     *
     * @param p1 First point
     * @param p2 Second point
     * @return p1+p2
     */
    public static Point addPoints(Point p1, Point p2) {

        return new Point(p1.x + p2.x, p1.y + p2.y);

    }

    /**
     *
     * Method for multiplication of 2 points
     *
     * @param p1 First point
     * @param p2 Second point
     * @return p1*p2
     */
    public static Point multiplyPoints(Point p1, Point p2) {

        return new Point(p1.x * p2.x, p1.y * p2.y);

    }

    /**
     *
     * Method for multiplication of point and a constant.
     *
     * @param p1 First point
     * @param t constant
     * @return p1*t
     */
    public static Point multiplyPoints(Point p1, double t) {

        return new Point(p1.x * t, p1.y * t);

    }


    /**
     *
     * Method for computing line segment length defined by start and end points.
     *
     * @param A Start point of line segment.
     * @param B End point of line segment.
     * @return length of line defined as AB
     */
    public static double lineLength(Point A, Point B) {

        Point diff = subtractPoints(A,B);

        return Math.sqrt((diff.x * diff.x) + (diff.y * diff.y));

    }


    /**
     *
     * Method for computing squared distance of 2 points.
     *
     * @param p First point
     * @param t Second point
     * @return squared distance of p from t
     */
    private static double DistanceToSquared( Point p, Point t )
    {
        double dX = p.x - t.x;
        double dY = p.y - t.y;

        return dX * dX + dY * dY;
    }

    /**
     *
     * Method for computing distance of 2 points.
     *
     * @param p First point
     * @param t Second point
     * @return distance of p from t
     */
    private static double DistanceTo( Point p, Point t )
    {
        return Math.sqrt(DistanceToSquared(p, t));
    }

    /**
     *
     * Method for computing dot product od 2 points.
     *
     * @param p First point
     * @param t Second point
     * @return dot product of p and t
     */
    private static double DotProduct( Point p, Point t )
    {
        return t.x * p.x + t.y * p.y;
    }

    /**
     *
     * Method for computing distance of point to line segment.
     *
     * @param v start point of line segment
     * @param w end point of line segment
     * @param p point
     * @return distance of p to line segment vw
     */
    private static double DistanceFromLineSegmentToPoint( Point v, Point w, Point p)
    {
        double distSq = DistanceToSquared( w, v );

        if ( distSq == 0.0 )
        {
            return DistanceTo( p, v );
        }

        // consider the line extending the segment, parameterized as v + t (w - v)
        // we find projection of point p onto the line
        // it falls where t = [(p-v) . (w-v)] / |w-v|^2

        Point temp = new Point( p.x - v.x, p.y - v.y );
        Point segVector = new Point(w.x - v.x, w.y - v.y);

        double t = DotProduct(segVector, temp ) / distSq;

        if ( t < 0.0 )
        {
            // beyond the v end of the segment
            return DistanceTo( p, v );
        }
        else if ( t > 1.0 )
        {
            // beyond the w end of the segment
            return DistanceTo( p, w );
        }

        // projection falls on the segment
        Point segVectorMulT = new Point(segVector.x * t, segVector.y * t );
        Point projection = new Point(v.x + segVectorMulT.x, v.y + segVectorMulT.y);

        return DistanceTo( projection, p );

    }

    /**
     *
     * Method for computing distance of point to line segment.
     *
     * @param l1 line segment defined by start and end points
     * @param l2 point
     * @return distance of p to line segment vw
     */
    public static double pointLineDistance(Point[] l1, Point l2) {


        return  DistanceFromLineSegmentToPoint( l1[0], l1[1], l2);

    }


    /**
     *
     * Method for checking if found intersection of 2 lines lays on both of 2 line segments.
     *
     * @param ab First line segment
     * @param cd Second line segment
     * @param q intersection point
     * @return true if intersection belongs both segments, false if not
     */
    public static Boolean intersectionBelongsSegments(Point[] ab, Point[] cd, Point q) {
        Point p = ab[0];
        Point r = ab[1];

        Point s = cd[0];
        Point t = cd[1];

        int pad = 20;

        if (q.x <= Math.max(p.x, r.x) + pad && q.x >= Math.min(p.x, r.x) - pad && q.y <= Math.max(p.y, r.y) + pad && q.y >= Math.min(p.y, r.y)  - pad ) { //aj druha lina
            if (q.x <= Math.max(s.x, t.x) + pad && q.x >= Math.min(s.x, t.x) - pad && q.y <= Math.max(s.y, t.y) + pad && q.y >= Math.min(s.y, t.y) - pad)
                return true;
        }

        return false;
    }

    /**
     *
     * Method for finding intersections of 2 line segments defined by 2 points each.
     * Intersection must lay within image, otherwise line segments are considered to have no intersection.
     * Intersection must lay on both line segments, otherwise line segments are considered to have no intersection.
     *
     * @param ab first line segment
     * @param cd second line segment
     * @param width width of image
     * @param height height of image
     * @return point of intersection if lines segments have one, returns Point(-1,-1) if not
     */
    public static Point intersection(Point[] ab, Point[] cd, int width, int height) {

        Point A = ab[0];
        Point B = ab[1];
        Point C = cd[0];
        Point D = cd[1];

        Point x = subtractPoints(C,A); // C - A
        Point d1 = subtractPoints(B,A); // B - A
        Point d2 = subtractPoints(D,C); // D - C

        Point r;

        double cross = d1.x*d2.y - d1.y*d2.x;

        if (Math.abs(cross) < /*EPS*/1e-8)
            return new Point(-1,-1); //false

        double t1 = (x.x * d2.y - x.y * d2.x)/cross;

		/*
		if(!(t1 >= 0 && t1 <= 1))
			return Point2f(-1,-1); //false
		*/

        r = addPoints(A, multiplyPoints(d1,t1));


        if(!intersectionBelongsSegments(ab,cd,r))
            return new Point(-1,-1); //false



        if(r.x >= 0 && r.x < width && r.y >=0 && r.y < height)
            return r; //true
        else
            return new Point(-1,-1); //false

    }


    /**
     *
     * Method used to transform object of type MatOfPoint to type Mat.
     *
     * @param mop matrix of points
     * @return matrix of points
     */
    public static Mat getMat(MatOfPoint mop) {

        Point[] points = mop.toArray();
        Mat obj = new Mat(points.length,1, CvType.CV_32FC2);

        for(int i = 0; i < points.length; i ++) {
            obj.put(i, 0, new double[]{points[i].x, points[i].y});
        }

        return obj;
    }

    /**
     *
     * Method used to transform object of type Mat to type MatOfPoint
     *
     * @param mat matrix of points
     * @return matrix of points
     */
    public static MatOfPoint getMatOfPoint(Mat mat) {

        List<Point> lp = new ArrayList<>();
        for(int i = 0; i < mat.rows(); i++) {
            double[] pt = mat.get(i,0);
            lp.add(new Point(pt[0],pt[1]));
        }

        MatOfPoint mop = new MatOfPoint();
        mop.fromList(lp);
        return mop;
    }

    /**
     *
     * Method for obtaining aspect ratio of mask of character.
     *
     * @param mask matrix defining bounding rect
     * @return mask aspect ratio
     */
    public static double boundingRectAspectRatio(Mat mask) {

        return ((double) mask.cols() / (double) mask.rows());

    }

    /**
     *
     * Method for obtaining ratio of ON pixels to OFF pixels in given matrix.
     *
     * @param mask matrix defining bounding rect
     * @param pointsSize number of ON pixels within mask
     * @return ratio of ON pixels to OFF pixels in mask
     */
    public static double foregroundToBackgroundRatio(Mat mask, int pointsSize) {

        double total = mask.cols() * mask.rows();

        return (pointsSize / total);

    }

    /**
     *
     * Method for computing custom characteristic moments of letters/numbers.
     * Uses class RunningStats to compute statistics on the fly.
     * Custom characteristic moments are made up of:
     *  1) relative mean of letter
     *  2) relation of middle of bounding box of letter to mean of letter in X direction
     *  3) relation of middle of bounding box of letter to mean of letter in Y direction
     *  4) relative of standard deviation of letter in X direction
     *  5) relative of standard deviation of letter in Y direction
     *  6) skewness of letter in X direction
     *  7) skewness of letter in Y direction
     *  7) Kurtosis of letter in X direction
     *  7) Kurtosis of letter in Y direction
     *
     * @param mask bounding box of letter
     * @param points ON pixels within bounding box of letter
     * @return characteristic vector for given letter
     */
    public static ArrayList<Double> computeMoments(Mat mask, ArrayList<Point> points) {

        RunningStats statX = new RunningStats();
        RunningStats statY = new RunningStats();

        ArrayList<Double> characteristics = new ArrayList<>();

        for(int i = 0; i < points.size(); i++ ) {
            statX.Push(points.get(i).x);
            statY.Push(points.get(i).y);
        }

        Point center;

        if(mask.cols() % 2 == 0) {
            if(mask.rows() % 2 == 0) {
                center = new Point(mask.cols()/2,mask.rows()/2);
            } else {
                center = new Point(mask.cols()/2,(mask.rows()/2 + 0.5));
            }
        } else if(mask.rows() % 2 == 0){
            center = new Point((mask.cols() / 2 + 0.5), mask.rows() / 2);
        } else {
            center = new Point((mask.cols() / 2 + 0.5), (mask.rows() / 2 + 0.5));
        }

        double relativeMean = statX.Mean() / statY.Mean() / mask.cols() * mask.rows();
        double meanCenterRelationX = (statX.Mean() - center.x) / mask.cols();
        double meanCenterRelationY = (statY.Mean() - center.y) / mask.rows();

        characteristics.add(relativeMean);
        characteristics.add(meanCenterRelationX);
        characteristics.add(meanCenterRelationY);
        characteristics.add(statX.StandardDeviation() / mask.cols());
        characteristics.add(statY.StandardDeviation() / mask.rows());
        characteristics.add(statX.Skewness());
        characteristics.add(statY.Skewness());
        characteristics.add(statX.Kurtosis());
        characteristics.add(statY.Kurtosis());

        return characteristics;

    }


    /**
     *
     * Method for rotating matrix.
     *
     * @param mat matrix to rotate
     * @param direction direction of rotation; 0 for rotate left, 1 for rotate right
     * @return rotated matrix
     */
    public static Mat rotateMat(Mat mat, int direction) {

        Mat rotatedMat = new Mat(mat.rows(),mat.cols(),mat.type());
        mat.copyTo(rotatedMat);

        Core.transpose(rotatedMat,rotatedMat);
        Core.flip(rotatedMat,rotatedMat,direction);

        return rotatedMat;

    }


}
