package com.example.dyckster.sebbiatesttask;

import android.app.Application;
import android.content.Context;

import com.activeandroid.sebbia.ActiveAndroid;
import com.activeandroid.sebbia.Configuration;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by dyckster on 16.11.2016.
 */

public class SebbiaTestTaskApplication extends Application {
    //Comment by: dombaev_yury
    // Опасно
    private static Context context;
    private static SebbiaTestTaskApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        SebbiaTestTaskApplication.context = getApplicationContext();
        JodaTimeAndroid.init(this);
        initializeActiveAndroid();
    }

    public void initializeActiveAndroid() {
        ActiveAndroid.setLoggingEnabled(true);
        ActiveAndroid.initialize(new Configuration
                .Builder(this)
                .setDatabaseName("sebbiatask.db")
                .setDatabaseVersion(4)
                .create(), false);
    }

    public static Context getAppContext() {
        return SebbiaTestTaskApplication.context;
    }

    public static SebbiaTestTaskApplication getInstance() {
        return instance;
    }
}
