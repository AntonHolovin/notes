package com.golovin.notes.model;

import com.golovin.notes.data.Note;

import java.io.Serializable;

public class NoteModel implements Serializable {

    private String mContent;

    private String mPhotoUri;

    public NoteModel() {
    }

    public NoteModel(String content, String photoUri) {
        mContent = content;
        mPhotoUri = photoUri;
    }

    public String getContent() {
        return mContent;
    }

    public String getPhotoUri() {
        return mPhotoUri;
    }

    public static NoteModel fromDbObject(Note note) {
        return new NoteModel(note.getContent(), note.getPhotoUri());
    }
}
