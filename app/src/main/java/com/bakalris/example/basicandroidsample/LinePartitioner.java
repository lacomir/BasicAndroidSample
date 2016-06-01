package com.bakalris.example.basicandroidsample;

import org.opencv.core.Point;

import java.util.ArrayList;

/**
 * @author Miroslav Laco
 * Copyright: OpenCV
 *
 * Refactored code from OpenCV C++ library for desktop devices.
 * Clustering method of elements based on predicate.
 *
 */

public class LinePartitioner {

    private int nclasses;

    /**
     *
     * @return number of clusters found in given set.
     */
    public int getNclasses() {
        return nclasses;
    }


    /**
     *
     * Predicate method for clustering given set of lines.
     * Two lines are clustered based on this predicate when their angle is not greater than 1 degree
     * and their shortest distance is not greater than 10 pixels.
     *
     * @param _l1 first line defined by start and end points
     * @param _l2 second line defined by start and end points
     * @return true if lines are considered to be clustered, false if not
     */
    private Boolean predicate(Point[] _l1, Point[] _l2) {

        final int MAX_DIST = 10;
        final double MAX_ANGLE = Math.cos(Math.PI / 60);

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

        if (Math.abs(product / (length1 * length2)) < MAX_ANGLE)
            return false;

        double distL1 = Math.min(CustomMathOperations.pointLineDistance(_l1, _l2[0]), CustomMathOperations.pointLineDistance(_l1, _l2[1]));
        double distL2 = Math.min(CustomMathOperations.pointLineDistance(_l2, _l1[0]), CustomMathOperations.pointLineDistance(_l2, _l1[1]));
        double dist = Math.min(distL1, distL2);

        if(dist > MAX_DIST)
            return false;



        return true;
    }

    /**
     *
     * Refactored clustering method from OpenCV C++ library for desktop devices.
     *
     * @param vec given set to be clustered
     * @return field of clusters labels; row in field corresponds to row in given set; number in row represents cluster if of element in given set
     */
    public int[] partition( ArrayList<Point[]> vec)
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
        nclasses = 0;

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


}
