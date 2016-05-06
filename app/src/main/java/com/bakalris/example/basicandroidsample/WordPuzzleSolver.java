package com.bakalris.example.basicandroidsample;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Mirko on 7.5.2016.
 */
public class WordPuzzleSolver {

    private boolean puzzleBool[][];
    private ArrayList<ArrayList<String> > puzzle;
    private ArrayList<ArrayList<String> > words;

    public WordPuzzleSolver(ArrayList<ArrayList<String> > puzzle, ArrayList<String> words) {

        this.puzzle = puzzle;

        try {
            puzzleBool = new boolean[puzzle.size()][puzzle.get(0).size()];
        } catch(IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        this.words = new ArrayList<>();
        for(int i = 0; i < words.size(); i++) {

            ArrayList<String> tmp= new ArrayList<>();

            for(int j = 0; j < words.get(i).length(); j++) {
                tmp.add(Character.toString(words.get(i).charAt(j)));
            }

            this.words.add(tmp);
        }

    }

    private String solveWordPuzzle() {

        for(int i = 0; i < words.size(); i++) {
            if(!solveWord(words.get(i))) {

            }
        }

        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < puzzle.size(); i++) {
            for(int j = 0; j < puzzle.get(i).size(); j++) {
                if(!puzzleBool[i][j])
                    builder.append(puzzle.get(i).get(j));
            }
        }

        return builder.toString();
    }



    private boolean solveWord(ArrayList<String> find) {

        int maxI = puzzle.size();

        for(int i = 0; i < maxI; i++) {

            int maxJ = puzzle.get(i).size();

            for(int j = 0; j < maxJ; j++) {

                if(!(puzzle.get(i).get(j).equals(find.get(0))))
                    continue;

                //right
                if((j + (find.size()-1)) < maxJ)
                {

                    ArrayList<int[]> tempPuzzleBool = new ArrayList<int[]>();

                    int index[] = {i,j};
                    tempPuzzleBool.add(index);

                    int iter = j+1;
                    boolean solved = true;

                    for(int x = 0; x < find.size()-1; x++, iter++) {
                        if(!(puzzle.get(i).get(iter).equals(find.get(x + 1)))) {
                            solved = false;
                            break;
                        }
                        else {
                            int indexx[] = {i,iter};
                            tempPuzzleBool.add(indexx);
                        }
                    }

                    if(solved) {

                        for(int z = 0; z < tempPuzzleBool.size(); z++) {
                            puzzleBool[tempPuzzleBool.get(z)[0]][tempPuzzleBool.get(z)[1]] = true;
                        }

                        return true;

                    }

                }

                //left
                if((j - (find.size()-1)) >= 0)
                {

                    ArrayList<int[]> tempPuzzleBool = new ArrayList<int[]>();

                    int index[] = {i,j};
                    tempPuzzleBool.add(index);

                    int iter = j-1;
                    boolean solved = true;

                    for(int x = 0; x < find.size()-1; x++, iter--) {
                        if(!(puzzle.get(i).get(iter).equals(find.get(x + 1)))) {
                            solved = false;
                            break;
                        }
                        else {
                            int indexx[] = {i,iter};
                            tempPuzzleBool.add(indexx);
                        }
                    }

                    if(solved) {

                        for(int z = 0; z < tempPuzzleBool.size(); z++) {
                            puzzleBool[tempPuzzleBool.get(z)[0]][tempPuzzleBool.get(z)[1]] = true;
                        }

                        return true;

                    }
                }

                //down
                if((i + (find.size()-1)) < maxI)
                {

                    ArrayList<int[]> tempPuzzleBool = new ArrayList<int[]>();

                    int index[] = {i,j};
                    tempPuzzleBool.add(index);

                    int iter = i+1;
                    boolean solved = true;

                    for(int x = 0; x < find.size()-1; x++, iter++) {
                        if(!(puzzle.get(iter).get(j).equals(find.get(x + 1)))) {
                            solved = false;
                            break;
                        }
                        else {
                            int indexx[] = {iter,j};
                            tempPuzzleBool.add(indexx);
                        }
                    }

                    if(solved) {

                        for(int z = 0; z < tempPuzzleBool.size(); z++) {
                            puzzleBool[tempPuzzleBool.get(z)[0]][tempPuzzleBool.get(z)[1]] = true;
                        }

                        return true;

                    }
                }

                //up
                if((i - (find.size()-1)) >= 0)
                {

                    ArrayList<int[]> tempPuzzleBool = new ArrayList<int[]>();

                    int index[] = {i,j};
                    tempPuzzleBool.add(index);

                    int iter = i-1;
                    boolean solved = true;

                    for(int x = 0; x < find.size()-1; x++, iter--) {
                        if(!(puzzle.get(iter).get(j).equals(find.get(x + 1)))) {
                            solved = false;
                            break;
                        }
                        else {
                            int indexx[] = {iter,j};
                            tempPuzzleBool.add(indexx);
                        }
                    }

                    if(solved) {

                        for(int z = 0; z < tempPuzzleBool.size(); z++) {
                            puzzleBool[tempPuzzleBool.get(z)[0]][tempPuzzleBool.get(z)[1]] = true;
                        }

                        return true;

                    }
                }


                //Diagonal Right-Down
                if((j + (find.size()-1)) < maxJ && (i + (find.size()-1)) < maxI)
                {
                    ArrayList<int[]> tempPuzzleBool = new ArrayList<int[]>();

                    int index[] = {i,j};
                    tempPuzzleBool.add(index);

                    int iterI = i+1;
                    int iterJ = j+1;
                    boolean solved = true;

                    for(int x = 0; x < find.size()-1; x++, iterI++, iterJ++) {
                        if(!(puzzle.get(iterI).get(iterJ).equals(find.get(x + 1)))) {
                            solved = false;
                            break;
                        }
                        else {
                            int indexx[] = {iterI,iterJ};
                            tempPuzzleBool.add(indexx);
                        }
                    }

                    if(solved) {

                        for(int z = 0; z < tempPuzzleBool.size(); z++) {
                            puzzleBool[tempPuzzleBool.get(z)[0]][tempPuzzleBool.get(z)[1]] = true;
                        }

                        return true;

                    }
                }

                //Diagonal Right-Up
                if((j + (find.size()-1)) < maxJ && (i - (find.size()-1)) >= 0)
                {
                    ArrayList<int[]> tempPuzzleBool = new ArrayList<int[]>();

                    int index[] = {i,j};
                    tempPuzzleBool.add(index);


                    int iterI = i-1;
                    int iterJ = j+1;
                    boolean solved = true;

                    for(int x = 0; x < find.size()-1; x++, iterI--, iterJ++) {
                        if(!(puzzle.get(iterI).get(iterJ).equals(find.get(x + 1)))) {
                            solved = false;
                            break;
                        }
                        else {
                            int indexx[] = {iterI,iterJ};
                            tempPuzzleBool.add(indexx);
                        }
                    }

                    if(solved) {

                        for(int z = 0; z < tempPuzzleBool.size(); z++) {
                            puzzleBool[tempPuzzleBool.get(z)[0]][tempPuzzleBool.get(z)[1]] = true;
                        }

                        return true;

                    }
                }

                //Diagonal Left-Down
                if((j - (find.size()-1)) >= 0 && (i + (find.size()-1)) < maxI)
                {
                    ArrayList<int[]> tempPuzzleBool = new ArrayList<int[]>();

                    int index[] = {i,j};
                    tempPuzzleBool.add(index);

                    int iterI = i+1;
                    int iterJ = j-1;
                    boolean solved = true;

                    for(int x = 0; x < find.size()-1; x++, iterI++, iterJ--) {
                        if(!(puzzle.get(iterI).get(iterJ).equals(find.get(x + 1)))) {
                            solved = false;
                            break;
                        }
                        else {
                            int indexx[] = {iterI,iterJ};
                            tempPuzzleBool.add(indexx);
                        }
                    }

                    if(solved) {

                        for(int z = 0; z < tempPuzzleBool.size(); z++) {
                            puzzleBool[tempPuzzleBool.get(z)[0]][tempPuzzleBool.get(z)[1]] = true;
                        }

                        return true;

                    }
                }

                //Diagonal Left-Up
                if((j - (find.size()-1)) >= 0 && (i - (find.size()-1)) >= 0)
                {
                    ArrayList<int[]> tempPuzzleBool = new ArrayList<int[]>();

                    int index[] = {i,j};
                    tempPuzzleBool.add(index);

                    int iterI = i-1;
                    int iterJ = j-1;
                    boolean solved = true;

                    for(int x = 0; x < find.size()-1; x++, iterI--, iterJ--) {
                        if(!(puzzle.get(iterI).get(iterJ).equals(find.get(x + 1)))) {
                            solved = false;
                            break;
                        }
                        else {
                            int indexx[] = {iterI,iterJ};
                            tempPuzzleBool.add(indexx);
                        }
                    }

                    if(solved) {

                        for(int z = 0; z < tempPuzzleBool.size(); z++) {
                            puzzleBool[tempPuzzleBool.get(z)[0]][tempPuzzleBool.get(z)[1]] = true;
                        }

                        return true;

                    }
                }




            }

        }


        return false;
    }


}
