package com.bakalris.example.basicandroidsample;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.telephony.TelephonyManager;

import com.orm.SugarApp;

import java.util.Random;
import java.util.UUID;

/**
 * @author lukassos
 * @date 4/6/2016
 * @time 10:27 AM
 * All rights reserved.
 */

public class App extends SugarApp {

    private static final String LOGTAG = "App";

    private static Context mContext;
    private static Random r;
    private static App sInstance;

    public static final String APP_DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/Bakalarka/";

    public static App getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        r = new Random();
        mContext = this;

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static Random getRandom() {
        return r;
    }

    public static Context getContext() {
        return mContext;
    }

    public static long getTimestamp() {
        return System.currentTimeMillis();
    }


    public String getUniqueId() {
        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        return deviceUuid.toString();
    }

}
