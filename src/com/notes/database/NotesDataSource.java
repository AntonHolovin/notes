package com.notes.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.notes.models.NoteModel;

import java.util.ArrayList;
import java.util.List;

public class NotesDataSource {

    private DbHelper mDbHelper;
    private SQLiteDatabase mDatabase;

    public NotesDataSource(Context context) {
        mDbHelper = new DbHelper(context);
    }

    public void open() {
        mDatabase = mDbHelper.getWritableDatabase();
    }

    public void close() {
        mDbHelper.close();
    }

    public void deleteAllNotes() {
        mDatabase.delete(DbHelper.TABLE_NOTES, null, null);
    }

    public void insertNotes(List<NoteModel> noteModels) {
        for (NoteModel noteModel : noteModels) {
            insertNote(noteModel);
        }
    }

    private void insertNote(NoteModel model) {
        ContentValues values = new ContentValues();

        values.put(DbHelper.COLUMN_CONTENT, model.getContent());
        values.put(DbHelper.COLUMN_INDEX, model.getIndex());

        mDatabase.insert(DbHelper.TABLE_NOTES, null, values);
    }

    public List<NoteModel> getAllNotes() {
        List<NoteModel> models = new ArrayList<NoteModel>();

        Cursor cursor = mDatabase.query(DbHelper.TABLE_NOTES, new String[]{DbHelper.COLUMN_ID, DbHelper.COLUMN_CONTENT,
                DbHelper.COLUMN_INDEX}, null, null, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            NoteModel noteModel = toNote(cursor);

            models.add(noteModel);
            cursor.moveToNext();
        }
        cursor.close();

        return models;
    }

    private NoteModel toNote(Cursor cursor) {

        int columnIndex = cursor.getColumnIndex(DbHelper.COLUMN_CONTENT);
        String content = cursor.getString(columnIndex);

        columnIndex = cursor.getColumnIndex(DbHelper.COLUMN_INDEX);
        int index = cursor.getInt(columnIndex);

        return new NoteModel(content, index);
    }
}
