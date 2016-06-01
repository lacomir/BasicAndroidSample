package com.bakalris.example.basicandroidsample;

import java.util.ArrayList;

/**
 * @author Miroslav Laco
 * All rights reserved.
 *
 * Class for storing computer representation of word search puzzle recognized from image.
 *
 */

public class Osemsmerovka extends Hlavolam {


    private Letter[][] letterField;
    private ArrayList<Letter[]> wordList = new ArrayList<>();

    private String solution = null;


    public Osemsmerovka(int width, int height) {

        letterField = new Letter[height][width];

        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                letterField[i][j] = new Letter();
            }
        }

    }


    @Override
    public Letter[][] getLetters() {
        return letterField;
    }

    @Override
    public void solveProblem() {

        ArrayList<ArrayList<String> > letters = new ArrayList<>();
        ArrayList<String> words = new ArrayList<>();

        for(int i = 0; i < letterField.length; i++) {

            ArrayList<String> row = new ArrayList<>();

            for(int j = 0; j < letterField.length; j++) {
                row.add(letterField[i][j].character);
            }

            letters.add(row);
        }

        for(int i = 0; i < wordList.size(); i++) {

            StringBuilder word = new StringBuilder();

            for(int j = 0; j < wordList.get(i).length; j++) {
                word.append(wordList.get(i)[j].character);
            }

            words.add(word.toString());
        }

        WordPuzzleSolver solver = new WordPuzzleSolver(letters,words);

        solution = solver.solveWordPuzzle();

    }

    public Letter[][] getLetterField() {
        return letterField;
    }

    public void setLetterField(Letter[][] letterField) {
        this.letterField = letterField;
    }

    public ArrayList<Letter[]> getWordList() {
        return wordList;
    }

    public void setWordList(ArrayList<Letter[]> wordList) {
        this.wordList = wordList;
    }

    public String getSolution() {
        return solution;
    }




}
