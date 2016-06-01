package com.bakalris.example.basicandroidsample;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import com.googlecode.tesseract.android.ResultIterator;
import com.googlecode.tesseract.android.TessBaseAPI;

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
