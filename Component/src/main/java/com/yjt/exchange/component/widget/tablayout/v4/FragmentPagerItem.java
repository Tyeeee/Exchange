package com.hynet.heebit.components.widget.tablayout.v4;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import com.hynet.heebit.components.utils.LogUtil;
import com.hynet.heebit.components.widget.tablayout.view.PagerItem;


public class FragmentPagerItem extends PagerItem {

    private final String className;
    private final Bundle args;

    protected FragmentPagerItem(CharSequence title, float width, String className, Bundle args) {
        super(title, width);
        this.className = className;
        this.args = args;
    }

    public static FragmentPagerItem of(CharSequence title, Class<? extends Fragment> clazz) {
        return of(title, DEFAULT_WIDTH, clazz);
    }

    public static FragmentPagerItem of(CharSequence title, Class<? extends Fragment> clazz, Bundle args) {
        return of(title, DEFAULT_WIDTH, clazz, args);
    }

    public static FragmentPagerItem of(CharSequence title, float width, Class<? extends Fragment> clazz) {
        return of(title, width, clazz, new Bundle());
    }

    public static FragmentPagerItem of(CharSequence title, float width, Class<? extends Fragment> clazz, Bundle args) {
        return new FragmentPagerItem(title, width, clazz.getName(), args);
    }

    public static boolean hasPosition(Bundle args) {
        return args != null && args.containsKey("FragmentPagerItem:Position");
    }

    public static int getPosition(Bundle args) {
        return (hasPosition(args)) ? args.getInt("FragmentPagerItem:Position") : 0;
    }

    private void setPosition(Bundle args, int position) {
        args.putInt("FragmentPagerItem:Position", position);
    }

    public Fragment instantiate(Context context, int position) {
        setPosition(args, position);
         LogUtil.Companion.getInstance().print("className:" + className + " ,args:" + args);
        return Fragment.instantiate(context, className, args);
    }

}
