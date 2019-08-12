package com.hynet.heebit.components.widget.sticky.listener;

import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

public interface StickyRecyclerHeadersAdapter<VH extends RecyclerView.ViewHolder> {
    
    long getHeaderId(int position);

    VH onCreateHeaderViewHolder(ViewGroup parent);

    void onBindHeaderViewHolder(VH holder, int position);

    int getItemCount();
}
