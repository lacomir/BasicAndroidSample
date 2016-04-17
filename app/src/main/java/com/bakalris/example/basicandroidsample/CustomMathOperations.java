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
 * Created by Mirko on 16.4.2016.
 */
public class CustomMathOperations {

    public static Point subtractPoints(Point p1, Point p2) {

        return new Point(p1.x - p2.x, p1.y - p2.y);

    }

    public static Point addPoints(Point p1, Point p2) {

        return new Point(p1.x + p2.x, p1.y + p2.y);

    }

    public static Point multiplyPoints(Point p1, Point p2) {

        return new Point(p1.x * p2.x, p1.y * p2.y);

    }

    public static Point multiplyPoints(Point p1, double t) {

        return new Point(p1.x * t, p1.y * t);

    }



    public static double lineLength(Point A, Point B) {

        Point diff = subtractPoints(A,B);

        return Math.sqrt((diff.x * diff.x) + (diff.y * diff.y));

    }



    private static double DistanceToSquared( Point p, Point t )
    {
        double dX = p.x - t.x;
        double dY = p.y - t.y;

        return dX * dX + dY * dY;
    }

    private static double DistanceTo( Point p, Point t )
    {
        return Math.sqrt(DistanceToSquared(p, t));
    }

    private static double DotProduct( Point p, Point t )
    {
        return t.x * p.x + t.y * p.y;
    }

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

    public static double pointLineDistance(Point[] l1, Point l2) {


        return  DistanceFromLineSegmentToPoint( l1[0], l1[1], l2);

    }











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

		/*
		float abDistanceToX = min(abs(A.x - r.x),abs(B.x - r.x));
		float abDistanceToY = min(abs(A.y - r.y),abs(B.y - r.y));
		float cdDistanceToX = min(abs(C.x - r.x),abs(D.x - r.x));
		float cdDistanceToY = min(abs(C.y - r.y),abs(D.y - r.y));
		float maxDist = 20;

		if(abDistanceToX > maxDist || abDistanceToY > maxDist || cdDistanceToX > maxDist || cdDistanceToY > maxDist)
			return Point2f(-1,-1); //false

		*/

        if(r.x >= 0 && r.x < width && r.y >=0 && r.y < height)
            return r; //true
        else
            return new Point(-1,-1); //false

    }



    public static Mat getMat(MatOfPoint mop) {

        Point[] points = mop.toArray();
        Mat obj = new Mat(points.length,1, CvType.CV_32FC2);

        for(int i = 0; i < points.length; i ++) {
            obj.put(i, 0, new double[]{points[i].x, points[i].y});
        }

        return obj;
    }

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


}
