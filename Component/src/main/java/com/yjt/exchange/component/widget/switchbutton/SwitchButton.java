package com.hynet.heebit.components.widget.switchbutton;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Checkable;

import com.hynet.heebit.components.R;
import com.hynet.heebit.components.utils.ViewUtil;
import com.hynet.heebit.components.widget.switchbutton.listener.OnCheckedChangeListener;


public class SwitchButton extends View implements Checkable {

    private final int ANIMATE_STATE_NONE = 0;
    private final int ANIMATE_STATE_PENDING_DRAG = 1;
    private final int ANIMATE_STATE_DRAGING = 2;
    private final int ANIMATE_STATE_PENDING_RESET = 3;
    private final int ANIMATE_STATE_PENDING_SETTLE = 4;
    private final int ANIMATE_STATE_SWITCH = 5;
    private Context context;

    public SwitchButton(Context context) {
        super(context);
        initialize(context, null);
    }

    public SwitchButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public SwitchButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SwitchButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context, attrs);
    }

    @Override
    public final void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(0, 0, 0, 0);
    }

    private void initialize(Context context, AttributeSet attrs) {
        this.context = context;
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SwitchButton);
            if (typedArray != null) {
                shadowEffect = typedArray.getBoolean(R.styleable.SwitchButton_sb_shadow_effect, true);
                uncheckCircleColor = typedArray.getColor(R.styleable.SwitchButton_sb_uncheckcircle_color, 0XffAAAAAA);//0XffAAAAAA;
                uncheckCircleWidth = typedArray.getDimensionPixelOffset(R.styleable.SwitchButton_sb_uncheckcircle_width, ViewUtil.Companion.getInstance().dp2px(context, 1.5f));
                uncheckCircleOffsetX = ViewUtil.Companion.getInstance().dp2px(Resources.getSystem(), 10);
                uncheckCircleRadius = typedArray.getDimension(R.styleable.SwitchButton_sb_uncheckcircle_radius, ViewUtil.Companion.getInstance().dp2px(Resources.getSystem(), 4));
                checkedLineOffsetX = ViewUtil.Companion.getInstance().dp2px(Resources.getSystem(), 4);
                checkedLineOffsetY = ViewUtil.Companion.getInstance().dp2px(Resources.getSystem(), 4);
                shadowRadius = typedArray.getDimensionPixelOffset(R.styleable.SwitchButton_sb_shadow_radius, ViewUtil.Companion.getInstance().dp2px(context, 2.5f));
                shadowOffset = typedArray.getDimensionPixelOffset(R.styleable.SwitchButton_sb_shadow_offset, ViewUtil.Companion.getInstance().dp2px(context, 1.5f));
                shadowColor = typedArray.getColor(R.styleable.SwitchButton_sb_shadow_color, 0X33000000);//0X33000000;
                uncheckColor = typedArray.getColor(R.styleable.SwitchButton_sb_uncheck_color, 0XffDDDDDD);//0XffDDDDDD;
                checkedColor = typedArray.getColor(R.styleable.SwitchButton_sb_checked_color, 0Xff51d367);//0Xff51d367;
                borderWidth = typedArray.getDimensionPixelOffset(R.styleable.SwitchButton_sb_border_width, ViewUtil.Companion.getInstance().dp2px(context, 1));
                checkLineColor = typedArray.getColor(R.styleable.SwitchButton_sb_checkline_color, Color.WHITE);//Color.WHITE;
                checkLineWidth = typedArray.getDimensionPixelOffset(R.styleable.SwitchButton_sb_checkline_width, ViewUtil.Companion.getInstance().dp2px(context, 1f));
                checkLineLength = ViewUtil.Companion.getInstance().dp2px(Resources.getSystem(), 6);
                int buttonColor = typedArray.getColor(R.styleable.SwitchButton_sb_button_color, Color.WHITE);//Color.WHITE;
                int effectDuration = typedArray.getInt(R.styleable.SwitchButton_sb_effect_duration, 300);//300;
                hasChecked = typedArray.getBoolean(R.styleable.SwitchButton_sb_checked, false);
                showIndicator = typedArray.getBoolean(R.styleable.SwitchButton_sb_show_indicator, true);
                background = typedArray.getColor(R.styleable.SwitchButton_sb_background_color, Color.WHITE);//Color.WHITE;
                enableEffect = typedArray.getBoolean(R.styleable.SwitchButton_sb_enable_effect, true);
                typedArray.recycle();
                paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                buttonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                buttonPaint.setColor(buttonColor);
                if (shadowEffect) {
                    buttonPaint.setShadowLayer(shadowRadius, 0, shadowOffset, shadowColor);
                }
                viewState = new ViewState();
                beforeState = new ViewState();
                afterState = new ViewState();
                valueAnimator = ValueAnimator.ofFloat(0f, 1f);
                valueAnimator.setDuration(effectDuration);
                valueAnimator.setRepeatCount(0);
                valueAnimator.addUpdateListener(animatorUpdateListener);
                valueAnimator.addListener(animatorListener);
                super.setClickable(true);
                this.setPadding(0, 0, 0, 0);
                setLayerType(LAYER_TYPE_SOFTWARE, null);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == MeasureSpec.UNSPECIFIED || widthMode == MeasureSpec.AT_MOST) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(ViewUtil.Companion.getInstance().dp2px(context, 58), MeasureSpec.EXACTLY);
        }
        if (heightMode == MeasureSpec.UNSPECIFIED || heightMode == MeasureSpec.AT_MOST) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(ViewUtil.Companion.getInstance().dp2px(context, 36), MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float viewPadding = Math.max(shadowRadius + shadowOffset, borderWidth);
        height = h - viewPadding - viewPadding;
        width = w - viewPadding - viewPadding;
        viewRadius = height * .5f;
        buttonRadius = viewRadius - borderWidth;
        left = viewPadding;
        top = viewPadding;
        right = w - viewPadding;
        bottom = h - viewPadding;
        centerX = (left + right) * .5f;
        centerY = (top + bottom) * .5f;
        buttonMinX = left + viewRadius;
        buttonMaxX = right - viewRadius;
        if (isChecked()) {
            setCheckedViewState(viewState);
        } else {
            setUncheckViewState(viewState);
        }
        hasUiInitialized = true;
        postInvalidate();
    }

    private void setUncheckViewState(ViewState viewState) {
        viewState.radius = 0;
        viewState.checkStateColor = uncheckColor;
        viewState.checkedLineColor = Color.TRANSPARENT;
        viewState.buttonX = buttonMinX;
    }

    private void setCheckedViewState(ViewState viewState) {
        viewState.radius = viewRadius;
        viewState.checkStateColor = checkedColor;
        viewState.checkedLineColor = checkLineColor;
        viewState.buttonX = buttonMaxX;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStrokeWidth(borderWidth);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(background);
        drawRoundRect(canvas, left, top, right, bottom, viewRadius, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(uncheckColor);
        drawRoundRect(canvas, left, top, right, bottom, viewRadius, paint);
        if (showIndicator) {
            drawUncheckIndicator(canvas);
        }
        float des = viewState.radius * .5f;//[0-backgroundRadius*0.5f]
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(viewState.checkStateColor);
        paint.setStrokeWidth(borderWidth + des * 2f);
        drawRoundRect(canvas, left + des, top + des, right - des, bottom - des, viewRadius, paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(1);
        drawArc(canvas, left, top, left + 2 * viewRadius, top + 2 * viewRadius, 90, 180, paint);
        canvas.drawRect(left + viewRadius, top, viewState.buttonX, top + 2 * viewRadius, paint);
        if (showIndicator) {
            drawCheckedIndicator(canvas);
        }
        drawButton(canvas, viewState.buttonX, centerY);
    }


    protected void drawCheckedIndicator(Canvas canvas) {
        drawCheckedIndicator(canvas, viewState.checkedLineColor, checkLineWidth, left + viewRadius - checkedLineOffsetX, centerY - checkLineLength, left + viewRadius - checkedLineOffsetY, centerY + checkLineLength, paint);
    }

    protected void drawCheckedIndicator(Canvas canvas, int color, float lineWidth, float sx, float sy, float ex, float ey, Paint paint) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setStrokeWidth(lineWidth);
        canvas.drawLine(sx, sy, ex, ey, paint);
    }

    private void drawUncheckIndicator(Canvas canvas) {
        drawUncheckIndicator(canvas, uncheckCircleColor, uncheckCircleWidth, right - uncheckCircleOffsetX, centerY, uncheckCircleRadius, paint);
    }

    protected void drawUncheckIndicator(Canvas canvas, int color, float lineWidth, float centerX, float centerY, float radius, Paint paint) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setStrokeWidth(lineWidth);
        canvas.drawCircle(centerX, centerY, radius, paint);
    }

    private void drawArc(Canvas canvas, float left, float top, float right, float bottom, float startAngle, float sweepAngle, Paint paint) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawArc(left, top, right, bottom, startAngle, sweepAngle, true, paint);
        } else {
            rect.set(left, top, right, bottom);
            canvas.drawArc(rect, startAngle, sweepAngle, true, paint);
        }
    }

    private void drawRoundRect(Canvas canvas, float left, float top, float right, float bottom, float backgroundRadius, Paint paint) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(left, top, right, bottom, backgroundRadius, backgroundRadius, paint);
        } else {
            rect.set(left, top, right, bottom);
            canvas.drawRoundRect(rect, backgroundRadius, backgroundRadius, paint);
        }
    }

    private void drawButton(Canvas canvas, float x, float y) {
        canvas.drawCircle(x, y, buttonRadius, buttonPaint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        paint.setColor(0XffDDDDDD);
        canvas.drawCircle(x, y, buttonRadius, paint);
    }

    @Override
    public void setChecked(boolean checked) {
        if (checked == isChecked()) {
            postInvalidate();
            return;
        }
        toggle(enableEffect, false);
    }
    
    @Override
    public boolean isChecked() {
        return hasChecked;
    }

    @Override
    public void toggle() {
        toggle(true);
    }

    public void toggle(boolean animate) {
        toggle(animate, true);
    }

    public void toggle(boolean animate, boolean broadcast) {
        if (!isEnabled()) {
            return;
        }
        if (hasEventBroadcast) {
            throw new RuntimeException("should not switch the state in method: [onCheckedChanged]!");
        }
        if (!hasUiInitialized) {
            hasChecked = !hasChecked;
            if (broadcast) {
                broadcastEvent();
            }
            return;
        }
        if (valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        if (!enableEffect || !animate) {
            hasChecked = !hasChecked;
            if (isChecked()) {
                setCheckedViewState(viewState);
            } else {
                setUncheckViewState(viewState);
            }
            postInvalidate();
            if (broadcast) {
                broadcastEvent();
            }
            return;
        }
        animateState = ANIMATE_STATE_SWITCH;
        beforeState.copy(viewState);
        if (isChecked()) {
            setUncheckViewState(afterState);
        } else {
            setCheckedViewState(afterState);
        }
        valueAnimator.start();
    }

    private void broadcastEvent() {
        if (onCheckedChangeListener != null) {
            hasEventBroadcast = true;
            onCheckedChangeListener.onCheckedChanged(this, isChecked());
        }
        hasEventBroadcast = false;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        int actionMasked = event.getActionMasked();

        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN: {
                isTouchingDown = true;
                touchDownTime = System.currentTimeMillis();
                removeCallbacks(postPendingDrag);
                postDelayed(postPendingDrag, 100);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                float eventX = event.getX();
                if (isPendingDragState()) {
                    float fraction = eventX / getWidth();
                    fraction = Math.max(0f, Math.min(1f, fraction));
                    viewState.buttonX = buttonMinX + (buttonMaxX - buttonMinX) * fraction;
                } else if (isDragState()) {
                    float fraction = eventX / getWidth();
                    fraction = Math.max(0f, Math.min(1f, fraction));
                    viewState.buttonX = buttonMinX + (buttonMaxX - buttonMinX) * fraction;
                    viewState.checkStateColor = (int) argbEvaluator.evaluate(fraction, uncheckColor, checkedColor);
                    postInvalidate();
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                isTouchingDown = false;
                removeCallbacks(postPendingDrag);
                if (System.currentTimeMillis() - touchDownTime <= 300) {
                    toggle();
                } else if (isDragState()) {
                    float eventX = event.getX();
                    float fraction = eventX / getWidth();
                    fraction = Math.max(0f, Math.min(1f, fraction));
                    boolean newCheck = fraction > .5f;
                    if (newCheck == isChecked()) {
                        pendingCancelDragState();
                    } else {
                        hasChecked = newCheck;
                        pendingSettleState();
                    }
                } else if (isPendingDragState()) {
                    pendingCancelDragState();
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                isTouchingDown = false;
                removeCallbacks(postPendingDrag);
                if (isPendingDragState() || isDragState()) {
                    pendingCancelDragState();
                }
                break;
            }
        }
        return true;
    }

    private boolean isInAnimating() {
        return animateState != ANIMATE_STATE_NONE;
    }

    private boolean isPendingDragState() {
        return animateState == ANIMATE_STATE_PENDING_DRAG || animateState == ANIMATE_STATE_PENDING_RESET;
    }

    private boolean isDragState() {
        return animateState == ANIMATE_STATE_DRAGING;
    }

    public void setShadowEffect(boolean shadowEffect) {
        if (this.shadowEffect == shadowEffect) {
            return;
        }
        this.shadowEffect = shadowEffect;
        if (this.shadowEffect) {
            buttonPaint.setShadowLayer(shadowRadius, 0, shadowOffset, shadowColor);
        } else {
            buttonPaint.setShadowLayer(0, 0, 0, 0);
        }
    }

    public void setEnableEffect(boolean enable) {
        this.enableEffect = enable;
    }

    private void pendingDragState() {
        if (isInAnimating()) {
            return;
        }
        if (!isTouchingDown) {
            return;
        }
        if (valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        animateState = ANIMATE_STATE_PENDING_DRAG;
        beforeState.copy(viewState);
        afterState.copy(viewState);
        if (isChecked()) {
            afterState.checkStateColor = checkedColor;
            afterState.buttonX = buttonMaxX;
            afterState.checkedLineColor = checkedColor;
        } else {
            afterState.checkStateColor = uncheckColor;
            afterState.buttonX = buttonMinX;
            afterState.radius = viewRadius;
        }
        valueAnimator.start();
    }

    private void pendingCancelDragState() {
        if (isDragState() || isPendingDragState()) {
            if (valueAnimator.isRunning()) {
                valueAnimator.cancel();
            }
            animateState = ANIMATE_STATE_PENDING_RESET;
            beforeState.copy(viewState);
            if (isChecked()) {
                setCheckedViewState(afterState);
            } else {
                setUncheckViewState(afterState);
            }
            valueAnimator.start();
        }
    }

    private void pendingSettleState() {
        if (valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        animateState = ANIMATE_STATE_PENDING_SETTLE;
        beforeState.copy(viewState);
        if (isChecked()) {
            setCheckedViewState(afterState);
        } else {
            setUncheckViewState(afterState);
        }
        valueAnimator.start();
    }


    @Override
    public final void setOnClickListener(OnClickListener onClickListener) {}

    @Override
    public final void setOnLongClickListener(OnLongClickListener onLongClickListener) {}

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    private int shadowRadius;
    private int shadowOffset;
    private int shadowColor;
    private float viewRadius;
    private float buttonRadius;
    private float height;
    private float width;
    private float left;
    private float top;
    private float right;
    private float bottom;
    private float centerX;
    private float centerY;
    private int background;
    private int uncheckColor;
    private int checkedColor;
    private int borderWidth;
    private int checkLineColor;
    private int checkLineWidth;
    private float checkLineLength;
    private int uncheckCircleColor;
    private int uncheckCircleWidth;
    private float uncheckCircleOffsetX;
    private float uncheckCircleRadius;
    private float checkedLineOffsetX;
    private float checkedLineOffsetY;
    private float buttonMinX;
    private float buttonMaxX;
    private Paint buttonPaint;
    private Paint paint;
    private ViewState viewState;
    private ViewState beforeState;
    private ViewState afterState;
    private RectF rect = new RectF();
    private int animateState = ANIMATE_STATE_NONE;
    private ValueAnimator valueAnimator;
    private final android.animation.ArgbEvaluator argbEvaluator = new android.animation.ArgbEvaluator();
    private boolean hasChecked;
    private boolean enableEffect;
    private boolean shadowEffect;
    private boolean showIndicator;
    private boolean isTouchingDown = false;
    private boolean hasUiInitialized = false;
    private boolean hasEventBroadcast = false;
    private OnCheckedChangeListener onCheckedChangeListener;
    private long touchDownTime;

    private Runnable postPendingDrag = new Runnable() {
        @Override
        public void run() {
            if (!isInAnimating()) {
                pendingDragState();
            }
        }
    };

    private ValueAnimator.AnimatorUpdateListener animatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float value = (Float) animation.getAnimatedValue();
            switch (animateState) {
                case ANIMATE_STATE_PENDING_SETTLE:
                case ANIMATE_STATE_PENDING_RESET:
                case ANIMATE_STATE_PENDING_DRAG:
                    viewState.checkedLineColor = (int) argbEvaluator.evaluate(value, beforeState.checkedLineColor, afterState.checkedLineColor);
                    viewState.radius = beforeState.radius + (afterState.radius - beforeState.radius) * value;
                    if (animateState != ANIMATE_STATE_PENDING_DRAG) {
                        viewState.buttonX = beforeState.buttonX + (afterState.buttonX - beforeState.buttonX) * value;
                    }
                    viewState.checkStateColor = (int) argbEvaluator.evaluate(value, beforeState.checkStateColor, afterState.checkStateColor);
                    break;
                case ANIMATE_STATE_SWITCH:
                    viewState.buttonX = beforeState.buttonX + (afterState.buttonX - beforeState.buttonX) * value;
                    float fraction = (viewState.buttonX - buttonMinX) / (buttonMaxX - buttonMinX);
                    viewState.checkStateColor = (int) argbEvaluator.evaluate(fraction, uncheckColor, checkedColor);
                    viewState.radius = fraction * viewRadius;
                    viewState.checkedLineColor = (int) argbEvaluator.evaluate(fraction, Color.TRANSPARENT, checkLineColor);
                    break;
                case ANIMATE_STATE_DRAGING:
                case ANIMATE_STATE_NONE:
                default:
                    break;
            }
            postInvalidate();
        }
    };

    private Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {

        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            switch (animateState) {
                case ANIMATE_STATE_DRAGING:
                    break;
                case ANIMATE_STATE_PENDING_DRAG:
                    animateState = ANIMATE_STATE_DRAGING;
                    viewState.checkedLineColor = Color.TRANSPARENT;
                    viewState.radius = viewRadius;
                    postInvalidate();
                    break;
                case ANIMATE_STATE_PENDING_RESET:
                    animateState = ANIMATE_STATE_NONE;
                    postInvalidate();
                    break;
                case ANIMATE_STATE_PENDING_SETTLE:
                    animateState = ANIMATE_STATE_NONE;
                    postInvalidate();
                    broadcastEvent();
                    break;
                case ANIMATE_STATE_SWITCH:
                    hasChecked = !hasChecked;
                    animateState = ANIMATE_STATE_NONE;
                    postInvalidate();
                    broadcastEvent();
                    break;
                case ANIMATE_STATE_NONE:
                default:
                    break;
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    };


    private static class ViewState {
        
        float buttonX;
        int checkStateColor;
        int checkedLineColor;
        float radius;

        ViewState() {}

        private void copy(ViewState source) {
            this.buttonX = source.buttonX;
            this.checkStateColor = source.checkStateColor;
            this.checkedLineColor = source.checkedLineColor;
            this.radius = source.radius;
        }
    }
}
