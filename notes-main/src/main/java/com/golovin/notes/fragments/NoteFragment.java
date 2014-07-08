package com.golovin.notes.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.golovin.notes.R;
import com.golovin.notes.models.NoteModel;

public class NoteFragment extends Fragment {

    public static final String NOTE_MODEL = "noteModel";
    public static final String NUMBER = "index";

    private NoteModel mNoteModel;

    private int mNumber;

    private View.OnTouchListener mTouchListener;

    public NoteFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();

        mNoteModel = (NoteModel) arguments.getSerializable(NOTE_MODEL);
        mNumber = arguments.getInt(NUMBER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);

        initContent(view);
        initIndex(view);
        initSlider(view);

        return view;
    }

    private void initContent(View view) {
        final EditText contentEditText = (EditText) view.findViewById(R.id.edit_data);

        contentEditText.setText(mNoteModel.getContent());
    }

    private void initIndex(View view) {
        TextView numberTextView = (TextView) view.findViewById(R.id.text_index);

        numberTextView.setText(String.valueOf(mNumber));
    }

    private void initSlider(View view) {

        view.findViewById(R.id.button_slider).setOnTouchListener(mTouchListener);
    }

    public void setTouchListener(View.OnTouchListener touchListener) {
        mTouchListener = touchListener;
    }
}