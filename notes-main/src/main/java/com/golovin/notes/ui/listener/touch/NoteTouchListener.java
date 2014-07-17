package com.golovin.notes.ui.listener.touch;

import android.content.res.Resources;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.RelativeLayout;
import com.golovin.notes.R;
import com.golovin.notes.ui.animation.TopMarginEvaluator;
import com.nineoldandroids.animation.ValueAnimator;


public class NoteTouchListener implements View.OnTouchListener {

    private ViewPager mViewPager;

    private Resources mResources;

    private float mCurrentTopMargin = 0;

    private float mLastY = 0;

    private boolean mDirectionDown = false;

    public NoteTouchListener(Resources resources, ViewPager viewPager) {
        mResources = resources;
        mViewPager = viewPager;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mViewPager.getLayoutParams();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
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

                int openedMargin = mResources.getDimensionPixelSize(R.dimen.note_opened_top_margin);

                int finishMargin = mDirectionDown ? openedMargin : 0;

                int startMargin = params.topMargin;

                int duration = mResources.getInteger(R.integer.note_animation_swipe_duration);

                ValueAnimator animation = ValueAnimator.ofObject(new TopMarginEvaluator(mViewPager),
                        startMargin, finishMargin).setDuration(duration);

                float overshoot = getAnimationOvershootValue();

                animation.setInterpolator(new OvershootInterpolator(overshoot));
                animation.start();

                break;
        }

        return true;
    }

    private float getAnimationOvershootValue() {
        TypedValue outValue = new TypedValue();
        mResources.getValue(R.dimen.note_animation_overshoot, outValue, true);
        return outValue.getFloat();
    }
}
