package com.hynet.heebit.components.widget.spinner.adapter;

import android.content.Context;

import com.hynet.heebit.components.widget.spinner.listener.OnTextFormatListener;

import java.util.List;

public class TextViewSpinnerAdapter<T> extends BaseAdapter {

    private final List<T> items;

    public TextViewSpinnerAdapter(Context context, List<T> items, int textColor, int backgroundSelector, OnTextFormatListener onTextFormatListener) {
        super(context, textColor, backgroundSelector, onTextFormatListener);
        this.items = items;
    }

    @Override public int getCount() {
        return items.size() - 1;
    }

    @Override public T getItem(int position) {
        if (position >= selectedIndex) {
            return items.get(position + 1);
        } else {
            return items.get(position);
        }
    }

    @Override public T getItemInDataset(int position) {
        return items.get(position);
    }
}