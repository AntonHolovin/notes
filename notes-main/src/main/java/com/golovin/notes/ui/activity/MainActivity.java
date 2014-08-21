package com.golovin.notes.ui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import com.golovin.notes.R;
import com.golovin.notes.controller.DataSourceManager;
import com.golovin.notes.controller.EventManager;
import com.golovin.notes.controller.NotesApplication;
import com.golovin.notes.data.Note;
import com.golovin.notes.event.Event;
import com.golovin.notes.event.EventHandler;
import com.golovin.notes.helper.FontHelper;
import com.golovin.notes.helper.ShareHelper;
import com.golovin.notes.log.Logger;
import com.golovin.notes.ui.adapter.NotesPageAdapter;
import com.golovin.notes.ui.listener.touch.NoteTouchListener;

import java.util.List;

public class MainActivity extends FragmentActivity implements EventHandler {

    public static final String SAVED_POSITION = "savedPosition";
    private ViewPager mViewPager;

    private NoteTouchListener mNoteTouchListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initShareButton();
        initNotesViewPager();
        restoreViewPagerPosition();
    }

    private void restoreViewPagerPosition() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        int savedPosition = preferences.getInt(SAVED_POSITION, 0);

        mViewPager.setCurrentItem(savedPosition);
    }

    private void initShareButton() {
        Button shareButton = (Button) findViewById(R.id.button_share);
        FontHelper.applyFont(this, shareButton, FontHelper.FONT_ROBOTO_CONDENSED_REGULAR);

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentItem = mViewPager.getCurrentItem();

                NotesPageAdapter adapter = (NotesPageAdapter) mViewPager.getAdapter();
                Note note = adapter.getNote(currentItem);

                ShareHelper.byEmail(MainActivity.this, note.getContent());
            }
        });
    }

    private void initNotesViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager_notes);

        mViewPager.setOffscreenPageLimit(2);

        mViewPager.setOnPageChangeListener(buildViewPagerChangeListener());

        initViewPagerAppearance();
        initViewPagerAdapter();
    }

    private ViewPager.SimpleOnPageChangeListener buildViewPagerChangeListener() {
        return new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {

                fireNoteSelectedEvent(position);

                NotesPageAdapter adapter = (NotesPageAdapter) mViewPager.getAdapter();

                int size = adapter.getSize();

                if (size > 2) {

                    if (position == 0) { // first note

                        checkRightNote(position, adapter);

                    } else if (isNoteLast(position, adapter)) { // last note

                        checkLeftNote(position, adapter);

                    } else { // inner note

                        checkRightNote(position, adapter);

                        checkLeftNote(position, adapter);
                    }
                }
            }

            private boolean isNoteLast(int position, NotesPageAdapter adapter) {
                return position == adapter.getSize() - 2 || position == adapter.getSize() - 1;
            }

            private void fireNoteSelectedEvent(int position) {
                Event event = new Event(Event.EventType.NOTE_SELECTED);
                event.putParam(Event.ACTION_KEY, position);

                EventManager eventManager = NotesApplication.getInstance().getEventManager();
                eventManager.fireEvent(event);
            }

            private void checkLeftNote(int position, NotesPageAdapter adapter) {
                int leftPosition = position - 1;
                checkNote(leftPosition, adapter, false);
            }

            private void checkRightNote(int position, NotesPageAdapter adapter) {
                int rightPosition = position + 1;
                checkNote(rightPosition, adapter, true);
            }

            private void checkNote(int position, NotesPageAdapter adapter, boolean isRight) {
                Note note = adapter.getNote(position);
                String content = note.getContent();

                if (content == null || content.isEmpty()) {

                    Logger.logDebug(MainActivity.class, String.format("Note will be removed. Index = %d, " + note,
                            position));

                    adapter.removeNote(position);

                    DataSourceManager dataSource = NotesApplication.getInstance().getDataSourceManager();
                    dataSource.removeNote(note);

                    List<Note> notes = adapter.getNotes();

                    NotesPageAdapter newAdapter = new NotesPageAdapter(getSupportFragmentManager(), notes,
                            MainActivity.this, mNoteTouchListener, false);

                    mViewPager.setAdapter(newAdapter);

                    newAdapter.notifyDataSetChanged();

                    int movePosition = isRight ? position - 1 : position;
                    mViewPager.setCurrentItem(movePosition, false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        saveCurrentNote();
                        break;
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        EventManager eventManager = NotesApplication.getInstance().getEventManager();
        eventManager.addHandler(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        saveCurrentNote();
        saveCurrentPosition();

        EventManager eventManager = NotesApplication.getInstance().getEventManager();
        eventManager.removeHandler(this);
    }

    private void saveCurrentPosition() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();

        edit.putInt(SAVED_POSITION, mViewPager.getCurrentItem());
        edit.commit();
    }

    private void saveCurrentNote() {
        NotesPageAdapter adapter = (NotesPageAdapter) mViewPager.getAdapter();
        Note note = adapter.getNote(mViewPager.getCurrentItem());

        DataSourceManager sourceManager = NotesApplication.getInstance().getDataSourceManager();

        String content = note.getContent();

        if (content != null && !content.isEmpty()) {
            if (note.getId() == null) {

                long id = sourceManager.insert(note);
                note.setId(id);

            } else {

                sourceManager.update(note);
            }
        }
    }

    private void initViewPagerAppearance() {
        float visiblePartMargin = getResources().getDimensionPixelSize(R.dimen.note_visible_part_margin);
        mViewPager.setPageMargin((int) -visiblePartMargin);
    }

    private void initViewPagerAdapter() {
        DataSourceManager sourceManager = NotesApplication.getInstance().getDataSourceManager();

        List<Note> notes = sourceManager.getNotes();

        mNoteTouchListener = new NoteTouchListener(getResources(), mViewPager);

        NotesPageAdapter pageAdapter = new NotesPageAdapter(getSupportFragmentManager(), notes, this,
                mNoteTouchListener, true);

        mViewPager.setAdapter(pageAdapter);
        pageAdapter.notifyDataSetChanged();
    }

    @Override
    public void handlerEvent(Event event) {

        switch (event.getEventType()) {
            case TEXT_ENTERED:
                NotesPageAdapter adapter = (NotesPageAdapter) mViewPager.getAdapter();

                int noteIndex = (Integer) event.getParam(Event.ACTION_KEY);

                if (noteIndex == adapter.getSize() - 1) {
                    adapter.addNote(new Note());
                    adapter.notifyDataSetChanged();
                }

                break;
        }
    }
}