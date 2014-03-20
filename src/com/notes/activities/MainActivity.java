package com.notes.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.RelativeLayout;
import com.nineoldandroids.animation.ValueAnimator;
import com.notes.R;
import com.notes.adapters.NotesPageAdapter;
import com.notes.animation.TopMarginEvaluator;
import com.notes.database.NotesDataSource;
import com.notes.models.NoteModel;
import com.notes.utils.MetricUtils;
import com.notes.utils.ShareUtils;

import java.util.List;

public class MainActivity extends FragmentActivity {

    private ViewPager mViewPager;
    private NotesDataSource mNotesDataSource;
    private View.OnTouchListener mSliderTouchListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initSliderTouchListener();
        initDataSource();
        initNotesViewPager();
        initEmailButton();
    }

    private void initEmailButton() {
        findViewById(R.id.button_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotesPageAdapter adapter = (NotesPageAdapter) mViewPager.getAdapter();
                int index = mViewPager.getCurrentItem();
                List<NoteModel> noteModels = adapter.getNoteModels();

                NoteModel noteModel = noteModels.get(index);
                ShareUtils.byEmail(MainActivity.this, noteModel.getContent());
            }
        });
    }

    private void initSliderTouchListener() {
        mSliderTouchListener = new View.OnTouchListener() {
            float yStart = 0;

            float currentTopMargin = 0;

            float yLast = 0;

            boolean directionDown = false;

            @Override
            public boolean onTouch(View view, MotionEvent event) {

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mViewPager.getLayoutParams();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        yStart = event.getRawY();
                        yLast = event.getRawY();
                        currentTopMargin = params.topMargin;
                        break;

                    case MotionEvent.ACTION_MOVE:

                        int sliderHalfWidth = mViewPager.findViewById(R.id.button_slider).getWidth() / 2;

                        float difference = event.getRawY() - currentTopMargin;
                        params.topMargin = (int) (currentTopMargin + difference - sliderHalfWidth);
                        mViewPager.setLayoutParams(params);

                        directionDown = event.getRawY() > yLast;

                        yLast = event.getRawY();
                        break;

                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:

                        final float OPENED_MARGIN = 80f;
                        int finishMargin = directionDown ? (int) MetricUtils.calculatePx(MainActivity.this,
                                OPENED_MARGIN) : 0;

                        int startTopMargin = params.topMargin;
                        ValueAnimator animation = ValueAnimator.ofObject(new TopMarginEvaluator(mViewPager),
                                startTopMargin, finishMargin).setDuration(200);

                        animation.setInterpolator(new OvershootInterpolator(1));
                        animation.start();

                        break;
                }

                return true;
            }
        };
    }

    private void initDataSource() {
        mNotesDataSource = new NotesDataSource(this);
    }

    private void initNotesViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager_notes);

        initViewPagerAppearance();
        initViewPagerAdapter();
    }

    private void initViewPagerAdapter() {

        mNotesDataSource.open();

        List<NoteModel> notes = mNotesDataSource.getAllNotes();
        mNotesDataSource.close();

        final NotesPageAdapter adapter = new NotesPageAdapter(getSupportFragmentManager(), notes);

        adapter.setInnerNoteRemovedListener(new NotesPageAdapter.OnInnerNoteRemovedListener() {
            @Override
            public void onInnerNoteRemoved(final int index) {
                adapter.removeNote(index);

                /**
                 * Ugly implementation. Of course new adapter is the strange solution.
                 * But I had problems with notifyDataSetChanged(). It doesn't work correctly :(
                 */
                NotesPageAdapter newAdapter = new NotesPageAdapter(getSupportFragmentManager(),
                        adapter.getNoteModels());

                newAdapter.setInnerNoteRemovedListener(this);
                newAdapter.setSliderListener(mSliderTouchListener);

                mViewPager.setAdapter(newAdapter);
                newAdapter.notifyDataSetChanged();

                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mViewPager.setCurrentItem(index, true);
                    }
                }, 100);
            }
        });

        adapter.setSliderListener(mSliderTouchListener);

        mViewPager.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NotesPageAdapter adapter = (NotesPageAdapter) mViewPager.getAdapter();

        // of course not the best solution
        mNotesDataSource.open();
        mNotesDataSource.deleteAllNotes();
        mNotesDataSource.insertNotes(adapter.getNoteModels());
        mNotesDataSource.close();
    }

    private void initViewPagerAppearance() {
        // show previous and next pages
        final int DIP_MARGIN = 25;
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DIP_MARGIN, getResources()
                .getDisplayMetrics());
        mViewPager.setPageMargin(-margin);
    }
}