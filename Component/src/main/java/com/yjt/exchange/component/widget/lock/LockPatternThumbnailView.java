package com.hynet.heebit.components.widget.lock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.hynet.heebit.components.R;
import com.hynet.heebit.components.constant.Constant;
import com.hynet.heebit.components.utils.ViewUtil;

public class LockPatternThumbnailView extends View {

    private Paint paint;
    private int width;
    private int height;
    private Drawable drawable1;
    private Drawable drawable2;
    private String lockParameter;

    public LockPatternThumbnailView(Context context) {
        super(context);
    }

    public LockPatternThumbnailView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(Constant.Lock.NODE_EDGE_RATIO / 2);
        paint.setStyle(Paint.Style.STROKE);
        drawable1 = getResources().getDrawableForDensity(R.mipmap.icon_thumbnail_point_gray, ViewUtil.Companion.getInstance().getDensityDpi(context));
        drawable2 = getResources().getDrawableForDensity(R.mipmap.icon_thumbnail_point_blue, ViewUtil.Companion.getInstance().getDensityDpi(context));
        if (drawable2 != null) {
            width = drawable2.getIntrinsicWidth();
            height = drawable2.getIntrinsicHeight();
            drawable1.setBounds(0, 0, width, height);
            drawable2.setBounds(0, 0, width, height);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (drawable1 == null || drawable2 == null) {
            return;
        }
        for (int i = 0; i < Constant.Lock.CIRCLE_COUNT; i++) {
            for (int j = 0; j < Constant.Lock.CIRCLE_COUNT; j++) {
                paint.setColor(-16777216);
                canvas.save();
                canvas.translate(j * height + j * height, i * width + i * width);
                if (!TextUtils.isEmpty(lockParameter)) {
                    if (lockParameter.indexOf(String.valueOf(Constant.Lock.CIRCLE_COUNT * i + (j))) == -1) {
                        drawable1.draw(canvas);
                    } else {
                        drawable2.draw(canvas);
                    }
                } else {
                    drawable1.draw(canvas);
                }
                canvas.restore();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (drawable2 != null) {
            setMeasuredDimension(Constant.Lock.CIRCLE_COUNT * height + height
                    * (Constant.Lock.CIRCLE_COUNT - 1), Constant.Lock.CIRCLE_COUNT
                                         * width + width * (Constant.Lock.CIRCLE_COUNT - 1));
        }
    }

    public void setLockParameter(String str) {
        this.lockParameter = str;
        invalidate();
    }
}
