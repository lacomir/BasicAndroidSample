package com.bakalris.example.basicandroidsample;

/**
 * Created by Mirko on 16.4.2016.
 * Copyright: http://www.johndcook.com/blog/skewness_kurtosis/
 */

public class RunningStats {

    private long n;
    private double M1, M2, M3, M4;

    public RunningStats() {
        Clear();
    }

    public void Clear() {
        n = 0;
        M1 = M2 = M3 = M4 = 0.0;
    }

    public void Push(double x) {
        double delta, delta_n, delta_n2, term1;

        long n1 = n;
        n++;
        delta = x - M1;
        delta_n = delta / n;
        delta_n2 = delta_n * delta_n;
        term1 = delta * delta_n * n1;
        M1 += delta_n;
        M4 += term1 * delta_n2 * (n*n - 3*n + 3) + 6 * delta_n2 * M2 - 4 * delta_n * M3;
        M3 += term1 * delta_n * (n - 2) - 3 * delta_n * M2;
        M2 += term1;
    }

    public long NumDataValues() {
        return n;
    }

    public double Mean() {
        return M1;
    }

    public double Variance() {
        return M2/(n-1.0);
    }

    public double StandardDeviation() {
        return Math.sqrt(Variance());
    }

    public double Skewness() {
        return Math.sqrt((double) n ) * M3/ Math.pow(M2, 1.5);
    }

    public double Kurtosis() {
        return (double)n*M4 / (M2*M2) - 3.0;
    }


}
