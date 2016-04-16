package com.bakalris.example.basicandroidsample;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Point;

import java.util.ArrayList;

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


    private static Boolean predicate(Point[] _l1, Point[] _l2) {

        double[] l1 = new double[4];
        l1[0] = _l1[0].x;
        l1[1] = _l1[0].y;
        l1[2] = _l1[1].x;
        l1[3] = _l1[1].y;

        double[] l2 = new double[4];
        l2[0] = _l2[0].x;
        l2[1] = _l2[0].y;
        l2[2] = _l2[1].x;
        l2[3] = _l2[1].y;

        double length1 = Math.sqrt((l1[2] - l1[0]) * (l1[2] - l1[0]) + (l1[3] - l1[1]) * (l1[3] - l1[1]));
        double length2 = Math.sqrt((l2[2] - l2[0]) * (l2[2] - l2[0]) + (l2[3] - l2[1]) * (l2[3] - l2[1]));

        double product = (l1[2] - l1[0])*(l2[2] - l2[0]) + (l1[3] - l1[1])*(l2[3] - l2[1]);

        if (Math.abs(product / (length1 * length2)) < Math.cos(Math.PI / 60))
            return false;

        double distL1 = Math.min(pointLineDistance(_l1, _l2[0]), pointLineDistance(_l1, _l2[1]));
        double distL2 = Math.min(pointLineDistance(_l2, _l1[0]), pointLineDistance(_l2, _l1[1]));
        double dist = Math.min(distL1, distL2);

        if(dist > 10)
            return false;



        return true;
    }

    public static int[] partition( ArrayList<Point[]> vec)
    {
        int i, j, N = vec.size();
        //const _Tp* vec = &_vec[0];

        int PARENT=0;
        int RANK=1;

        //std::vector<int> _nodes(N*2);
        //int (*nodes)[2] = (int(*)[2])&_nodes[0];

        int[][] nodes = new int[N*2][2];

        // The first O(N) pass: create N single-vertex trees
        for(i = 0; i < N; i++)
        {
            nodes[i][PARENT]=-1;
            nodes[i][RANK] = 0;
        }

        // The main O(N^2) pass: merge connected components
        for( i = 0; i < N; i++ )
        {
            int root = i;

            // find root
            while( nodes[root][PARENT] >= 0 )
                root = nodes[root][PARENT];

            for( j = 0; j < N; j++ )
            {
                if( i == j || !predicate(vec.get(i), vec.get(j)))
                    continue;
                int root2 = j;

                while( nodes[root2][PARENT] >= 0 )
                    root2 = nodes[root2][PARENT];

                if( root2 != root )
                {
                    // unite both trees
                    int rank = nodes[root][RANK], rank2 = nodes[root2][RANK];
                    if( rank > rank2 )
                        nodes[root2][PARENT] = root;
                    else
                    {
                        nodes[root][PARENT] = root2;
                        nodes[root2][RANK] += (rank == rank2) ? 1 : 0;
                        root = root2;
                    }
                    //CV_Assert(nodes[root][PARENT] < 0);

                    int k = j, parent;

                    // compress the path from node2 to root
                    while( (parent = nodes[k][PARENT]) >= 0 )
                    {
                        nodes[k][PARENT] = root;
                        k = parent;
                    }

                    // compress the path from node to root
                    k = i;
                    while( (parent = nodes[k][PARENT]) >= 0 )
                    {
                        nodes[k][PARENT] = root;
                        k = parent;
                    }
                }
            }
        }

        // Final O(N) pass: enumerate classes
        int[] labels = new int[N];
        int nclasses = 0;

        for( i = 0; i < N; i++ )
        {
            int root = i;
            while( nodes[root][PARENT] >= 0 )
                root = nodes[root][PARENT];
            // re-use the rank as the class label
            if( nodes[root][RANK] >= 0 )
                nodes[root][RANK] = ~nclasses++;
            labels[i] = ~nodes[root][RANK];
        }

        return labels;
    }





    public Boolean intersectionBelongsSegments(Point[] ab, Point[] cd, Point q) {
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

    public Point intersection(Point[] ab, Point[] cd, int width, int height) {

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







}
