package com.hynet.heebit.components.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

public class CustomViewPager extends ViewPager {

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private boolean scrollable;

    public void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        if (scrollable){
            return super.onTouchEvent(arg0);
        }else {
            return false;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (scrollable){
            return super.onInterceptTouchEvent(arg0);
        }else {
            return false;
        }
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item);
    }

    @Override
    public void setOffscreenPageLimit(int limit) {
        super.setOffscreenPageLimit(limit);
    }
    
    
}
