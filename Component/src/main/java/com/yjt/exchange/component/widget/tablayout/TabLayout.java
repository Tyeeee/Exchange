package com.hynet.heebit.components.widget.tablayout;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hynet.heebit.components.R;
import com.hynet.heebit.components.utils.ViewUtil;
import com.hynet.heebit.components.widget.tablayout.listener.OnTabClickListener;
import com.hynet.heebit.components.widget.tablayout.listener.TabColorizer;
import com.hynet.heebit.components.widget.tablayout.listener.TabProvider;
import com.hynet.heebit.components.widget.tablayout.listener.implement.InternalViewPagerListener;
import com.hynet.heebit.components.widget.tablayout.listener.implement.SimpleTabProvider;

import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;


public class TabLayout extends HorizontalScrollView {

    private static final boolean DEFAULT_DISTRIBUTE_EVENLY = false;
    private static final int TITLE_OFFSET_DIPS = 24;
    private static final int TITLE_OFFSET_AUTO_CENTER = -1;
    private static final int TAB_VIEW_PADDING_DIPS = 16;
    private static final boolean TAB_VIEW_TEXT_ALL_CAPS = true;
    private static final int TAB_VIEW_TEXT_SIZE_SP = 12;
    private static final int TAB_VIEW_TEXT_COLOR = 0xFC000000;
    private static final int TAB_VIEW_TEXT_MIN_WIDTH = 0;
    private static final boolean TAB_CLICKABLE = true;

    protected final TabStrip tabStrip;
    private int titleOffset;
    private int tabViewBackgroundResId;
    private boolean tabViewTextAllCaps;
    private ColorStateList tabViewTextColors;
    private float tabViewTextSize;
    private int tabViewTextHorizontalPadding;
    private int tabViewTextMinWidth;
    private ViewPager viewPager;
    private ViewPager.OnPageChangeListener onPageChangeListener;
    private com.hynet.heebit.components.widget.tablayout.listener.OnScrollChangeListener onScrollChangeListener;
    private TabProvider tabProvider;
    private InternalTabClickListener internalTabClickListener;
    private OnTabClickListener onTabClickListener;
    private boolean distributeEvenly;

    public TabLayout(Context context) {
        this(context, null);
    }

    public TabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // Disable the Scroll Bar
        setHorizontalScrollBarEnabled(false);
        final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        final float density = displayMetrics.density;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TabLayout, defStyle, 0);
        int customTabLayoutId = typedArray.getResourceId(R.styleable.TabLayout_tl_customTabTextLayoutId, View.NO_ID);
        int customTabTextViewId = typedArray.getResourceId(R.styleable.TabLayout_tl_customTabTextViewId, View.NO_ID);
        this.titleOffset = typedArray.getLayoutDimension(R.styleable.TabLayout_tl_titleOffset, (int) (TITLE_OFFSET_DIPS * density));
        this.tabViewBackgroundResId = typedArray.getResourceId(R.styleable.TabLayout_tl_defaultTabBackground, View.NO_ID);
        this.tabViewTextAllCaps = typedArray.getBoolean(R.styleable.TabLayout_tl_defaultTabTextAllCaps, TAB_VIEW_TEXT_ALL_CAPS);
        ColorStateList textColors = typedArray.getColorStateList(R.styleable.TabLayout_tl_defaultTabTextColor);
        this.tabViewTextColors = (textColors != null) ? textColors : ColorStateList.valueOf(TAB_VIEW_TEXT_COLOR);
        this.tabViewTextSize = typedArray.getDimension(R.styleable.TabLayout_tl_defaultTabTextSize, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, TAB_VIEW_TEXT_SIZE_SP, displayMetrics));
        this.tabViewTextHorizontalPadding = typedArray.getDimensionPixelSize(R.styleable.TabLayout_tl_defaultTabTextHorizontalPadding, (int) (TAB_VIEW_PADDING_DIPS * density));
        this.tabViewTextMinWidth = typedArray.getDimensionPixelSize(R.styleable.TabLayout_tl_defaultTabTextMinWidth, (int) (TAB_VIEW_TEXT_MIN_WIDTH * density));
        this.internalTabClickListener = typedArray.getBoolean(R.styleable.TabLayout_tl_clickable, TAB_CLICKABLE) ? new InternalTabClickListener() : null;
        this.distributeEvenly = typedArray.getBoolean(R.styleable.TabLayout_tl_distributeEvenly, DEFAULT_DISTRIBUTE_EVENLY);
        typedArray.recycle();
        if (customTabLayoutId != View.NO_ID) {
            setCustomTabView(customTabLayoutId, customTabTextViewId);
        }
        this.tabStrip = new TabStrip(context, attrs);
        if (distributeEvenly && tabStrip.isIndicatorAlwaysInCenter()) {
            throw new UnsupportedOperationException("'distributeEvenly' and 'indicatorAlwaysInCenter' both use does not support");
        }
        // Make sure that the Tab Strips fills this View
        setFillViewport(!tabStrip.isIndicatorAlwaysInCenter());
        addView(tabStrip, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onScrollChangeListener != null) {
            onScrollChangeListener.onScrollChanged(l, oldl);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (tabStrip.isIndicatorAlwaysInCenter() && tabStrip.getChildCount() > 0) {
            View firstTab = tabStrip.getChildAt(0);
            View lastTab = tabStrip.getChildAt(tabStrip.getChildCount() - 1);
            int start = (w - ViewUtil.Companion.getInstance().getMeasuredWidth(firstTab)) / 2 - ViewUtil.Companion.getInstance().getMarginStart(firstTab);
            int end = (w - ViewUtil.Companion.getInstance().getMeasuredWidth(lastTab)) / 2 - ViewUtil.Companion.getInstance().getMarginEnd(lastTab);
            tabStrip.setMinimumWidth(tabStrip.getMeasuredWidth());
            ViewCompat.setPaddingRelative(this, start, getPaddingTop(), end, getPaddingBottom());
            setClipToPadding(false);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        // Ensure first scroll
        if (changed && viewPager != null) {
            scrollToTab(viewPager.getCurrentItem(), 0);
        }
    }

    public void setIndicationInterpolator(TabIndicationInterpolator tabIndicationInterpolator) {
        tabStrip.setTabIndicationInterpolator(tabIndicationInterpolator);
    }

    public void setCustomTabColorizer(TabColorizer tabColorizer) {
        tabStrip.setCustomTabColorizer(tabColorizer);
    }

    public void setDefaultTabTextColor(int color) {
        tabViewTextColors = ColorStateList.valueOf(color);
    }

    public void setDefaultTabTextColor(ColorStateList colorStateList) {
        tabViewTextColors = colorStateList;
    }

    public void setDistributeEvenly(boolean distributeEvenly) {
        this.distributeEvenly = distributeEvenly;
    }

    public void setSelectedIndicatorColors(int... colors) {
        tabStrip.setSelectedIndicatorColors(colors);
    }

    public void setDividerColors(int... colors) {
        tabStrip.setDividerColors(colors);
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
    }

    public void setOnScrollChangeListener(com.hynet.heebit.components.widget.tablayout.listener.OnScrollChangeListener onScrollChangeListener) {
        this.onScrollChangeListener = onScrollChangeListener;
    }

    public void setOnTabClickListener(OnTabClickListener onTabClickListener) {
        this.onTabClickListener = onTabClickListener;
    }

    public void setCustomTabView(int layoutResId, int textViewId) {
        this.tabProvider = new SimpleTabProvider(getContext(), layoutResId, textViewId);
    }

    public void setCustomTabView(TabProvider tabProvider) {
        this.tabProvider = tabProvider;
    }

    public void setViewPager(ViewPager viewPager) {
        tabStrip.removeAllViews();
        this.viewPager = viewPager;
        if (viewPager != null && viewPager.getAdapter() != null) {
            viewPager.addOnPageChangeListener(new InternalViewPagerListener(tabStrip, this, onPageChangeListener));
            populateTabStrip();
        }
    }

    public View getTabAt(int position) {
        return tabStrip.getChildAt(position);
    }

    protected TextView createDefaultTabView(CharSequence title) {
        TextView textView = new TextView(getContext());
        textView.setGravity(Gravity.CENTER);
        textView.setText(title);
        textView.setTextColor(tabViewTextColors);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabViewTextSize);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
        if (tabViewBackgroundResId != View.NO_ID) {
            textView.setBackgroundResource(tabViewBackgroundResId);
        } else {
            // If we're running on Honeycomb or newer, then we can use the Theme's
            // selectableItemBackground to ensure that the View has a pressed state
            TypedValue typedValue = new TypedValue();
            getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);
            textView.setBackgroundResource(typedValue.resourceId);
        }
        // If we're running on ICS or newer, enable all-caps to match the Action Bar tab style
        textView.setAllCaps(tabViewTextAllCaps);
        textView.setPadding(tabViewTextHorizontalPadding, 0, tabViewTextHorizontalPadding, 0);
        if (tabViewTextMinWidth > 0) {
            textView.setMinWidth(tabViewTextMinWidth);
        }
        return textView;
    }

    private void populateTabStrip() {
        final PagerAdapter pagerAdapter = viewPager.getAdapter();
        for (int i = 0; i < pagerAdapter.getCount(); i++) {
            final View tabView = (tabProvider == null) ? createDefaultTabView(pagerAdapter.getPageTitle(i)) : tabProvider.createTabView(tabStrip, i, pagerAdapter);
            if (tabView == null) {
                throw new IllegalStateException("tabView is null.");
            }
            if (distributeEvenly) {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                layoutParams.width = 0;
                layoutParams.weight = 1;
            }
            if (internalTabClickListener != null) {
                tabView.setOnClickListener(internalTabClickListener);
            }
            tabStrip.addView(tabView);
            if (i == viewPager.getCurrentItem()) {
                tabView.setSelected(true);
            }
        }
    }

    public void scrollToTab(int tabIndex, float positionOffset) {
        final int tabStripChildCount = tabStrip.getChildCount();
        if (tabStripChildCount == 0 || tabIndex < 0 || tabIndex >= tabStripChildCount) {
            return;
        }
        final boolean isLayoutRtl = ViewUtil.Companion.getInstance().isLayoutRtl(this);
        View selectedTab = tabStrip.getChildAt(tabIndex);
        int widthPlusMargin = ViewUtil.Companion.getInstance().getWidth(selectedTab) + ViewUtil.Companion.getInstance().getMarginHorizontally(selectedTab);
        int extraOffset = (int) (positionOffset * widthPlusMargin);
        if (tabStrip.isIndicatorAlwaysInCenter()) {
            if (0f < positionOffset && positionOffset < 1f) {
                View nextTab = tabStrip.getChildAt(tabIndex + 1);
                int selectHalfWidth = ViewUtil.Companion.getInstance().getWidth(selectedTab) / 2 + ViewUtil.Companion.getInstance().getMarginEnd(selectedTab);
                int nextHalfWidth = ViewUtil.Companion.getInstance().getWidth(nextTab) / 2 + ViewUtil.Companion.getInstance().getMarginStart(nextTab);
                extraOffset = Math.round(positionOffset * (selectHalfWidth + nextHalfWidth));
            }
            View firstTab = tabStrip.getChildAt(0);
            int x;
            if (isLayoutRtl) {
                int first = ViewUtil.Companion.getInstance().getWidth(firstTab) + ViewUtil.Companion.getInstance().getMarginEnd(firstTab);
                int selected = ViewUtil.Companion.getInstance().getWidth(selectedTab) + ViewUtil.Companion.getInstance().getMarginEnd(selectedTab);
                x = ViewUtil.Companion.getInstance().getEnd(selectedTab) - ViewUtil.Companion.getInstance().getMarginEnd(selectedTab) - extraOffset;
                x -= (first - selected) / 2;
            } else {
                int first = ViewUtil.Companion.getInstance().getWidth(firstTab) + ViewUtil.Companion.getInstance().getMarginStart(firstTab);
                int selected = ViewUtil.Companion.getInstance().getWidth(selectedTab) + ViewUtil.Companion.getInstance().getMarginStart(selectedTab);
                x = ViewUtil.Companion.getInstance().getStart(selectedTab) - ViewUtil.Companion.getInstance().getMarginStart(selectedTab) + extraOffset;
                x -= (first - selected) / 2;
            }
            scrollTo(x, 0);
            return;
        }
        int x;
        if (titleOffset == TITLE_OFFSET_AUTO_CENTER) {
            if (0f < positionOffset && positionOffset < 1f) {
                View nextTab = tabStrip.getChildAt(tabIndex + 1);
                int selectHalfWidth = ViewUtil.Companion.getInstance().getWidth(selectedTab) / 2 + ViewUtil.Companion.getInstance().getMarginEnd(selectedTab);
                int nextHalfWidth = ViewUtil.Companion.getInstance().getWidth(nextTab) / 2 + ViewUtil.Companion.getInstance().getMarginStart(nextTab);
                extraOffset = Math.round(positionOffset * (selectHalfWidth + nextHalfWidth));
            }
            if (isLayoutRtl) {
                x = -ViewUtil.Companion.getInstance().getWidthWithMargin(selectedTab) / 2 + getWidth() / 2;
                x -= ViewUtil.Companion.getInstance().getPaddingStart(this);
            } else {
                x = ViewUtil.Companion.getInstance().getWidthWithMargin(selectedTab) / 2 - getWidth() / 2;
                x += ViewUtil.Companion.getInstance().getPaddingStart(this);
            }
        } else {
            if (isLayoutRtl) {
                x = (tabIndex > 0 || positionOffset > 0) ? titleOffset : 0;
            } else {
                x = (tabIndex > 0 || positionOffset > 0) ? -titleOffset : 0;
            }
        }
        int start = ViewUtil.Companion.getInstance().getStart(selectedTab);
        int startMargin = ViewUtil.Companion.getInstance().getMarginStart(selectedTab);
        if (isLayoutRtl) {
            x += start + startMargin - extraOffset - getWidth() + ViewUtil.Companion.getInstance().getPaddingHorizontally(this);
        } else {
            x += start - startMargin + extraOffset;
        }
        scrollTo(x, 0);
    }

    private class InternalTabClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            for (int i = 0; i < tabStrip.getChildCount(); i++) {
                if (v == tabStrip.getChildAt(i)) {
                    if (onTabClickListener != null) {
                        onTabClickListener.onTabClicked(i);
                    }
                    viewPager.setCurrentItem(i, true);
                    return;
                }
            }
        }
    }
}
