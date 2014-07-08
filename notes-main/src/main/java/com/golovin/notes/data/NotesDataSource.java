package com.golovin.notes.data;

import com.golovin.notes.models.NoteModel;

import java.util.ArrayList;
import java.util.List;

public class NotesDataSource {

    private DaoSession mDaoSession;

    public NotesDataSource(DaoSession daoSession) {
        mDaoSession = daoSession;
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

    public void update(Note note) {
        mDaoSession.getNoteDao().update(note);
    }

    public void remove(Note note) {
        mDaoSession.getNoteDao().delete(note);
    }

    public void insert(Note note) {
        mDaoSession.getNoteDao().insert(note);
    }
}
