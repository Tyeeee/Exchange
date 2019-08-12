package com.hynet.heebit.components.widget.spinner.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hynet.heebit.components.R;
import com.hynet.heebit.components.widget.spinner.listener.OnTextFormatListener;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {

    private final OnTextFormatListener onTextFormatListener;
    private int textColor;
    private int backgroundSelector;
    int selectedIndex;

    BaseAdapter(Context context, int textColor, int backgroundSelector, OnTextFormatListener onTextFormatListener) {
        this.onTextFormatListener = onTextFormatListener;
        this.backgroundSelector = backgroundSelector;
        this.textColor = textColor;
    }

    @Override
    public View getView(int position, @Nullable View convertView, ViewGroup parent) {
        TextView textView;
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.item_spinner_list, null);
            textView = convertView.findViewById(R.id.textView);
            textView.setBackground(ContextCompat.getDrawable(parent.getContext(), backgroundSelector));
            convertView.setTag(new ViewHolder(textView));
        } else {
            textView = ((ViewHolder) convertView.getTag()).textView;
        }
        textView.setText(onTextFormatListener.onTextFormat(getItem(position).toString()));
        textView.setTextColor(textColor);
        return convertView;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int index) {
        selectedIndex = index;
    }

    public abstract T getItemInDataset(int position);

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public abstract T getItem(int position);

    @Override
    public abstract int getCount();

    static class ViewHolder {
        
        TextView textView;

        ViewHolder(TextView textView) {
            this.textView = textView;
        }
    }
}
