package com.hynet.heebit.components.widget.tablayout.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;

import androidx.collection.SparseArrayCompat;
import androidx.viewpager.widget.PagerAdapter;

public class ViewPagerItemAdapter extends PagerAdapter {

    private final ViewPagerItems pages;
    private final SparseArrayCompat<WeakReference<View>> holder;
    private final LayoutInflater inflater;

    public ViewPagerItemAdapter(ViewPagerItems pages) {
        this.pages = pages;
        this.holder = new SparseArrayCompat<>(pages.size());
        this.inflater = LayoutInflater.from(pages.getContext());
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = getPagerItem(position).initiate(inflater, container);
        container.addView(view);
        holder.put(position, new WeakReference<View>(view));
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        holder.remove(position);
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object == view;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return getPagerItem(position).getTitle();
    }

    @Override
    public float getPageWidth(int position) {
        return getPagerItem(position).getWidth();
    }

    public View getPage(int position) {
        final WeakReference<View> weakRefItem = holder.get(position);
        return (weakRefItem != null) ? weakRefItem.get() : null;
    }

    protected ViewPagerItem getPagerItem(int position) {
        return pages.get(position);
    }
}
