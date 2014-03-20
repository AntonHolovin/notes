package com.notes.animation;

import android.view.View;
import android.widget.RelativeLayout;
import com.nineoldandroids.animation.IntEvaluator;

public class TopMarginEvaluator extends IntEvaluator {

    private View mView;

    public TopMarginEvaluator(View view) {
        this.mView = view;
    }

    @Override
    public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mView.getLayoutParams();
        params.topMargin = super.evaluate(fraction, startValue, endValue);
        mView.setLayoutParams(params);

        return params.topMargin;
    }
}
