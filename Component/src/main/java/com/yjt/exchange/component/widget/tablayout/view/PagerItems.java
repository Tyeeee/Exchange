package com.hynet.heebit.components.widget.tablayout.view;

import android.content.Context;

import java.util.ArrayList;

public abstract class PagerItems<T extends PagerItem> extends ArrayList<T> {

    private final Context context;

    public PagerItems(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

}
