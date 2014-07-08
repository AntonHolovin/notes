package com.golovin.notes.controller;

import android.app.Application;

public class NotesApplication extends Application {

    private static NotesApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;
    }

    public DataSourceManager getDataSourceManager() {
        return DataSourceManager.getInstance(this);
    }

    public static NotesApplication getInstance() {
        return sInstance;
    }
}