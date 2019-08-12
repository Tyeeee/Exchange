package com.hynet.heebit.components.widget.alphabetindex;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.hynet.heebit.components.utils.ViewUtil;

import androidx.annotation.Nullable;


public class AlphabetIndexLayout extends FrameLayout {

    private int width;
    private int height;
    private Paint layoutPaint;
    private Paint textPaint;
    private float layoutRadius;
    private int layoutColor;
    private int textColor = Color.WHITE;
    private int textSize = 80;
    private float yAxis;
    private float touchYPivot;
    private boolean isDrawByTouch = false;
    private boolean isShowCircle;
    private String index;
    private AlphabetIndexView alphabetIndexView;
    private int alphabetIndexViewWidth = ViewUtil.Companion.getInstance().dp2px(getContext(), 30);
    private float alphabetIndexViewHeightRatio = 1;
    private long duration;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isShowCircle = false;
            invalidate();
        }
    };

    public AlphabetIndexLayout(Context context) {
        this(context, null);
    }

    public AlphabetIndexLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlphabetIndexLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setLayoutRadius(float layoutRadius) {
        this.layoutRadius = layoutRadius;
    }

    public int getLayoutColor() {
        return layoutColor;
    }

    public void setLayoutColor(int layoutColor) {
        this.layoutColor = layoutColor;
        layoutPaint.setColor(layoutColor);
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        textPaint.setColor(textColor);
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        textPaint.setTextSize(textSize);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float total = -fontMetrics.ascent + fontMetrics.descent;
        yAxis = total / 2 - fontMetrics.descent;
    }

    public void setAlphabetIndexViewWidth(int alphabetIndexViewWidth) {
        this.alphabetIndexViewWidth = alphabetIndexViewWidth;
    }

    public void setAlphabetIndexViewHeightRatio(float alphabetIndexViewHeightRatio) {
        this.alphabetIndexViewHeightRatio = alphabetIndexViewHeightRatio;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setDrawByTouch(boolean drawByTouch) {
        this.isDrawByTouch = drawByTouch;
    }

    public void setShowCircle(boolean showCircle) {
        this.isShowCircle = showCircle;
    }

    public AlphabetIndexView getAlphabetIndexView() {
        return alphabetIndexView;
    }

    private void initialize() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        this.width = point.x;
        this.height = point.y;
        this.layoutPaint = new Paint();
        layoutPaint.setAntiAlias(true);
        layoutPaint.setColor(layoutColor);
        this.textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
        textPaint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float total = -fontMetrics.ascent + fontMetrics.descent;
        this.yAxis = total / 2 - fontMetrics.descent;
        this.alphabetIndexView = new AlphabetIndexView(getContext());
        addView(alphabetIndexView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureSize(1, width, widthMeasureSpec), measureSize(0, height, heightMeasureSpec));
    }

    public int measureSize(int specType, int contentSize, int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
            case MeasureSpec.AT_MOST:
                result = Math.min(specSize, contentSize);
                if (specType == 1) {
                    result += getPaddingLeft() + getPaddingRight();
                } else {
                    result += getPaddingTop() + getPaddingBottom();
                }
                break;
            case MeasureSpec.UNSPECIFIED:
            default:
                result = contentSize;
                break;
        }
        return result;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);
        int childCount = getChildCount();
        if (childCount < 1) return;
        View childView = getChildAt(0);
        float topPadding = (1 - alphabetIndexViewHeightRatio) / 2;
        childView.layout(getWidth() - alphabetIndexViewWidth, (int) (getHeight() * topPadding), getWidth(), (int) (getHeight() * (topPadding + alphabetIndexViewHeightRatio)));
    }

    public void drawLayout(float touchYpivot, String index) {
        this.touchYPivot = touchYpivot;
        this.index = index;
        this.isShowCircle = true;
        invalidate();
    }

    public void dismiss() {
        handler.removeMessages(1);
        handler.sendEmptyMessageDelayed(1, duration);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (isShowCircle) {
            if (isDrawByTouch) {
                canvas.drawCircle(getWidth() / 2, touchYPivot + getHeight() * (1 - alphabetIndexViewHeightRatio) / 2, layoutRadius, layoutPaint);
                canvas.drawText(index, getWidth() / 2, touchYPivot + yAxis + getHeight() * (1 - alphabetIndexViewHeightRatio) / 2, textPaint);
            } else {
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, layoutRadius, layoutPaint);
                canvas.drawText(index, getWidth() / 2, getHeight() / 2 + yAxis, textPaint);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeMessages(1);
    }
}
