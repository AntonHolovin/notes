package com.notes.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.notes.R;
import com.notes.controls.CustomEditText;
import com.notes.models.NoteModel;

public class NoteFragment extends Fragment {

    private static final String NOTE_MODEL = "noteModel";

    private NoteModel mNoteModel;

    private View.OnTouchListener mSliderListener;

    public interface OnTextTypedListener {

        public void onTextEntered(int index);
    }

    public interface OnTextRemovedListener {

        public void onTextRemoved(int index);
    }

    private OnTextTypedListener mOnTextEnteredListener;

    private OnTextRemovedListener mOnTextRemovedListener;

    private NoteFragment() {
    }

    public static NoteFragment newInstance(NoteModel noteModel) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(NOTE_MODEL, noteModel);

        NoteFragment fragment = new NoteFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNoteModel = (NoteModel) getArguments().getSerializable(NOTE_MODEL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, null);

        initContent(view);
        initIndex(view);
        initSlider(view);

        return view;
    }

    private void initSlider(View view) {

        if (mSliderListener == null) {
            throw new IllegalArgumentException("You should call setSliderListener()");
        }

        view.findViewById(R.id.button_slider).setOnTouchListener(mSliderListener);
    }

    private void initIndex(View view) {
        TextView indexTextView = (TextView) view.findViewById(R.id.text_index);

        int noteNumber = mNoteModel.getIndex() + 1;
        indexTextView.setText(String.valueOf(noteNumber));
    }

    private void initContent(View view) {
        final CustomEditText contentEditText = (CustomEditText) view.findViewById(R.id.edit_data);

        contentEditText.setOnTypingFinished(new CustomEditText.OnTypingFinishedListener() {
            @Override
            public void onTypingFinished() {
                String content = contentEditText.getText().toString();

                mNoteModel.setContent(content);

                if (content.isEmpty()) {
                    if (mOnTextRemovedListener == null) {
                        throw new IllegalStateException("You should call setOnTextRemoved()");
                    }

                    mOnTextRemovedListener.onTextRemoved(mNoteModel.getIndex());
                } else {
                    if (mOnTextEnteredListener == null) {
                        throw new IllegalStateException("You should call setOnTextEntered()");
                    }

                    mOnTextEnteredListener.onTextEntered(mNoteModel.getIndex());
                }
            }
        });

        contentEditText.setText(mNoteModel.getContent());
    }

    public void setSliderListener(View.OnTouchListener sliderListener) {
        this.mSliderListener = sliderListener;
    }

    public void setOnTextEntered(OnTextTypedListener listener) {
        this.mOnTextEnteredListener = listener;
    }

    public void setOnTextRemoved(OnTextRemovedListener listener) {
        this.mOnTextRemovedListener = listener;
    }
}