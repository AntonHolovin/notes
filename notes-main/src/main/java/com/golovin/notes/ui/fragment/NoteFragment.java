package com.golovin.notes.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.golovin.notes.R;
import com.golovin.notes.controller.EventManager;
import com.golovin.notes.controller.NotesApplication;
import com.golovin.notes.data.Note;
import com.golovin.notes.event.Event;
import com.golovin.notes.event.EventHandler;
import com.golovin.notes.log.Logger;
import com.golovin.notes.util.FontUtils;

public class NoteFragment extends Fragment implements EventHandler {

    public static final String NOTE = "note";
    public static final String INDEX = "index";

    private Note mNote;

    private int mIndex;

    private View.OnTouchListener mTouchListener;

    private EditText mEditText;

    public NoteFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();

        mNote = (Note) arguments.getSerializable(NOTE);
        mIndex = arguments.getInt(INDEX);
    }

    @Override
    public void onResume() {
        super.onResume();

        EventManager eventManager = NotesApplication.getInstance().getEventManager();
        eventManager.addHandler(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        EventManager eventManager = NotesApplication.getInstance().getEventManager();
        eventManager.removeHandler(this);
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
        mEditText = (EditText) view.findViewById(R.id.edit_data);

        FontUtils.applyFont(getActivity(), mEditText, FontUtils.CALIBRI_FONT);

        mEditText.setText(mNote.getContent());
        mEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                // nop
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // nop
            }

            @Override
            public void afterTextChanged(Editable editable) {

                String text = editable.toString();
                String oldContent = mNote.getContent();

                if (!text.equals(oldContent)) {
                    mNote.setContent(text);

                    if (!text.isEmpty()) {

                        fireTextEnteredEvent();

                        mEditText.requestFocus();
                    }
                }
            }

            private void fireTextEnteredEvent() {
                EventManager eventManager = NotesApplication.getInstance().getEventManager();

                Event event = new Event(Event.EventType.TEXT_ENTERED);
                event.putParam(Event.ACTION_KEY, mIndex);

                eventManager.fireEvent(event);
            }
        });
    }

    private void initIndex(View view) {
        TextView numberTextView = (TextView) view.findViewById(R.id.text_index);

        int viewNumber = mIndex + 1;

        numberTextView.setText(String.valueOf(viewNumber));
    }

    private void initSlider(View view) {

        view.findViewById(R.id.button_slider).setOnTouchListener(mTouchListener);
    }

    public void setTouchListener(View.OnTouchListener touchListener) {
        mTouchListener = touchListener;
    }

    @Override
    public void handlerEvent(Event event) {
        Event.EventType eventType = event.getEventType();

        Integer index = (Integer) event.getParam(Event.ACTION_KEY);

        switch (eventType) {
            case NOTE_SELECTED:

                if (mIndex == index) {

                    Logger.logDebug(NoteFragment.class, String.format("Note selected. Index = %d, " + mNote, mIndex));
                    mEditText.requestFocus();
                }

                break;
        }
    }
}