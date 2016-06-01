package com.bakalris.example.basicandroidsample;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.telephony.TelephonyManager;

import com.orm.SugarApp;

import java.util.Random;
import java.util.UUID;

/**
 * @author Miroslav Laco
 * All rights reserved.
 */

public class App extends SugarApp {

    private static final String LOGTAG = "App";

    private static Context mContext;
    private static App sInstance;

    public static final String APP_DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/Bakalarka/";

    public static App getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mContext = this;

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }



    public static Context getContext() {
        return mContext;
    }



}
