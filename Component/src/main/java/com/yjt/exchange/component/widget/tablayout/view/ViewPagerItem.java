package com.hynet.heebit.components.widget.tablayout.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;

public class ViewPagerItem extends PagerItem {

    private final int resource;

    public ViewPagerItem(CharSequence title, float width, @LayoutRes int resource) {
        super(title, width);
        this.resource = resource;
    }

    public static ViewPagerItem of(CharSequence title, @LayoutRes int resource) {
        return of(title, DEFAULT_WIDTH, resource);
    }

    public static ViewPagerItem of(CharSequence title, float width, @LayoutRes int resource) {
        return new ViewPagerItem(title, width, resource);
    }

    public View initiate(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(resource, container, false);
    }
}
