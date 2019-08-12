package com.hynet.heebit.components.widget.panel;

import android.view.View;
import android.widget.ListView;
import android.widget.ScrollView;

import androidx.recyclerview.widget.RecyclerView;

public class ScrollableViewHelper {

    public int getScrollableViewScrollPosition(View scrollableView, boolean isSlidingUp) {
        if (scrollableView == null) {
            return 0;
        }
        if (scrollableView instanceof ScrollView) {
            if (isSlidingUp) {
                return scrollableView.getScrollY();
            } else {
                ScrollView sv = ((ScrollView) scrollableView);
                View child = sv.getChildAt(0);
                return (child.getBottom() - (sv.getHeight() + sv.getScrollY()));
            }
        } else if (scrollableView instanceof ListView && ((ListView) scrollableView).getChildCount() > 0) {
            ListView lv = ((ListView) scrollableView);
            if (lv.getAdapter() == null) return 0;
            if (isSlidingUp) {
                View firstChild = lv.getChildAt(0);
                // Approximate the scroll position based on the top child and the first visible item
                return lv.getFirstVisiblePosition() * firstChild.getHeight() - firstChild.getTop();
            } else {
                View lastChild = lv.getChildAt(lv.getChildCount() - 1);
                // Approximate the scroll position based on the bottom child and the last visible item
                return (lv.getAdapter().getCount() - lv.getLastVisiblePosition() - 1) * lastChild.getHeight() + lastChild.getBottom() - lv.getBottom();
            }
        } else if (scrollableView instanceof RecyclerView && ((RecyclerView) scrollableView).getChildCount() > 0) {
            RecyclerView recyclerView = ((RecyclerView) scrollableView);
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (recyclerView.getAdapter() == null) return 0;
            if (isSlidingUp) {
                View firstChild = recyclerView.getChildAt(0);
                // Approximate the scroll position based on the top child and the first visible item
                return recyclerView.getChildLayoutPosition(firstChild) * layoutManager.getDecoratedMeasuredHeight(firstChild) - layoutManager.getDecoratedTop(firstChild);
            } else {
                View lastChild = recyclerView.getChildAt(recyclerView.getChildCount() - 1);
                // Approximate the scroll position based on the bottom child and the last visible item
                return (recyclerView.getAdapter().getItemCount() - 1) * layoutManager.getDecoratedMeasuredHeight(lastChild) + layoutManager.getDecoratedBottom(lastChild) - recyclerView.getBottom();
            }
        } else {
            return 0;
        }
    }
}
