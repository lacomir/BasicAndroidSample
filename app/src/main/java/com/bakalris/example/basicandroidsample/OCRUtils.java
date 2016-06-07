package com.bakalris.example.basicandroidsample;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;
import android.util.Pair;

import com.googlecode.tesseract.android.ResultIterator;
import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.core.Point;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author Lukáš Puchoň
 * All rights reserved.
 *
 * Tesseract utils for text recognition on image. Requires tesseract train data for Tesseract initialization.
 * Expects train data in Assets/tessdata folder. For train data based on slovak language we use slk.traineddata file.
 *
 */

public class OCRUtils {
    private static final String TAG = "OCRUtils";
    private static final String TESS_LANG = "slk";
    public static final String TESS_DATA_PATH = App.APP_DATA_PATH + "tessdata/";
    public static final String TESS_LANG_PACK_ASSETS = "tessdata/" + TESS_LANG + ".traineddata";
    public static final String TESS_LANG_PACK_APP_DATA = TESS_DATA_PATH + TESS_LANG + ".traineddata";
    private static final String WORD_MATCHING_REGEXP_1 = "\\d+,\\d+";
    private static final String WORD_MATCHING_REGEXP_2 = "\\d+";

    private static int LETTERS_LINE_WIDTH_MIN;

    public static boolean initAppDataPath(Activity activity) {

        Log.e(TAG, "initAppDataPath: 1" );
        String[] paths = new String[]{
                App.APP_DATA_PATH,
                TESS_DATA_PATH
        };
        Log.e(TAG, "initAppDataPath: 2" );
        for (String path : paths) {
            File dir = new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
                    return false;
                } else {
                    Log.v(TAG, "Created directory " + path + " on sdcard");
                }
            }

        }
        Log.e(TAG, "initAppDataPath: 3" );
        return initTessPack(activity);
    }

    private static boolean initTessPack(Activity activity) {
        if(!(new File(TESS_LANG_PACK_APP_DATA)).exists())
            try {
                Log.e(TAG, "initTessPack: 1" );
                InputStream in = activity.getAssets().open(TESS_LANG_PACK_ASSETS);
                Log.e(TAG, "initTessPack: 2" );
                OutputStream out = new FileOutputStream(TESS_LANG_PACK_APP_DATA);
                Log.e(TAG, "initTessPack: 3" );
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
                Log.v(TAG, "Successfully copied Asset " + TESS_LANG_PACK_ASSETS
                        + " to " + TESS_LANG_PACK_APP_DATA);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Error during file copy, could not create : "+TESS_LANG_PACK_APP_DATA);
                return false;
            }

        return true;
    }
    public static String getOCRText(Bitmap ocrPreprocessedBitmap) {
        Log.v(TAG, "TessTwo show time starts now !!");

        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        baseApi.init(App.APP_DATA_PATH, TESS_LANG);

        Log.e(TAG, "getOCRText: image size : " + ocrPreprocessedBitmap.getWidth() + " x " + ocrPreprocessedBitmap.getHeight());

        baseApi.setImage(ocrPreprocessedBitmap);

        String OCRedText = baseApi.getUTF8Text();
        //baseApi.getBoxText(0);

        baseApi.end();

        Log.v(TAG, "OCRed text: " + OCRedText);

//        textAlert(OCRedText);

        return OCRedText;
    }

    public static Osemsmerovka getOCRPixa(Bitmap ocrPreprocessedBitmap) {
        Log.v(TAG, "TessTwo show time starts now !!");

        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        baseApi.init(App.APP_DATA_PATH, TESS_LANG);

        Log.e(TAG, "getOCRText: image size : " + ocrPreprocessedBitmap.getWidth() + " x " + ocrPreprocessedBitmap.getHeight());

        baseApi.setImage(ocrPreprocessedBitmap);

        LETTERS_LINE_WIDTH_MIN = (int) (ocrPreprocessedBitmap.getWidth()/1.5);

        String OCRedText = baseApi.getUTF8Text();

        Log.e(TAG, "getOCRPixa: getUTF8Text():" + OCRedText);

        // Iterate through the results.
        final ResultIterator iterator = baseApi.getResultIterator();
        String lastUTF8Text;
        float lastConfidence;
        Rect lastBoundRect;
        iterator.begin();

        ArrayList<Pair<String,Rect>> pairs = new ArrayList<Pair<String,Rect>>();

        Log.e(TAG, "getOCRPixa: WORD");
        do {
            lastUTF8Text = iterator.getUTF8Text(TessBaseAPI.PageIteratorLevel.RIL_WORD);
            lastConfidence = iterator.confidence(TessBaseAPI.PageIteratorLevel.RIL_WORD);
            lastBoundRect = iterator.getBoundingRect(TessBaseAPI.PageIteratorLevel.RIL_WORD);
            Log.e(TAG, "getOCRPixa: " + lastUTF8Text + "-confidence- " + lastConfidence + "-rect- " +  lastBoundRect.centerY());

            //if(lastConfidence > 70.0)
                pairs.add(new Pair<String, Rect>(lastUTF8Text, lastBoundRect));
            //else
            //    pairs.add(new Pair<String, Rect>("|", lastBoundRect));

        } while (iterator.next(TessBaseAPI.PageIteratorLevel.RIL_WORD));

        Osemsmerovka osemsmerovka = processPairedResult(pairs);

        baseApi.end();

        return osemsmerovka;

    }

    private static Osemsmerovka processPairedResult(ArrayList<Pair<String, Rect>> pairs) {

        LinePartitioner partitioner = new LinePartitioner();

        ArrayList temp = pairs;

        int[] labels = partitioner.partition(temp,1); // 0 for line partition

        Log.e(TAG, "processPairedResult: pairs size:" + pairs.size());
        Log.e(TAG, "processPairedResult: labels size:" + labels.length);

        for(int i = 0; i < labels.length; i++) {
            Log.e(TAG, "processPairedResult: label[" + i + "]= " + labels[i] );
        }

        ArrayList<ArrayList<Pair<String, Rect>>> sortedPairs = new ArrayList<ArrayList<Pair<String, Rect>>>();
        for(int i = 0; i < partitioner.getNclasses(); i++)
            sortedPairs.add(new ArrayList<Pair<String, Rect>>());

        if(partitioner.getNclasses() == 0) {
            Log.e(TAG, "processPairedResult: return from SORTEDPAIRS");
            return null;
        }

        Log.e(TAG, "processPairedResult: sortedPairs size:" + sortedPairs.size());

        for(int i = 0; i < pairs.size(); i++) {
            sortedPairs.get(labels[i]).add(pairs.get(i));
        }

        Letter[][] letterField;
        ArrayList<Letter[]> wordList = new ArrayList<>();

        ArrayList<Pair<String, Rect>> letters = new ArrayList<>();
        ArrayList<Pair<Integer, Integer>> lengths = new ArrayList<>();

        ArrayList<String> words = new ArrayList<>();

//        for(int i = 0; i < sortedPairs.size(); i++) {
//            Log.e(TAG, "processPairedResult: SortedPairs[" + i + "]" );
//            for(int j = 0; j < sortedPairs.get(i).size(); j++) {
//                Log.e(TAG, sortedPairs.get(i).get(j).first);
//            }
//
//        }



        for(int i = 0; i < sortedPairs.size(); i++) {
            Log.e(TAG, "processPairedResult: sortedPairs[" + i + "]: " + sortedPairs.get(i).size());

            if(sortedPairs.get(i).size() > 1) {

                words = processWordLine(sortedPairs.get(i));

                for(int j = 0; j < words.size(); j++) {

                    if(words.get(j) == null)
                        continue;


                    Letter wordLet[] = new Letter[words.get(j).length()];

                    for(int x = 0; x < words.get(j).length(); x++) {
                        wordLet[x] = new Letter();
                        wordLet[x].hasChar = true;
                        wordLet[x].character = words.get(j).substring(x,x+1);
                        wordLet[x].boundRect = new org.opencv.core.Rect(new Point(sortedPairs.get(i).get(j).second.left,sortedPairs.get(i).get(j).second.top),new Point(sortedPairs.get(i).get(j).second.right,sortedPairs.get(i).get(j).second.bottom));
                    }

                    wordList.add(wordLet);
                }



            }
            else if(sortedPairs.get(i).size() > 0) { //&& sortedPairs.get(i).get(0).second.width() > LETTERS_LINE_WIDTH_MIN) {
                letters.add(sortedPairs.get(i).get(0));

                int addIndex = -1;
                for(int j = 0; j < lengths.size(); j++) {
                    if(lengths.get(j).first == sortedPairs.get(i).get(0).first.length())
                        addIndex=j;
                }
                if(sortedPairs.get(i).get(0).first != null && addIndex == -1)
                    lengths.add(new Pair<Integer,Integer>(sortedPairs.get(i).get(0).first.length(),1));
                else
                    lengths.set(addIndex, new Pair<Integer,Integer>(lengths.get(addIndex).first, (lengths.get(addIndex).second + 1)));

            }


        }

        if(letters.size() < 1) {
            Log.e(TAG, "processPairedResult: NO LETTER ROWS FOUND!");
            return null;
        }

        int commonLength = lengths.get(0).first;
        int commonCount = lengths.get(0).second;

        for(int i = 0; i < lengths.size(); i++) {

            Log.e(TAG, "processPairedResult: lengths[" + i + "]: " + lengths.get(i).first + ", " + lengths.get(i).second );

            if(lengths.get(i).first == 1 || lengths.get(i).second > commonCount) {
                commonLength = lengths.get(i).first;
                commonCount = lengths.get(i).second;
            }
        }

        Log.e(TAG, "processPairedResult: commonLength-" + commonLength + " commonCount-" + commonCount);


        int letterFieldWidth = commonLength;
        int letterFieldHeight = letters.size();
        letterField = new Letter[letterFieldHeight][letterFieldWidth];

        Pattern alphabet = Pattern.compile("[áéíĺóŕúýčďľňšťžäôÁÉÍĹÓŔÚÝČĎĽŇŠŤŽÄÔa-zA-Z]"); // SLOVAK ALPHABET

        for(int i = 0; i < letters.size(); i++) {

            int elementRectOffset = letters.get(i).second.width()/commonLength;
            Point topLeft = new Point(letters.get(i).second.left - elementRectOffset/4, letters.get(i).second.top);
            Point bottomRight = new Point((topLeft.x + elementRectOffset), letters.get(i).second.bottom);

            for (int j = 0; j < commonLength; j++) {
                letterField[i][j] = new Letter();

                if (letters.get(i).first.length() <= j) {
                    letterField[i][j].hasChar = false;
                } else {

                    Matcher m = alphabet.matcher(letters.get(i).first.substring(j, j + 1));

                    if(m.matches()) {
                        letterField[i][j].hasChar = true;
                        letterField[i][j].character = letters.get(i).first.substring(j, j + 1);
                    } else {
                        letterField[i][j].hasChar = false;
                    }
                }

                letterField[i][j].boundRect = new org.opencv.core.Rect(new Point((topLeft.x + j*elementRectOffset), topLeft.y), new Point((bottomRight.x + j*elementRectOffset), bottomRight.y));
            }
        }

        Log.e(TAG, "processPairedResult: LETTER FIELD:");

        for(int i = 0; i < letterField.length; i++) {
            for(int j = 0; j < letterField[i].length; j++) {
                if(letterField[i][j].hasChar)
                    Log.e(TAG, letterField[i][j].character);
                else
                    Log.e(TAG, "_");
            }

            Log.e(TAG, "------------------------");
        }

        Log.e(TAG, "======================================");

        Log.e(TAG, "processPairedResult: WORD LIST:");

        for(int i = 0; i < wordList.size(); i++) {
            for(int j = 0; j < wordList.get(i).length; j ++) {
                Log.e(TAG, wordList.get(i)[j].character);
            }

            Log.e(TAG, "------------------------");
        }

        Log.e(TAG, "======================================");

        Osemsmerovka osemsmerovka = new Osemsmerovka(letterFieldWidth,letterFieldHeight);
        osemsmerovka.setLetterField(letterField);
        osemsmerovka.setWordList(wordList);

        return osemsmerovka;

    }

    private static ArrayList<String> processWordLine(ArrayList<Pair<String, Rect>> pairs) {

        Pattern alphabetOne = Pattern.compile("[áéíĺóŕúýčďľňšťžäôÁÉÍĹÓŔÚÝČĎĽŇŠŤŽÄÔa-zA-Z]"); // SLOVAK ALPHABET
        Pattern alphabetMany = Pattern.compile("[áéíĺóŕúýčďľňšťžäôÁÉÍĹÓŔÚÝČĎĽŇŠŤŽÄÔa-zA-Z]+"); // SLOVAK ALPHABET

        ArrayList<String> result = new ArrayList<>();

        for(int i = 0; i < pairs.size(); i++) {

            String word = pairs.get(i).first;

            if(word.length() < 1) {
                result.add(null);
                continue;
            }

            Matcher m = alphabetOne.matcher(word.substring(0, 1));

            if(!m.matches()) {
                word = word.substring(1,word.length());
            }

            if(word.length() < 1) {
                result.add(null);
                continue;
            }

            m = alphabetOne.matcher(word.substring(word.length()-1,word.length()));

            if(!m.matches()) {
                word = word.substring(0,word.length()-1);
            }

            if(word.length() < 1) {
                result.add(null);
                continue;
            }

            m = alphabetMany.matcher(word);

            if(!m.matches()) {
                result.add(null);
                continue;
            } else {
                result.add(word);
            }

        }

        return result;
    }

    public static String getOCRTextRecognize(Bitmap ocrPreprocessedBitmap) {
        Log.v(TAG, "TessTwo show time starts now !!");

        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        baseApi.init(App.APP_DATA_PATH, TESS_LANG);

        Log.e(TAG, "getOCRText: image size : " + ocrPreprocessedBitmap.getWidth() + " x " + ocrPreprocessedBitmap.getHeight());

        baseApi.setImage(ocrPreprocessedBitmap);

        String OCRedText = baseApi.getUTF8Text();

        final ResultIterator iterator = baseApi.getResultIterator();
        String lastUTF8Text;
        float lastConfidence;
        Rect lastRect;
        int[] lastBox;
        int count = 0;
        iterator.begin();
        do {
            lastUTF8Text = iterator.getUTF8Text(TessBaseAPI.PageIteratorLevel.RIL_WORD);
            lastConfidence = iterator.confidence(TessBaseAPI.PageIteratorLevel.RIL_WORD);
            lastRect = iterator.getBoundingRect(TessBaseAPI.PageIteratorLevel.RIL_WORD);
            lastBox = iterator.getBoundingBox(TessBaseAPI.PageIteratorLevel.RIL_WORD);
            count++;
        } while (iterator.next(TessBaseAPI.PageIteratorLevel.RIL_WORD));


        baseApi.end();

        Log.v(TAG, "OCRed text: " + OCRedText);

        return OCRedText;
    }


    private static List<String> matchedWords(String inputText, String wordMatchingRegexp1) {
        List<String> words = new ArrayList<String>();
        Matcher m = Pattern.compile(WORD_MATCHING_REGEXP_1)
                .matcher(inputText);
        while (m.find()) {
            words.add(m.group());
        }
        // sort the collection
        Collections.sort(words);
        return words;
    }

}
