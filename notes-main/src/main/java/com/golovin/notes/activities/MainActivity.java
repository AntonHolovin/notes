package com.golovin.notes.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.RelativeLayout;
import com.golovin.notes.R;
import com.golovin.notes.adapters.NotesPageAdapter;
import com.golovin.notes.animation.TopMarginEvaluator;
import com.golovin.notes.application.NotesApplication;
import com.golovin.notes.data.NotesDataSource;
import com.golovin.notes.models.NoteModel;
import com.nineoldandroids.animation.ValueAnimator;

import java.util.List;

public class MainActivity extends FragmentActivity {

    private ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initNotesViewPager();
    }

    private void initNotesViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager_notes);

//        initViewPagerAppearance();
        initViewPagerAdapter();
    }

    private View.OnTouchListener buildSliderTouchListener() {
        return new View.OnTouchListener() {
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

                        int openedMargin = getResources().getDimensionPixelSize(R.dimen.note_opened_top_margin);

                        int finishMargin = directionDown ? openedMargin : 0;

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
        // show previous and next pages

        float visiblePartMargin = getResources().getDimensionPixelSize(R.dimen.note_visible_part_margin);

        mViewPager.setPageMargin((int) -visiblePartMargin);
    }

    private void initViewPagerAdapter() {
        NotesApplication notesApplication = NotesApplication.getInstance();

        NotesDataSource notesDataSource = notesApplication.getNotesDataSource();

        List<NoteModel> noteModels = notesDataSource.getNoteModels();

        View.OnTouchListener onTouchListener = buildSliderTouchListener();

        NotesPageAdapter pageAdapter = new NotesPageAdapter(getSupportFragmentManager(), noteModels, this,
                onTouchListener);

        mViewPager.setAdapter(pageAdapter);

        pageAdapter.notifyDataSetChanged();
    }
}