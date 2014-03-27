package com.notes.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import com.notes.fragments.NoteFragment;
import com.notes.models.NoteModel;

import java.util.ArrayList;
import java.util.List;

public class NotesPageAdapter extends FragmentStatePagerAdapter {

    public interface OnInnerNoteRemovedListener {
        public void onInnerNoteRemoved(int index);
    }

    public List<NoteModel> getNoteModels() {
        return mNoteModels;
    }

    private List<NoteModel> mNoteModels = new ArrayList<NoteModel>();

    private View.OnTouchListener mSliderListener;

    private OnInnerNoteRemovedListener mInnerNoteRemovedListener;

    public NotesPageAdapter(FragmentManager fm, List<NoteModel> noteModels) {
        super(fm);

        mNoteModels = noteModels;

        if (mNoteModels.size() == 0) {
            addNewNote();
        }
    }

    private void addNewNote() {
        NoteModel noteModel = new NoteModel(mNoteModels.size());
        mNoteModels.add(noteModel);
    }

    public void setInnerNoteRemovedListener(OnInnerNoteRemovedListener listener) {
        this.mInnerNoteRemovedListener = listener;
    }

    public void setSliderListener(View.OnTouchListener sliderListener) {
        this.mSliderListener = sliderListener;
    }

    public void removeNote(int noteIndex) {
        mNoteModels.remove(noteIndex);

        for (int i = noteIndex; i < mNoteModels.size(); i++) {
            NoteModel noteModel = mNoteModels.get(i);
            noteModel.decrementIndex();
        }

        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public Fragment getItem(int index) {
        NoteModel noteModel = mNoteModels.get(index);

        return buildFragment(noteModel);
    }

    private NoteFragment buildFragment(NoteModel noteModel) {
        NoteFragment fragment = NoteFragment.newInstance(noteModel);

        fragment.setOnTextEntered(new NoteFragment.OnTextTypedListener() {
            @Override
            public void onTextEntered(int index) {
                int lastIndex = mNoteModels.size() - 1;

                if (index == lastIndex) { // add new note to the end
                    addNewNote();
                    notifyDataSetChanged();
                }
            }
        });

        fragment.setOnTextRemoved(new NoteFragment.OnTextRemovedListener() {
            @Override
            public void onTextRemoved(int index) {
                int lastIndex = mNoteModels.size() - 1;

                if (mNoteModels.size() > 1 && index != lastIndex) {

                    // empty note removing
                    if (index == lastIndex - 1) { // is penultimate
                        removeNote(lastIndex);
                    } else {

                        if (mInnerNoteRemovedListener == null) {
                            throw new IllegalStateException("You should call setInnerNoteRemovedListener()");
                        }

                        mInnerNoteRemovedListener.onInnerNoteRemoved(index);
                    }
                }
            }
        });

        fragment.setSliderListener(mSliderListener);

        return fragment;
    }

    @Override
    public int getCount() {
        return mNoteModels.size();
    }
}