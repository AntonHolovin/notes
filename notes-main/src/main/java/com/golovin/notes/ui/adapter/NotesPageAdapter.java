package com.golovin.notes.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import com.golovin.notes.data.Note;
import com.golovin.notes.ui.fragment.NoteFragment;

import java.util.ArrayList;
import java.util.List;

public class NotesPageAdapter extends FragmentStatePagerAdapter {

    private Context mContext;

    private List<Note> mNotes = new ArrayList<Note>();

    private View.OnTouchListener mSliderTouchListener;

    public NotesPageAdapter(FragmentManager fm, List<Note> notes, Context context,
                            View.OnTouchListener sliderTouchListener) {
        super(fm);

        mContext = context;
        mSliderTouchListener = sliderTouchListener;

        mNotes = notes;

        mNotes.add(new Note());
    }

    public void removeNote(int index) {
        mNotes.remove(index);
    }

    public void addNote(Note note) {
        mNotes.add(note);
    }

    public int getSize() {
        return mNotes.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    public Note getNote(int index) {
        return mNotes.get(index);
    }

    @Override
    public Fragment getItem(int index) {
        Note noteModel = mNotes.get(index);

        return buildFragment(noteModel, index);
    }

    private NoteFragment buildFragment(Note note, int index) {

        Bundle bundle = new Bundle();

        bundle.putSerializable(NoteFragment.NOTE, note);
        bundle.putInt(NoteFragment.INDEX, index);

        NoteFragment noteFragment = (NoteFragment) Fragment.instantiate(mContext, NoteFragment.class.getCanonicalName(),
                bundle);

        noteFragment.setTouchListener(mSliderTouchListener);

        return noteFragment;
    }

    @Override
    public int getCount() {
        return mNotes.size();
    }
}