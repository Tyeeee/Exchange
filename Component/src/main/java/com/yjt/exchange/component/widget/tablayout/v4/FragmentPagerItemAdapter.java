package com.hynet.heebit.components.widget.tablayout.v4;

import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;

import androidx.collection.SparseArrayCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class FragmentPagerItemAdapter extends FragmentPagerAdapter {

    private final FragmentPagerItems fragmentPagerItems;
    private final SparseArrayCompat<WeakReference<Fragment>> holder;
    private boolean destroyItem;

    public FragmentPagerItemAdapter(FragmentManager fragmentManager, FragmentPagerItems fragmentPagerItems, boolean destroyItem) {
        super(fragmentManager);
        this.fragmentPagerItems = fragmentPagerItems;
        this.holder = new SparseArrayCompat<>(fragmentPagerItems.size());
        this.destroyItem = destroyItem;
    }

    @Override
    public int getCount() {
        return fragmentPagerItems.size();
    }

    @Override
    public Fragment getItem(int position) {
        return getFragmentPagerItem(position).instantiate(fragmentPagerItems.getContext(), position);
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (destroyItem) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            String tag = fragment.getTag();

        }
        Object item = super.instantiateItem(container, position);
        if (item instanceof Fragment) {
            holder.put(position, new WeakReference<>((Fragment) item));
        }
        return item;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (destroyItem) {
            holder.remove(position);
            super.destroyItem(container, position, object);
        }
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        if (destroyItem) {
            holder.remove(position);
            super.destroyItem(container, position, object);
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return getFragmentPagerItem(position).getTitle();
    }

    @Override
    public float getPageWidth(int position) {
        return super.getPageWidth(position);
    }

    public Fragment getFragmentItem(int position) {
        return (holder.get(position) != null) ? holder.get(position).get() : null;
    }

    protected FragmentPagerItem getFragmentPagerItem(int position) {
        return fragmentPagerItems.get(position);
    }
}
