package com.golovin.notes.controller;

import android.content.Context;
import com.golovin.notes.data.DaoMaster;
import com.golovin.notes.data.DaoSession;
import com.golovin.notes.data.Note;
import com.golovin.notes.model.NoteModel;

import java.util.ArrayList;
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

    public List<NoteModel> getNoteModels() {
        List<Note> notes = mDaoSession.getNoteDao().loadAll();

        List<NoteModel> result = new ArrayList<NoteModel>();

        for (Note note : notes) {
            NoteModel noteModel = NoteModel.fromDbObject(note);

            result.add(noteModel);
        }

        return result;
    }

}
