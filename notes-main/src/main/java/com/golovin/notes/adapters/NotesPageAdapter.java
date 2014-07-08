package com.golovin.notes.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import com.golovin.notes.fragments.NoteFragment;
import com.golovin.notes.models.NoteModel;

import java.util.ArrayList;
import java.util.List;

public class NotesPageAdapter extends FragmentStatePagerAdapter {

    private Context mContext;

    private List<NoteModel> mNoteModels = new ArrayList<NoteModel>();

    private View.OnTouchListener mSliderTouchListener;

    public NotesPageAdapter(FragmentManager fm, List<NoteModel> noteModels, Context context,
                            View.OnTouchListener sliderTouchListener) {
        super(fm);

        mContext = context;
        mSliderTouchListener = sliderTouchListener;

        mNoteModels = noteModels;

        if (mNoteModels.isEmpty()) {
            noteModels.add(new NoteModel());
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public Fragment getItem(int index) {
        NoteModel noteModel = mNoteModels.get(index);

        return buildFragment(noteModel, index);
    }

    private NoteFragment buildFragment(NoteModel noteModel, int index) {

        Bundle bundle = new Bundle();

        bundle.putSerializable(NoteFragment.NOTE_MODEL, noteModel);
        bundle.putInt(NoteFragment.NUMBER, index + 1);

        NoteFragment noteFragment = (NoteFragment) Fragment.instantiate(mContext, NoteFragment.class.getCanonicalName(),
                bundle);

        noteFragment.setTouchListener(mSliderTouchListener);

        return noteFragment;
    }

    @Override
    public int getCount() {
        return mNoteModels.size();
    }
}