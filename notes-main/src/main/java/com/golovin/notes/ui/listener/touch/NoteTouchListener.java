package com.golovin.notes.ui.listener.touch;

import android.content.res.Resources;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.RelativeLayout;
import com.golovin.notes.R;
import com.golovin.notes.log.Logger;
import com.golovin.notes.ui.animation.TopMarginEvaluator;
import com.nineoldandroids.animation.ValueAnimator;

import java.util.Calendar;


public class NoteTouchListener implements View.OnTouchListener {

    private ViewPager mViewPager;

    private Resources mResources;

    private int mTouchDiff;

    private float mLastY;

    private boolean mIsGoingDown;

    private boolean mIsOpened;

    private long mStartClickTime;

    final int MAX_CLICK_DURATION = 200;

    public NoteTouchListener(Resources resources, ViewPager viewPager) {
        mResources = resources;
        mViewPager = viewPager;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mViewPager.getLayoutParams();

        float rawY = event.getRawY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchDiff = (int) (rawY - params.topMargin);
                mStartClickTime = Calendar.getInstance().getTimeInMillis();

                mLastY = rawY;
                break;

            case MotionEvent.ACTION_MOVE:

                params.topMargin = (int) rawY - mTouchDiff;

                mViewPager.setLayoutParams(params);

                mIsGoingDown = rawY > mLastY;

                mLastY = rawY;
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:

                int duration = mResources.getInteger(R.integer.note_animation_swipe_duration);
                int startMargin = params.topMargin;

                int openedMargin = mResources.getDimensionPixelSize(R.dimen.note_opened_top_margin);

                long clickDuration = Calendar.getInstance().getTimeInMillis() - mStartClickTime;

                if (clickDuration < MAX_CLICK_DURATION) { // click event

                    Logger.logDebug(NoteTouchListener.class, String.format("Click event. Is opened: %b", mIsOpened));

                    int finishMargin = !mIsOpened ? openedMargin : 0;

                    ValueAnimator animation = ValueAnimator.ofObject(new TopMarginEvaluator(mViewPager),
                            startMargin, finishMargin).setDuration(duration);

                    float overshoot = getAnimationOvershootValue();

                    animation.setInterpolator(new OvershootInterpolator(overshoot));
                    animation.start();

                    mIsOpened = !mIsOpened;

                } else {

                    int finishMargin = mIsGoingDown ? openedMargin : 0;

                    ValueAnimator animation = ValueAnimator.ofObject(new TopMarginEvaluator(mViewPager),
                            startMargin, finishMargin).setDuration(duration);

                    float overshoot = getAnimationOvershootValue();

                    animation.setInterpolator(new OvershootInterpolator(overshoot));
                    animation.start();

                    mIsOpened = mIsGoingDown;
                }

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
