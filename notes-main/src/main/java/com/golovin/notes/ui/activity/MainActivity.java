package com.golovin.notes.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import com.golovin.notes.R;
import com.golovin.notes.controller.DataSourceManager;
import com.golovin.notes.controller.EventManager;
import com.golovin.notes.controller.NotesApplication;
import com.golovin.notes.data.Note;
import com.golovin.notes.event.Event;
import com.golovin.notes.event.EventHandler;
import com.golovin.notes.log.Logger;
import com.golovin.notes.ui.adapter.NotesPageAdapter;
import com.golovin.notes.ui.listener.touch.NoteTouchListener;

import java.util.List;

public class MainActivity extends FragmentActivity implements EventHandler {

    private ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initNotesViewPager();
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
                NotesPageAdapter adapter = (NotesPageAdapter) mViewPager.getAdapter();

                if (adapter.getSize() >= 2) {

                    if (position == 0) {

                        checkRightNote(position, adapter);

                    } else if (position == adapter.getSize() - 1) {

                        checkLeftNote(position, adapter);

                    } else {

                        if (position != adapter.getSize() - 2) {
                            checkRightNote(position, adapter);
                        }

                        checkLeftNote(position, adapter);
                    }
                }
            }

            private void checkLeftNote(int position, NotesPageAdapter adapter) {
                int leftPosition = position - 1;
                checkNote(leftPosition, adapter);
            }

            private void checkRightNote(int position, NotesPageAdapter adapter) {
                int rightPosition = position + 1;
                checkNote(rightPosition, adapter);
            }

            private void checkNote(int position, NotesPageAdapter adapter) {
                Note note = adapter.getNote(position);

                String content = note.getContent();

                if (content == null || content.isEmpty()) {
                    adapter.removeNote(position);
                    adapter.notifyDataSetChanged();

                    Logger.logDebug(MainActivity.class, String.format("Removing %d note", position));
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

        EventManager eventManager = NotesApplication.getInstance().getEventManager();
        eventManager.removeHandler(this);
    }

    private void saveCurrentNote() {
        NotesPageAdapter adapter = (NotesPageAdapter) mViewPager.getAdapter();
        Note note = adapter.getNote(mViewPager.getCurrentItem());

        DataSourceManager sourceManager = NotesApplication.getInstance().getDataSourceManager();

        if (note.getId() == null) {

            long id = sourceManager.insert(note);
            note.setId(id);

        } else {

            sourceManager.update(note);
        }
    }

    private void initViewPagerAppearance() {
        float visiblePartMargin = getResources().getDimensionPixelSize(R.dimen.note_visible_part_margin);
        mViewPager.setPageMargin((int) -visiblePartMargin);
    }

    private void initViewPagerAdapter() {
        NotesApplication notesApplication = NotesApplication.getInstance();
        DataSourceManager sourceManager = notesApplication.getDataSourceManager();

        List<Note> notes = sourceManager.getNotes();

        NoteTouchListener noteTouchListener = new NoteTouchListener(getResources(), mViewPager);

        NotesPageAdapter pageAdapter = new NotesPageAdapter(getSupportFragmentManager(), notes, this,
                noteTouchListener);

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