package com.hynet.heebit.components.widget.sticky;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LinearLayoutDividerItemDecoration extends RecyclerView.ItemDecoration {

    private int orientation;
    private int size;
    private Paint paint;

    public LinearLayoutDividerItemDecoration(int color, int size, int orientation) {
        if (orientation != LinearLayoutManager.VERTICAL && orientation != LinearLayoutManager.HORIZONTAL) {
            throw new IllegalArgumentException("orientation error");
        }
        this.size = size;
        this.orientation = orientation;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        switch (orientation) {
            case LinearLayoutManager.HORIZONTAL:
                drawHorizontal(c, parent);
                break;
            case LinearLayoutManager.VERTICAL:
                drawVertical(c, parent);
                break;
            default:
                drawVertical(c, parent);
                break;
        }
    }

    private void drawVertical(Canvas canvas, RecyclerView parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + layoutParams.bottomMargin;
            final int bottom = top + size;
            canvas.drawRect(parent.getPaddingLeft(), top, parent.getMeasuredWidth() - parent.getPaddingRight(), bottom, paint);
        }
    }

    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + layoutParams.rightMargin;
            final int right = left + size;
            canvas.drawRect(left, parent.getPaddingTop(), right, parent.getMeasuredHeight() - parent.getPaddingBottom(), paint);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        switch (orientation) {
            case LinearLayoutManager.HORIZONTAL:
                outRect.set(0, 0, size, 0);
                break;
            case LinearLayoutManager.VERTICAL:
                outRect.set(0, 0, 0, size);
                break;
            default:
                outRect.set(0, 0, size, 0);
                break;
        }
    }
}
