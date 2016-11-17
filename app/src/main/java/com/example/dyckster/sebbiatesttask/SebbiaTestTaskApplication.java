package com.example.dyckster.sebbiatesttask;

import android.app.Application;
import android.content.Context;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by dyckster on 16.11.2016.
 */

public class SebbiaTestTaskApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        SebbiaTestTaskApplication.context = getApplicationContext();
        JodaTimeAndroid.init(this);
    }

    public static Context getAppContext() {
        return SebbiaTestTaskApplication.context;
    }
}
