package com.notes.utils;

import android.content.Context;

public class MetricUtils {

    public static float calculatePx(Context context, float dp) {
        float densityDpi = context.getResources().getDisplayMetrics().densityDpi;

        return dp * (densityDpi / 160);
    }
}
