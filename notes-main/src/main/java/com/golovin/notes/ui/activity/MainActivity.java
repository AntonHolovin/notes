package com.golovin.notes.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.RelativeLayout;
import com.golovin.notes.R;
import com.golovin.notes.controller.DataSourceManager;
import com.golovin.notes.controller.EventManager;
import com.golovin.notes.controller.NotesApplication;
import com.golovin.notes.data.Note;
import com.golovin.notes.event.Event;
import com.golovin.notes.event.EventHandler;
import com.golovin.notes.ui.adapter.NotesPageAdapter;
import com.golovin.notes.ui.animation.TopMarginEvaluator;
import com.nineoldandroids.animation.ValueAnimator;

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

    private View.OnTouchListener buildSliderTouchListener() {
        return new View.OnTouchListener() {
            float mStartY = 0;

            float mCurrentTopMargin = 0;

            float mLastY = 0;

            boolean mDirectionDown = false;

            @Override
            public boolean onTouch(View view, MotionEvent event) {

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mViewPager.getLayoutParams();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mStartY = event.getRawY();
                        mLastY = event.getRawY();
                        mCurrentTopMargin = params.topMargin;
                        break;

                    case MotionEvent.ACTION_MOVE:

                        int sliderHalfWidth = mViewPager.findViewById(R.id.button_slider).getWidth() / 2;

                        float difference = event.getRawY() - mCurrentTopMargin;
                        params.topMargin = (int) (mCurrentTopMargin + difference - sliderHalfWidth);
                        mViewPager.setLayoutParams(params);

                        mDirectionDown = event.getRawY() > mLastY;

                        mLastY = event.getRawY();
                        break;

                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:

                        int openedMargin = getResources().getDimensionPixelSize(R.dimen.note_opened_top_margin);

                        int finishMargin = mDirectionDown ? openedMargin : 0;

                        int startMargin = params.topMargin;

                        ValueAnimator animation = ValueAnimator.ofObject(new TopMarginEvaluator(mViewPager),
                                startMargin, finishMargin).setDuration(200);

                        animation.setInterpolator(new OvershootInterpolator(1));
                        animation.start();

                        break;
                }

                return true;
            }
        };
    }

    private void initViewPagerAppearance() {
        float visiblePartMargin = getResources().getDimensionPixelSize(R.dimen.note_visible_part_margin);
        mViewPager.setPageMargin((int) -visiblePartMargin);
    }

    private void initViewPagerAdapter() {
        NotesApplication notesApplication = NotesApplication.getInstance();
        DataSourceManager sourceManager = notesApplication.getDataSourceManager();

        List<Note> notes = sourceManager.getNotes();

        View.OnTouchListener onTouchListener = buildSliderTouchListener();

        NotesPageAdapter pageAdapter = new NotesPageAdapter(getSupportFragmentManager(), notes, this,
                onTouchListener);

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