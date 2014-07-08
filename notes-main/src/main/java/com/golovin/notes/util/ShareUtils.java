package com.golovin.notes.util;

import android.content.Context;
import android.content.Intent;
import com.golovin.notes.R;

public class ShareUtils {
    public static void byEmail(Context context, String text) {
        Intent intent = new Intent();

        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setType("text/plain");

        context.startActivity(Intent.createChooser(intent, context.getResources().getText(R.string.share)));
    }
}
