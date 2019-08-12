package com.hynet.heebit.components.widget.tablayout.listener;

import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

public interface TabProvider {

    View createTabView(ViewGroup container, int position, PagerAdapter adapter);

}
