package com.notes.models;

import java.io.Serializable;

public class NoteModel implements Serializable {

    private String content;

    private int index;

    public NoteModel(int index) {
        this.index = index;
    }

    public NoteModel(String content, int index) {
        this.content = content;
        this.index = index;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void decrementIndex() {
        index--;
    }

    public String getContent() {
        return content;
    }

    public int getIndex() {
        return index;
    }
}
