package com.hynet.heebit.components.widget.panel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import com.hynet.heebit.components.R;
import com.hynet.heebit.components.utils.ViewUtil;
import com.hynet.heebit.components.widget.panel.listener.PanelSlideListener;

import java.util.ArrayList;
import java.util.List;

import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;

public class PanelLayout extends ViewGroup {

    private static final int DEFAULT_PANEL_HEIGHT = 68; // dp;
    private static final float DEFAULT_ANCHOR_POINT = 1.0f; // In relative %
    private static PanelState DEFAULT_SLIDE_STATE = PanelState.COLLAPSED;
    private static final int DEFAULT_SHADOW_HEIGHT = 4; // dp;
    private static final int DEFAULT_FADE_COLOR = 0x99000000;
    private static final int DEFAULT_MIN_FLING_VELOCITY = 400; // dips per second
    private static final boolean DEFAULT_OVERLAY_FLAG = false;
    private static final boolean DEFAULT_CLIP_PANEL_FLAG = true;
    private static final int[] DEFAULT_ATTRS = {android.R.attr.gravity};
    private static final String SLIDING_STATE = "sliding_state";
    private static final int DEFAULT_PARALLAX_OFFSET = 0;

    private int minFlingVelocity = DEFAULT_MIN_FLING_VELOCITY;
    private int coveredFadeColor = DEFAULT_FADE_COLOR;
    private final Paint coveredFadePaint = new Paint();
    private final Drawable shadowDrawable;
    private int panelHeight = -1;
    private int shadowHeight = -1;
    private int parallaxOffset = -1;
    private boolean flag;
    private boolean overlayContent = DEFAULT_OVERLAY_FLAG;
    private boolean clipPanel = DEFAULT_CLIP_PANEL_FLAG;
    private View dragView;
    private int dragViewResouceId = View.NO_ID;
    private View scrollableView;
    private int scrollableViewResourceId;
    private ScrollableViewHelper scrollableViewHelper = new ScrollableViewHelper();
    private View slideableView;
    private View view;

    private PanelState slideState = DEFAULT_SLIDE_STATE;
    private PanelState lastNotDraggingSlideState = DEFAULT_SLIDE_STATE;
    private float slideOffset;
    private int slideRange;
    private float anchorPoint = 1.0f;
    private boolean isUnableToDrag;
    private boolean isTouchEnabled;

    private float previewMotionY;
    private float initialMotionX;
    private float initialMotionY;
    private boolean isScrollableViewHandlingTouch = false;

    private List<PanelSlideListener> panelSlideListeners = new ArrayList<>();
    private OnClickListener fadeOnClickListener;

    private final ViewDragHelper viewDragHelper;

    private boolean isFirstLayout = true;
    private final Rect tempRect = new Rect();

    public PanelLayout(Context context) {
        this(context, null);
    }

    public PanelLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PanelLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (isInEditMode()) {
            shadowDrawable = null;
            viewDragHelper = null;
            return;
        }
        Interpolator scrollerInterpolator = null;
        if (attrs != null) {
            TypedArray defaultTypedArray = context.obtainStyledAttributes(attrs, DEFAULT_ATTRS);
            if (defaultTypedArray != null) {
                int gravity = defaultTypedArray.getInt(0, Gravity.NO_GRAVITY);
                setGravity(gravity);
            }
            defaultTypedArray.recycle();
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PanelLayout);
            if (typedArray != null) {
                panelHeight = typedArray.getDimensionPixelSize(R.styleable.PanelLayout_pl_panel_height, -1);
                shadowHeight = typedArray.getDimensionPixelSize(R.styleable.PanelLayout_pl_shadow_height, -1);
                parallaxOffset = typedArray.getDimensionPixelSize(R.styleable.PanelLayout_pl_parallax_offset, -1);
                minFlingVelocity = typedArray.getInt(R.styleable.PanelLayout_pl_fling_velocity, DEFAULT_MIN_FLING_VELOCITY);
                coveredFadeColor = typedArray.getColor(R.styleable.PanelLayout_pl_fade_color, DEFAULT_FADE_COLOR);
                dragViewResouceId = typedArray.getResourceId(R.styleable.PanelLayout_pl_drag_view, -1);
                scrollableViewResourceId = typedArray.getResourceId(R.styleable.PanelLayout_pl_scrollable_view, -1);
                overlayContent = typedArray.getBoolean(R.styleable.PanelLayout_pl_overlay, DEFAULT_OVERLAY_FLAG);
                clipPanel = typedArray.getBoolean(R.styleable.PanelLayout_pl_clip_panel, DEFAULT_CLIP_PANEL_FLAG);
                anchorPoint = typedArray.getFloat(R.styleable.PanelLayout_pl_anchor_point, DEFAULT_ANCHOR_POINT);
                slideState = PanelState.values()[typedArray.getInt(R.styleable.PanelLayout_pl_initial_state, DEFAULT_SLIDE_STATE.ordinal())];
                int interpolatorResId = typedArray.getResourceId(R.styleable.PanelLayout_pl_scroll_interpolator, -1);
                if (interpolatorResId != -1) {
                    scrollerInterpolator = AnimationUtils.loadInterpolator(context, interpolatorResId);
                }
            }
            typedArray.recycle();
        }

        final float density = context.getResources().getDisplayMetrics().density;
        if (panelHeight == -1) {
            panelHeight = (int) (DEFAULT_PANEL_HEIGHT * density + 0.5f);
        }
        if (shadowHeight == -1) {
            shadowHeight = (int) (DEFAULT_SHADOW_HEIGHT * density + 0.5f);
        }
        if (parallaxOffset == -1) {
            parallaxOffset = (int) (DEFAULT_PARALLAX_OFFSET * density);
        }
        // If the shadow height is zero, don't show the shadow
        if (shadowHeight > 0) {
            if (flag) {
                shadowDrawable = getResources().getDrawable(R.drawable.shadow_panel_layout_above);
            } else {
                shadowDrawable = getResources().getDrawable(R.drawable.shadow_panel_layout_below);
            }
        } else {
            shadowDrawable = null;
        }

        setWillNotDraw(false);

        viewDragHelper = ViewDragHelper.create(this, 0.5f, scrollerInterpolator, new DragHelperCallback());
        viewDragHelper.setMinVelocity(minFlingVelocity * density);

        isTouchEnabled = true;
    }

    /**
     * Set the Drag View after the view is inflated
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (dragViewResouceId != -1) {
            setDragView(findViewById(dragViewResouceId));
        }
        if (scrollableViewResourceId != -1) {
            setScrollableView(findViewById(scrollableViewResourceId));
        }
    }

    public void setGravity(int gravity) {
        if (gravity != Gravity.TOP && gravity != Gravity.BOTTOM) {
            throw new IllegalArgumentException("gravity must be set to either top or bottom");
        }
        flag = gravity == Gravity.BOTTOM;
        if (!isFirstLayout) {
            requestLayout();
        }
    }

    /**
     * Set the color used to fade the pane covered by the sliding pane out when the pane
     * will become fully covered in the expanded state.
     *
     * @param color An ARGB-packed color value
     */
    public void setCoveredFadeColor(int color) {
        coveredFadeColor = color;
        requestLayout();
    }

    /**
     * @return The ARGB-packed color value used to fade the fixed pane
     */
    public int getCoveredFadeColor() {
        return coveredFadeColor;
    }

    /**
     * Set sliding enabled flag
     *
     * @param enabled flag value
     */
    public void setTouchEnabled(boolean enabled) {
        isTouchEnabled = enabled;
    }

    public boolean isTouchEnabled() {
        return isTouchEnabled && slideableView != null && slideState != PanelState.HIDDEN;
    }

    /**
     * Set the collapsed panel height in pixels
     *
     * @param val A height in pixels
     */
    public void setPanelHeight(int val) {
        if (getPanelHeight() == val) {
            return;
        }

        panelHeight = val;
        if (!isFirstLayout) {
            requestLayout();
        }

        if (getPanelState() == PanelState.COLLAPSED) {
            smoothToBottom();
            invalidate();
            return;
        }
    }

    protected void smoothToBottom() {
        smoothSlideTo(0, 0);
    }

    /**
     * @return The current shadow height
     */
    public int getShadowHeight() {
        return shadowHeight;
    }

    /**
     * Set the shadow height
     *
     * @param val A height in pixels
     */
    public void setShadowHeight(int val) {
        shadowHeight = val;
        if (!isFirstLayout) {
            invalidate();
        }
    }

    /**
     * @return The current collapsed panel height
     */
    public int getPanelHeight() {
        return panelHeight;
    }

    /**
     * @return The current parallax offset
     */
    public int getCurrentParallaxOffset() {
        // Clamp slide offset at zero for parallax computation;
        int offset = (int) (parallaxOffset * Math.max(slideOffset, 0));
        return flag ? -offset : offset;
    }

    /**
     * Set parallax offset for the panel
     *
     * @param val A height in pixels
     */
    public void setParallaxOffset(int val) {
        parallaxOffset = val;
        if (!isFirstLayout) {
            requestLayout();
        }
    }

    /**
     * @return The current minimin fling velocity
     */
    public int getMinFlingVelocity() {
        return minFlingVelocity;
    }

    /**
     * Sets the minimum fling velocity for the panel
     *
     * @param val the new value
     */
    public void setMinFlingVelocity(int val) {
        minFlingVelocity = val;
    }

    /**
     * Adds a panel slide listener
     *
     * @param listener
     */
    public void addPanelSlideListener(PanelSlideListener listener) {
        synchronized (panelSlideListeners){
            panelSlideListeners.add(listener);
        }
    }

    /**
     * Removes a panel slide listener
     *
     * @param listener
     */
    public void removePanelSlideListener(PanelSlideListener listener) {
        synchronized (panelSlideListeners){
            panelSlideListeners.remove(listener);
        }
    }

    /**
     * Provides an on click for the portion of the main view that is dimmed. The listener is not
     * triggered if the panel is in a collapsed or a hidden position. If the on click listener is
     * not provided, the clicks on the dimmed area are passed through to the main layout.
     *
     * @param listener
     */
    public void setFadeOnClickListener(OnClickListener listener) {
        fadeOnClickListener = listener;
    }

    /**
     * Set the draggable view portion. Use to null, to allow the whole panel to be draggable
     *
     * @param dragView A view that will be used to drag the panel.
     */
    public void setDragView(View dragView) {
        if (this.dragView != null) {
            this.dragView.setOnClickListener(null);
        }
        this.dragView = dragView;
        if (this.dragView != null) {
            this.dragView.setClickable(true);
            this.dragView.setFocusable(false);
            this.dragView.setFocusableInTouchMode(false);
            this.dragView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isEnabled() || !isTouchEnabled()) return;
                    if (slideState != PanelState.EXPANDED && slideState != PanelState.ANCHORED) {
                        if (anchorPoint < 1.0f) {
                            setPanelState(PanelState.ANCHORED);
                        } else {
                            setPanelState(PanelState.EXPANDED);
                        }
                    } else {
                        setPanelState(PanelState.COLLAPSED);
                    }
                }
            });
            ;
        }
    }

    /**
     * Set the draggable view portion. Use to null, to allow the whole panel to be draggable
     *
     * @param dragViewResId The resource ID of the new drag view
     */
    public void setDragView(int dragViewResId) {
        dragViewResouceId = dragViewResId;
        setDragView(findViewById(dragViewResId));
    }

    /**
     * Set the scrollable child of the sliding layout. If set, scrolling will be transfered between
     * the panel and the view when necessary
     *
     * @param scrollableView The scrollable view
     */
    public void setScrollableView(View scrollableView) {
        this.scrollableView = scrollableView;
    }

    /**
     * Sets the current scrollable view helper. See ScrollableViewHelper description for details.
     *
     * @param helper
     */
    public void setScrollableViewHelper(ScrollableViewHelper helper) {
        scrollableViewHelper = helper;
    }

    /**
     * Set an anchor point where the panel can stop during sliding
     *
     * @param anchorPoint A value between 0 and 1, determining the position of the anchor point
     *                    starting from the top of the layout.
     */
    public void setAnchorPoint(float anchorPoint) {
        if (anchorPoint > 0 && anchorPoint <= 1) {
            this.anchorPoint = anchorPoint;
            isFirstLayout = true;
            requestLayout();
        }
    }

    /**
     * Gets the currently set anchor point
     *
     * @return the currently set anchor point
     */
    public float getAnchorPoint() {
        return anchorPoint;
    }

    /**
     * Sets whether or not the panel overlays the content
     *
     * @param overlayed
     */
    public void setOverlayed(boolean overlayed) {
        overlayContent = overlayed;
    }

    /**
     * Check if the panel is set as an overlay.
     */
    public boolean isOverlayed() {
        return overlayContent;
    }

    /**
     * Sets whether or not the main content is clipped to the top of the panel
     *
     * @param clip
     */
    public void setClipPanel(boolean clip) {
        clipPanel = clip;
    }

    /**
     * Check whether or not the main content is clipped to the top of the panel
     */
    public boolean isClipPanel() {
        return clipPanel;
    }


    void dispatchOnPanelSlide(View panel) {
        synchronized (panelSlideListeners){
            for (PanelSlideListener panelSlideListener : panelSlideListeners) {
                panelSlideListener.onPanelSlide(panel, slideOffset);
            }
        }
    }


    void dispatchOnPanelStateChanged(View panel, PanelState previousState, PanelState newState) {
        synchronized (panelSlideListeners){
            for (PanelSlideListener l : panelSlideListeners) {
                l.onPanelStateChanged(panel, previousState, newState);
            }
        }
        sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
    }

    void updateObscuredViewVisibility() {
        if (getChildCount() == 0) {
            return;
        }
        final int leftBound = getPaddingLeft();
        final int rightBound = getWidth() - getPaddingRight();
        final int topBound = getPaddingTop();
        final int bottomBound = getHeight() - getPaddingBottom();
        final int left;
        final int right;
        final int top;
        final int bottom;
        if (slideableView != null && hasOpaqueBackground(slideableView)) {
            left = slideableView.getLeft();
            right = slideableView.getRight();
            top = slideableView.getTop();
            bottom = slideableView.getBottom();
        } else {
            left = right = top = bottom = 0;
        }
        View child = getChildAt(0);
        final int clampedChildLeft = Math.max(leftBound, child.getLeft());
        final int clampedChildTop = Math.max(topBound, child.getTop());
        final int clampedChildRight = Math.min(rightBound, child.getRight());
        final int clampedChildBottom = Math.min(bottomBound, child.getBottom());
        final int vis;
        if (clampedChildLeft >= left && clampedChildTop >= top &&
                clampedChildRight <= right && clampedChildBottom <= bottom) {
            ViewUtil.Companion.getInstance().setViewInvisible(child);
        } else {
            ViewUtil.Companion.getInstance().setViewVisible(child);
        }
    }

    void setAllChildrenVisible() {
        for (int i = 0, childCount = getChildCount(); i < childCount; i++) {
            final View child = getChildAt(i);
            if (ViewUtil.Companion.getInstance().isInvisible(child)) {
                ViewUtil.Companion.getInstance().setViewVisible(child);
            }
        }
    }

    private static boolean hasOpaqueBackground(View v) {
        final Drawable bg = v.getBackground();
        return bg != null && bg.getOpacity() == PixelFormat.OPAQUE;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isFirstLayout = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isFirstLayout = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode != MeasureSpec.EXACTLY && widthMode != MeasureSpec.AT_MOST) {
            throw new IllegalStateException("Width must have an exact value or MATCH_PARENT");
        } else if (heightMode != MeasureSpec.EXACTLY && heightMode != MeasureSpec.AT_MOST) {
            throw new IllegalStateException("Height must have an exact value or MATCH_PARENT");
        }

        final int childCount = getChildCount();

        if (childCount != 2) {
            throw new IllegalStateException("Sliding up panel layout must have exactly 2 children!");
        }

        view = getChildAt(0);
        slideableView = getChildAt(1);
        if (dragView == null) {
            setDragView(slideableView);
        }

        // If the sliding panel is not visible, then put the whole view in the hidden state
        if (slideableView.getVisibility() != VISIBLE) {
            slideState = PanelState.HIDDEN;
        }

        int layoutHeight = heightSize - getPaddingTop() - getPaddingBottom();
        int layoutWidth = widthSize - getPaddingLeft() - getPaddingRight();

        // First pass. Measure based on child LayoutParams width/height.
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();

            // We always measure the sliding panel in order to know it's height (needed for show panel)
            if (child.getVisibility() == GONE && i == 0) {
                continue;
            }

            int height = layoutHeight;
            int width = layoutWidth;
            if (child == view) {
                if (!overlayContent && slideState != PanelState.HIDDEN) {
                    height -= panelHeight;
                }

                width -= lp.leftMargin + lp.rightMargin;
            } else if (child == slideableView) {
                // The slideable view should be aware of its top margin.
                // See https://github.com//AndroidPanel/issues/412.
                height -= lp.topMargin;
            }

            int childWidthSpec;
            if (lp.width == LayoutParams.WRAP_CONTENT) {
                childWidthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST);
            } else if (lp.width == LayoutParams.MATCH_PARENT) {
                childWidthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            } else {
                childWidthSpec = MeasureSpec.makeMeasureSpec(lp.width, MeasureSpec.EXACTLY);
            }

            int childHeightSpec;
            if (lp.height == LayoutParams.WRAP_CONTENT) {
                childHeightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST);
            } else {
                // Modify the height based on the weight.
                if (lp.weight > 0 && lp.weight < 1) {
                    height = (int) (height * lp.weight);
                } else if (lp.height != LayoutParams.MATCH_PARENT) {
                    height = lp.height;
                }
                childHeightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            }

            child.measure(childWidthSpec, childHeightSpec);

            if (child == slideableView) {
                slideRange = slideableView.getMeasuredHeight() - panelHeight;
            }
        }

        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();

        final int childCount = getChildCount();

        if (isFirstLayout) {
            switch (slideState) {
                case EXPANDED:
                    slideOffset = 1.0f;
                    break;
                case ANCHORED:
                    slideOffset = anchorPoint;
                    break;
                case HIDDEN:
                    int newTop = computePanelTopPosition(0.0f) + (flag ? +panelHeight : -panelHeight);
                    slideOffset = computeSlideOffset(newTop);
                    break;
                default:
                    slideOffset = 0.f;
                    break;
            }
        }

        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();

            // Always layout the sliding view on the first layout
            if (child.getVisibility() == GONE && (i == 0 || isFirstLayout)) {
                continue;
            }

            final int childHeight = child.getMeasuredHeight();
            int childTop = paddingTop;

            if (child == slideableView) {
                childTop = computePanelTopPosition(slideOffset);
            }

            if (!flag) {
                if (child == view && !overlayContent) {
                    childTop = computePanelTopPosition(slideOffset) + slideableView.getMeasuredHeight();
                }
            }
            final int childBottom = childTop + childHeight;
            final int childLeft = paddingLeft + lp.leftMargin;
            final int childRight = childLeft + child.getMeasuredWidth();
            child.layout(childLeft, childTop, childRight, childBottom);
        }

        if (isFirstLayout) {
            updateObscuredViewVisibility();
        }
        applyParallaxForCurrentSlideOffset();

        isFirstLayout = false;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Recalculate sliding panes and their details
        if (h != oldh) {
            isFirstLayout = true;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // If the scrollable view is handling touch, never intercept
        if (isScrollableViewHandlingTouch || !isTouchEnabled()) {
            viewDragHelper.abort();
            return false;
        }

        final int action = MotionEventCompat.getActionMasked(ev);
        final float x = ev.getX();
        final float y = ev.getY();
        final float adx = Math.abs(x - initialMotionX);
        final float ady = Math.abs(y - initialMotionY);
        final int dragSlop = viewDragHelper.getTouchSlop();

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                isUnableToDrag = false;
                initialMotionX = x;
                initialMotionY = y;
                if (!isViewUnder(dragView, (int) x, (int) y)) {
                    viewDragHelper.cancel();
                    isUnableToDrag = true;
                    return false;
                }

                break;
            }

            case MotionEvent.ACTION_MOVE: {
                if (ady > dragSlop && adx > ady) {
                    viewDragHelper.cancel();
                    isUnableToDrag = true;
                    return false;
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                // If the dragView is still dragging when we get here, we need to call processTouchEvent
                // so that the view is settled
                // Added to make scrollable views work (tokudu)
                if (viewDragHelper.isDragging()) {
                    viewDragHelper.processTouchEvent(ev);
                    return true;
                }
                // Check if this was a click on the faded part of the screen, and fire off the listener if there is one.
                if (ady <= dragSlop
                        && adx <= dragSlop
                        && slideOffset > 0 && !isViewUnder(slideableView, (int) initialMotionX, (int) initialMotionY) && fadeOnClickListener != null) {
                    playSoundEffect(android.view.SoundEffectConstants.CLICK);
                    fadeOnClickListener.onClick(this);
                    return true;
                }
                break;
        }
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isEnabled() || !isTouchEnabled()) {
            return super.onTouchEvent(ev);
        }
        try {
            viewDragHelper.processTouchEvent(ev);
            return true;
        } catch (Exception ex) {
            // Ignore the pointer out of range exception
            return false;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);

        if (!isEnabled() || !isTouchEnabled() || (isUnableToDrag && action != MotionEvent.ACTION_DOWN)) {
            viewDragHelper.abort();
            return super.dispatchTouchEvent(ev);
        }

        final float y = ev.getY();

        if (action == MotionEvent.ACTION_DOWN) {
            isScrollableViewHandlingTouch = false;
            previewMotionY = y;
        } else if (action == MotionEvent.ACTION_MOVE) {
            float dy = y - previewMotionY;
            previewMotionY = y;

            // If the scroll view isn't under the touch, pass the
            // event along to the dragView.
            if (!isViewUnder(scrollableView, (int) initialMotionX, (int) initialMotionY)) {
                return super.dispatchTouchEvent(ev);
            }

            // Which direction (up or down) is the drag moving?
            if (dy * (flag ? 1 : -1) > 0) { // Collapsing
                // Is the child less than fully scrolled?
                // Then let the child handle it.
                if (scrollableViewHelper.getScrollableViewScrollPosition(scrollableView, flag) > 0) {
                    isScrollableViewHandlingTouch = true;
                    return super.dispatchTouchEvent(ev);
                }

                // Was the child handling the touch previously?
                // Then we need to rejigger things so that the
                // drag panel gets a proper down event.
                if (isScrollableViewHandlingTouch) {
                    // Send an 'UP' event to the child.
                    MotionEvent up = MotionEvent.obtain(ev);
                    up.setAction(MotionEvent.ACTION_CANCEL);
                    super.dispatchTouchEvent(up);
                    up.recycle();

                    // Send a 'DOWN' event to the panel. (We'll cheat
                    // and hijack this one)
                    ev.setAction(MotionEvent.ACTION_DOWN);
                }

                isScrollableViewHandlingTouch = false;
                return this.onTouchEvent(ev);
            } else if (dy * (flag ? 1 : -1) < 0) { // Expanding
                // Is the panel less than fully expanded?
                // Then we'll handle the drag here.
                if (slideOffset < 1.0f) {
                    isScrollableViewHandlingTouch = false;
                    return this.onTouchEvent(ev);
                }

                // Was the panel handling the touch previously?
                // Then we need to rejigger things so that the
                // child gets a proper down event.
                if (!isScrollableViewHandlingTouch && viewDragHelper.isDragging()) {
                    viewDragHelper.cancel();
                    ev.setAction(MotionEvent.ACTION_DOWN);
                }

                isScrollableViewHandlingTouch = true;
                return super.dispatchTouchEvent(ev);
            }
        } else if (action == MotionEvent.ACTION_UP) {
            // If the scrollable view was handling the touch and we receive an up
            // we want to clear any previous dragging state so we don't intercept a touch stream accidentally
            if (isScrollableViewHandlingTouch) {
                viewDragHelper.setDragState(ViewDragHelper.STATE_IDLE);
            }
        }

        // In all other cases, just let the default behavior take over.
        return super.dispatchTouchEvent(ev);
    }

    private boolean isViewUnder(View view, int x, int y) {
        if (view == null) return false;
        int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);
        int[] parentLocation = new int[2];
        this.getLocationOnScreen(parentLocation);
        int screenX = parentLocation[0] + x;
        int screenY = parentLocation[1] + y;
        return screenX >= viewLocation[0] && screenX < viewLocation[0] + view.getWidth() && screenY >= viewLocation[1] && screenY < viewLocation[1] + view.getHeight();
    }

    /*
     * Computes the top position of the panel based on the slide offset.
     */
    private int computePanelTopPosition(float slideOffset) {
        int slidingViewHeight = slideableView != null ? slideableView.getMeasuredHeight() : 0;
        int slidePixelOffset = (int) (slideOffset * slideRange);
        // Compute the top of the panel if its collapsed
        return flag
                ? getMeasuredHeight() - getPaddingBottom() - panelHeight - slidePixelOffset
                : getPaddingTop() - slidingViewHeight + panelHeight + slidePixelOffset;
    }

    /*
     * Computes the slide offset based on the top position of the panel
     */
    private float computeSlideOffset(int topPosition) {
        // Compute the panel top position if the panel is collapsed (offset 0)
        final int topBoundCollapsed = computePanelTopPosition(0);

        // Determine the new slide offset based on the collapsed top position and the new required
        // top position
        return (flag
                ? (float) (topBoundCollapsed - topPosition) / slideRange
                : (float) (topPosition - topBoundCollapsed) / slideRange);
    }

    /**
     * Returns the current state of the panel as an enum.
     *
     * @return the current panel state
     */
    public PanelState getPanelState() {
        return slideState;
    }

    /**
     * Change panel state to the given state with
     *
     * @param state - new panel state
     */
    public void setPanelState(PanelState state) {
        if (state == null || state == PanelState.DRAGGING) {
            throw new IllegalArgumentException("Panel state cannot be null or DRAGGING.");
        }
        if (!isEnabled()
                || (!isFirstLayout && slideableView == null)
                || state == slideState
                || slideState == PanelState.DRAGGING) return;

        if (isFirstLayout) {
            setPanelStateInternal(state);
        } else {
            if (slideState == PanelState.HIDDEN) {
                ViewUtil.Companion.getInstance().setViewVisible(slideableView);
                requestLayout();
            }
            switch (state) {
                case ANCHORED:
                    smoothSlideTo(anchorPoint, 0);
                    break;
                case COLLAPSED:
                    smoothSlideTo(0, 0);
                    break;
                case EXPANDED:
                    smoothSlideTo(1.0f, 0);
                    break;
                case HIDDEN:
                    int newTop = computePanelTopPosition(0.0f) + (flag ? +panelHeight : -panelHeight);
                    smoothSlideTo(computeSlideOffset(newTop), 0);
                    break;
            }
        }
    }

    private void setPanelStateInternal(PanelState state) {
        if (slideState == state) return;
        PanelState oldState = slideState;
        slideState = state;
        dispatchOnPanelStateChanged(this, oldState, state);
    }

    /**
     * Update the parallax based on the current slide offset.
     */
    @SuppressLint("NewApi")
    private void applyParallaxForCurrentSlideOffset() {
        if (parallaxOffset > 0) {
            int mainViewOffset = getCurrentParallaxOffset();
            ViewCompat.setTranslationY(view, mainViewOffset);
        }
    }

    private void onPanelDragged(int newTop) {
        if (slideState != PanelState.DRAGGING) {
            lastNotDraggingSlideState = slideState;
        }
        setPanelStateInternal(PanelState.DRAGGING);
        // Recompute the slide offset based on the new top position
        slideOffset = computeSlideOffset(newTop);
        applyParallaxForCurrentSlideOffset();
        // Dispatch the slide event
        dispatchOnPanelSlide(slideableView);
        // If the slide offset is negative, and overlay is not on, we need to increase the
        // height of the main content
        LayoutParams lp = (LayoutParams) view.getLayoutParams();
        int defaultHeight = getHeight() - getPaddingBottom() - getPaddingTop() - panelHeight;

        if (slideOffset <= 0 && !overlayContent) {
            // expand the main view
            lp.height = flag ? (newTop - getPaddingBottom()) : (getHeight() - getPaddingBottom() - slideableView.getMeasuredHeight() - newTop);
            if (lp.height == defaultHeight) {
                lp.height = LayoutParams.MATCH_PARENT;
            }
            view.requestLayout();
        } else if (lp.height != LayoutParams.MATCH_PARENT && !overlayContent) {
            lp.height = LayoutParams.MATCH_PARENT;
            view.requestLayout();
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean result;
//        final int save = canvas.save(Canvas.CLIP_SAVE_FLAG);
        final int save = canvas.save();
        if (slideableView != null && slideableView != child) { // if main view
            // Clip against the slider; no sense drawing what will immediately be covered,
            // Unless the panel is set to overlay content
            canvas.getClipBounds(tempRect);
            if (!overlayContent) {
                if (flag) {
                    tempRect.bottom = Math.min(tempRect.bottom, slideableView.getTop());
                } else {
                    tempRect.top = Math.max(tempRect.top, slideableView.getBottom());
                }
            }
            if (clipPanel) {
                canvas.clipRect(tempRect);
            }
            result = super.drawChild(canvas, child, drawingTime);
            if (coveredFadeColor != 0 && slideOffset > 0) {
                final int baseAlpha = (coveredFadeColor & 0xff000000) >>> 24;
                final int imag = (int) (baseAlpha * slideOffset);
                final int color = imag << 24 | (coveredFadeColor & 0xffffff);
                coveredFadePaint.setColor(color);
                canvas.drawRect(tempRect, coveredFadePaint);
            }
        } else {
            result = super.drawChild(canvas, child, drawingTime);
        }
        canvas.restoreToCount(save);
        return result;
    }

    /**
     * Smoothly animate mDraggingPane to the target X position within its range.
     *
     * @param slideOffset position to animate to
     * @param velocity    initial velocity in case of fling, or 0.
     */
    boolean smoothSlideTo(float slideOffset, int velocity) {
        if (!isEnabled() || slideableView == null) {
            // Nothing to do.
            return false;
        }

        int panelTop = computePanelTopPosition(slideOffset);
        if (viewDragHelper.smoothSlideViewTo(slideableView, slideableView.getLeft(), panelTop)) {
            setAllChildrenVisible();
            ViewCompat.postInvalidateOnAnimation(this);
            return true;
        }
        return false;
    }

    @Override
    public void computeScroll() {
        if (viewDragHelper != null && viewDragHelper.continueSettling(true)) {
            if (!isEnabled()) {
                viewDragHelper.abort();
                return;
            }

            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public void draw(Canvas c) {
        super.draw(c);

        // draw the shadow
        if (shadowDrawable != null && slideableView != null) {
            final int right = slideableView.getRight();
            final int top;
            final int bottom;
            if (flag) {
                top = slideableView.getTop() - shadowHeight;
                bottom = slideableView.getTop();
            } else {
                top = slideableView.getBottom();
                bottom = slideableView.getBottom() + shadowHeight;
            }
            final int left = slideableView.getLeft();
            shadowDrawable.setBounds(left, top, right, bottom);
            shadowDrawable.draw(c);
        }
    }

    /**
     * Tests scrollability within child views of v given a delta of dx.
     *
     * @param v      View to test for horizontal scrollability
     * @param checkV Whether the view v passed should itself be checked for scrollability (true),
     *               or just its children (false).
     * @param dx     Delta scrolled in pixels
     * @param x      X coordinate of the active touch point
     * @param y      Y coordinate of the active touch point
     *
     * @return true if child views of v can be scrolled by delta of dx.
     */
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v instanceof ViewGroup) {
            final ViewGroup group = (ViewGroup) v;
            final int scrollX = v.getScrollX();
            final int scrollY = v.getScrollY();
            final int count = group.getChildCount();
            // Count backwards - let topmost views consume scroll distance first.
            for (int i = count - 1; i >= 0; i--) {
                final View child = group.getChildAt(i);
                if (x + scrollX >= child.getLeft() && x + scrollX < child.getRight() &&
                        y + scrollY >= child.getTop() && y + scrollY < child.getBottom() &&
                        canScroll(child, true, dx, x + scrollX - child.getLeft(),
                                  y + scrollY - child.getTop())) {
                    return true;
                }
            }
        }
        return checkV && ViewCompat.canScrollHorizontally(v, -dx);
    }


    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof MarginLayoutParams
                ? new LayoutParams((MarginLayoutParams) p)
                : new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams && super.checkLayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        bundle.putSerializable(SLIDING_STATE, slideState != PanelState.DRAGGING ? slideState : lastNotDraggingSlideState);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            slideState = (PanelState) bundle.getSerializable(SLIDING_STATE);
            slideState = slideState == null ? DEFAULT_SLIDE_STATE : slideState;
            state = bundle.getParcelable("superState");
        }
        super.onRestoreInstanceState(state);
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            if (isUnableToDrag) {
                return false;
            }
            return child == slideableView;
        }

        @Override
        public void onViewDragStateChanged(int state) {
            if (viewDragHelper.getViewDragState() == ViewDragHelper.STATE_IDLE) {
                slideOffset = computeSlideOffset(slideableView.getTop());
                applyParallaxForCurrentSlideOffset();
                if (slideOffset == 1) {
                    updateObscuredViewVisibility();
                    setPanelStateInternal(PanelState.EXPANDED);
                } else if (slideOffset == 0) {
                    setPanelStateInternal(PanelState.COLLAPSED);
                } else if (slideOffset < 0) {
                    setPanelStateInternal(PanelState.HIDDEN);
                    ViewUtil.Companion.getInstance().setViewInvisible(slideableView);
                } else {
                    updateObscuredViewVisibility();
                    setPanelStateInternal(PanelState.ANCHORED);
                }
            }
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            setAllChildrenVisible();
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            onPanelDragged(top);
            invalidate();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int target;
            // direction is always positive if we are sliding in the expanded direction
            float direction = flag ? -yvel : yvel;

            if (direction > 0 && slideOffset <= anchorPoint) {
                // swipe up -> expand and stop at anchor point
                target = computePanelTopPosition(anchorPoint);
            } else if (direction > 0 && slideOffset > anchorPoint) {
                // swipe up past anchor -> expand
                target = computePanelTopPosition(1.0f);
            } else if (direction < 0 && slideOffset >= anchorPoint) {
                // swipe down -> collapse and stop at anchor point
                target = computePanelTopPosition(anchorPoint);
            } else if (direction < 0 && slideOffset < anchorPoint) {
                // swipe down past anchor -> collapse
                target = computePanelTopPosition(0.0f);
            } else if (slideOffset >= (1.f + anchorPoint) / 2) {
                // zero velocity, and far enough from anchor point => expand to the top
                target = computePanelTopPosition(1.0f);
            } else if (slideOffset >= anchorPoint / 2) {
                // zero velocity, and close enough to anchor point => go to anchor
                target = computePanelTopPosition(anchorPoint);
            } else {
                // settle at the bottom
                target = computePanelTopPosition(0.0f);
            }

            viewDragHelper.settleCapturedViewAt(releasedChild.getLeft(), target);
            invalidate();
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return slideRange;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            final int collapsedTop = computePanelTopPosition(0.f);
            final int expandedTop = computePanelTopPosition(1.0f);
            if (flag) {
                return Math.min(Math.max(top, expandedTop), collapsedTop);
            } else {
                return Math.min(Math.max(top, collapsedTop), expandedTop);
            }
        }
    }

    public static class LayoutParams extends MarginLayoutParams {
        private static final int[] ATTRS = new int[]{
                android.R.attr.layout_weight
        };

        public float weight = 0;

        public LayoutParams() {
            super(MATCH_PARENT, MATCH_PARENT);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, float weight) {
            super(width, height);
            this.weight = weight;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(LayoutParams source) {
            super(source);
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            final TypedArray ta = c.obtainStyledAttributes(attrs, ATTRS);
            if (ta != null) {
                this.weight = ta.getFloat(0, 0);
            }

            ta.recycle();
        }
    }
}
