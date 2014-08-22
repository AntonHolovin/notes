package com.golovin.notes.controller;

import android.content.Context;
import com.golovin.notes.data.DaoMaster;
import com.golovin.notes.data.DaoSession;
import com.golovin.notes.data.Note;

import java.util.List;

public class DataSourceManager {

    private static DataSourceManager sInstance;

    private DaoSession mDaoSession;

    private DataSourceManager(Context context) {
        DaoMaster.DevOpenHelper openHelper = new DaoMaster.DevOpenHelper(context, "notes.db", null);
        DaoMaster daoMaster = new DaoMaster(openHelper.getWritableDatabase());

        mDaoSession = daoMaster.newSession();
    }

    public static DataSourceManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DataSourceManager(context);
        }

        return sInstance;
    }

    public long insert(Note note) {
        return mDaoSession.getNoteDao().insert(note);
    }

    public void update(Note note) {
        mDaoSession.getNoteDao().update(note);
    }

    public List<Note> getNotes() {
        return mDaoSession.getNoteDao().loadAll();
    }

    public void removeNote(Note note) {

        if (note.getId() != null) {
            mDaoSession.getNoteDao().delete(note);
        }
    }
}
