package com.notes.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class CustomEditText extends EditText {

    public interface OnTypingFinishedListener {
        public void onTypingFinished();
    }

    public void setOnTypingFinished(OnTypingFinishedListener listener) {
        this.mOnTypingFinishedListener = listener;
    }

    private OnTypingFinishedListener mOnTypingFinishedListener;

    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Used for back button click event when EditText has focus
     */
    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            return false;
        }

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (mOnTypingFinishedListener == null) {
                throw new IllegalStateException("You should call setOnTypingFinished()");
            }

            mOnTypingFinishedListener.onTypingFinished();
        }

        return false;
    }
}
