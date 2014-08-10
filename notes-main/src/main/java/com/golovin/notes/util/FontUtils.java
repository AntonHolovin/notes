package com.golovin.notes.util;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class FontUtils {

    public static final String CALIBRI_FONT = "calibri";

    private static final String FONT_PATH_PATTERN = "fonts/%s.ttf";

    private static Map<String, Typeface> sCache = new HashMap<String, Typeface>();

    public static void applyFont(Context context, TextView textView, String fontName) {
        Typeface typeface = get(context, fontName);

        textView.setTypeface(typeface);
    }

    private static Typeface get(Context context, String name) {

        if (!sCache.containsKey(name)) {
            Typeface t = Typeface.createFromAsset(context.getAssets(), String.format(FONT_PATH_PATTERN, name));
            sCache.put(name, t);
        }

        return sCache.get(name);
    }
}
