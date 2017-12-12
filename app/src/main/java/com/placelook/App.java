package com.placelook;

import android.app.Application;

/**
 * Created by victor on 14.10.17.
 */

public class App extends Application {
    private static App instance;
    private static Settings settings;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        settings = new Settings(this);

    }

    public static App getApp() {
        return instance;
    }

    public String getID() {
        return settings.getID();
    }
}
