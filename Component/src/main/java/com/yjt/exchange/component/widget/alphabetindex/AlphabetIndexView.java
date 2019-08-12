package com.hynet.heebit.components.widget.alphabetindex;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.hynet.heebit.components.utils.LogUtil;
import com.hynet.heebit.components.widget.alphabetindex.listener.OnAlphabetIndexToggleListener;

import java.util.List;

public class AlphabetIndexView extends View {

    private Paint paint;
    private int textSpan;
    private OnAlphabetIndexToggleListener onAlphabetIndexToggleListener;
    private List<String> indexs;
    private int textSize = 40;
    private int selTextColor = Color.BLACK;
    private int norTextColor = Color.GRAY;
    private float yAxis;
    private int currentPosition;

    public AlphabetIndexView(Context context) {
        this(context, null);
    }

    public AlphabetIndexView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlphabetIndexView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public void setOnAlphabetIndexToggleListener(OnAlphabetIndexToggleListener onAlphabetIndexToggleListener) {
        this.onAlphabetIndexToggleListener = onAlphabetIndexToggleListener;
    }

    public void setIndexs(List<String> indexs) {
         LogUtil.Companion.getInstance().print("indexs:" + indexs);
        this.indexs = indexs;
        requestLayout();
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        paint.setTextSize(textSize);
    }

    public void setSelTextColor(int selTextColor) {
        this.selTextColor = selTextColor;
    }

    public void setNorTextColor(int norTextColor) {
        this.norTextColor = norTextColor;
        paint.setColor(norTextColor);
    }

    private void initialize() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(norTextColor);
        paint.setTextSize(textSize);
        paint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float total = -fontMetrics.ascent + fontMetrics.descent;
        yAxis = total / 2 - fontMetrics.descent;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (indexs != null && !indexs.isEmpty()) {
            textSpan = h / (indexs.size() + 1);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        if (indexs != null && !indexs.isEmpty()) {
            for (int i = 0; i < indexs.size(); i++) {
                canvas.drawText(indexs.get(i), getWidth() / 2, textSpan * (i + 1) + yAxis, paint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (indexs != null && !indexs.isEmpty()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    paint.setColor(selTextColor);
                    invalidate();
                case MotionEvent.ACTION_MOVE:
                    if (event.getY() < textSpan / 2 || (event.getY() - textSpan / 2) > textSpan * indexs.size()) {
                        return true;
                    }
                    int position = (int) ((event.getY() - textSpan / 2) / textSpan * 1.0f);
                    if (position >= 0 && position < indexs.size()) {
                        ((AlphabetIndexLayout) getParent()).drawLayout(event.getY(), indexs.get(position));
                        if (onAlphabetIndexToggleListener != null && currentPosition != position) {
                            currentPosition = position;
                            onAlphabetIndexToggleListener.onAlphabetIndexToggle(indexs.get(position));
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    ((AlphabetIndexLayout) getParent()).dismiss();
                    paint.setColor(norTextColor);
                    invalidate();
                    break;
            }
            return true;
        } else {
            return false;
        }
    }
}
