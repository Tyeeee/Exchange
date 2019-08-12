package com.hynet.heebit.components.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class DrawableTextView extends AppCompatTextView {

    public DrawableTextView(Context context) {
        super(context, null);
    }

    public DrawableTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawableTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable[] drawables = getCompoundDrawables();
        Drawable leftDrawable = drawables[0];
        if (leftDrawable != null) {
            int leftDrawableWidth = leftDrawable.getIntrinsicWidth();
            int drawablePadding = getCompoundDrawablePadding();
            int textWidth = (int) getPaint().measureText(String.valueOf(getText()).trim());
            canvas.save();
            canvas.translate((getWidth() - (leftDrawableWidth + drawablePadding + textWidth)) / 2, 0);
        }
        super.onDraw(canvas);
    }
}
