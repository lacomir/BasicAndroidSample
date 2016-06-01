package com.bakalris.example.basicandroidsample;

/**
 * @author Miroslav Laco
 * All rights reserved.
 */

public abstract class Hlavolam {

    public int rows;
    public int cols;

    public abstract Letter[][] getLetters();

    public abstract void solveProblem();

}
