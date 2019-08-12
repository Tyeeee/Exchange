package com.hynet.heebit.components.widget.spinner.listener.implement;

import android.text.Spannable;
import android.text.SpannableString;

import com.hynet.heebit.components.widget.spinner.listener.OnTextFormatListener;

public class DefaultSpinnerTextFormat implements OnTextFormatListener {

    @Override
    public Spannable onTextFormat(String text) {
        return new SpannableString(text);
    }
}
