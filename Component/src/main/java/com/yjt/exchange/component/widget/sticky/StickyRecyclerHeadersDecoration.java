package com.hynet.heebit.components.widget.sticky;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.SparseArray;
import android.view.View;

import com.hynet.heebit.components.widget.sticky.listener.HeaderProvider;
import com.hynet.heebit.components.widget.sticky.listener.OrientationProvider;
import com.hynet.heebit.components.widget.sticky.listener.StickyRecyclerHeadersAdapter;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class StickyRecyclerHeadersDecoration extends RecyclerView.ItemDecoration {

    private final StickyRecyclerHeadersAdapter stickyRecyclerHeadersAdapter;
    private final SparseArray<Rect> headerRects = new SparseArray<>();
    private final HeaderProvider headerProvider;
    private final OrientationProvider orientationProvider;
    private final HeaderPositionCalculator headerPositionCalculator;
    private final HeaderRenderer headerRenderer;
    private final DimensionCalculator dimensionCalculator;

    /**
     * The following field is used as a buffer for internal calculations. Its sole purpose is to avoid
     * allocating new Rect every time we need one.
     */
    private final Rect rect = new Rect();

    // TODO: Consider passing in orientation to simplify orientation accounting within calculation
    public StickyRecyclerHeadersDecoration(StickyRecyclerHeadersAdapter adapter) {
        this(adapter, new LinearLayoutOrientationProvider(), new DimensionCalculator());
    }

    private StickyRecyclerHeadersDecoration(StickyRecyclerHeadersAdapter adapter, OrientationProvider orientationProvider, DimensionCalculator dimensionCalculator) {
        this(adapter, orientationProvider, dimensionCalculator, new HeaderRenderer(orientationProvider),
                new HeaderViewCache(adapter, orientationProvider));
    }

    private StickyRecyclerHeadersDecoration(StickyRecyclerHeadersAdapter adapter, OrientationProvider orientationProvider,
                                            DimensionCalculator dimensionCalculator, HeaderRenderer headerRenderer, HeaderProvider headerProvider) {
        this(adapter, headerRenderer, orientationProvider, dimensionCalculator, headerProvider,
                new HeaderPositionCalculator(adapter, headerProvider, orientationProvider,
                        dimensionCalculator));
    }

    private StickyRecyclerHeadersDecoration(StickyRecyclerHeadersAdapter adapter, HeaderRenderer headerRenderer,
                                            OrientationProvider orientationProvider, DimensionCalculator dimensionCalculator, HeaderProvider headerProvider,
                                            HeaderPositionCalculator headerPositionCalculator) {
        stickyRecyclerHeadersAdapter = adapter;
        this.headerProvider = headerProvider;
        this.orientationProvider = orientationProvider;
        this.headerRenderer = headerRenderer;
        this.dimensionCalculator = dimensionCalculator;
        this.headerPositionCalculator = headerPositionCalculator;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int itemPosition = parent.getChildAdapterPosition(view);
        if (itemPosition == RecyclerView.NO_POSITION) {
            return;
        }
        if (headerPositionCalculator.hasNewHeader(itemPosition, orientationProvider.isReverseLayout(parent))) {
            View header = getHeaderView(parent, itemPosition);
            setItemOffsetsForHeader(outRect, header, orientationProvider.getOrientation(parent));
        }
    }

    /**
     * Sets the offsets for the first item in a section to make room for the header view
     *
     * @param itemOffsets rectangle to define offsets for the item
     * @param header      view used to calculate offset for the item
     * @param orientation used to calculate offset for the item
     */
    private void setItemOffsetsForHeader(Rect itemOffsets, View header, int orientation) {
        dimensionCalculator.initMargins(rect, header);
        if (orientation == LinearLayoutManager.VERTICAL) {
            itemOffsets.top = header.getHeight() + rect.top + rect.bottom;
        } else {
            itemOffsets.left = header.getWidth() + rect.left + rect.right;
        }
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);

        final int childCount = parent.getChildCount();
        if (childCount <= 0 || stickyRecyclerHeadersAdapter.getItemCount() <= 0) {
            return;
        }

        for (int i = 0; i < childCount; i++) {
            View itemView = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(itemView);
            if (position == RecyclerView.NO_POSITION) {
                continue;
            }

            boolean hasStickyHeader = headerPositionCalculator.hasStickyHeader(itemView, orientationProvider.getOrientation(parent), position);
            if (hasStickyHeader || headerPositionCalculator.hasNewHeader(position, orientationProvider.isReverseLayout(parent))) {
                View header = headerProvider.getHeader(parent, position);
                //re-use existing Rect, if any.
                Rect headerOffset = headerRects.get(position);
                if (headerOffset == null) {
                    headerOffset = new Rect();
                    headerRects.put(position, headerOffset);
                }
                headerPositionCalculator.initHeaderBounds(headerOffset, parent, header, itemView, hasStickyHeader);
                headerRenderer.drawHeader(parent, canvas, header, headerOffset);
            }
        }
    }

    /**
     * Gets the position of the header under the specified (x, y) coordinates.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @return position of header, or -1 if not found
     */
    public int findHeaderPositionUnder(int x, int y) {
        for (int i = 0; i < headerRects.size(); i++) {
            Rect rect = headerRects.get(headerRects.keyAt(i));
            if (rect.contains(x, y)) {
                return headerRects.keyAt(i);
            }
        }
        return -1;
    }

    /**
     * Gets the header view for the associated position.  If it doesn't exist yet, it will be
     * created, measured, and laid out.
     *
     * @param parent
     * @param position
     * @return Header view
     */
    public View getHeaderView(RecyclerView parent, int position) {
        return headerProvider.getHeader(parent, position);
    }

    /**
     * Invalidates cached headers.  This does not invalidate the recyclerview, you should do that manually after
     * calling this method.
     */
    public void invalidateHeaders() {
        headerProvider.invalidate();
    }
}
