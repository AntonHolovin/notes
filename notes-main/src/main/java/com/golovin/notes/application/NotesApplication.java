package com.golovin.notes.application;

import android.app.Application;
import com.golovin.notes.data.DaoMaster;
import com.golovin.notes.data.DaoSession;
import com.golovin.notes.data.NotesDataSource;

public class NotesApplication extends Application {

    private static NotesApplication sInstance;

    private NotesDataSource mNotesDataSource;

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;
    }

    public NotesDataSource getNotesDataSource() {
        if (mNotesDataSource == null) {
            initDaoSession();
        }

        return mNotesDataSource;
    }

    private void initDaoSession() {
        DaoMaster.DevOpenHelper openHelper = new DaoMaster.DevOpenHelper(this, "notes.db", null);
        DaoMaster daoMaster = new DaoMaster(openHelper.getWritableDatabase());

        DaoSession daoSession = daoMaster.newSession();

        mNotesDataSource = new NotesDataSource(daoSession);
    }

    public static NotesApplication getInstance() {
        return sInstance;
    }
}