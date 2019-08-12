package com.hynet.heebit.components.widget.actionsheet;

import com.hynet.heebit.components.widget.actionsheet.listener.OnSheetItemListener;

import androidx.annotation.ColorInt;

public class ActionSheetHeader implements OnSheetItemListener {

    private String title;
    @ColorInt
    private int textColor;

    public ActionSheetHeader(String title, @ColorInt int color) {
        this.title = title;
        textColor = color;
    }

    @ColorInt
    public int getTextColor() {
        return textColor;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
