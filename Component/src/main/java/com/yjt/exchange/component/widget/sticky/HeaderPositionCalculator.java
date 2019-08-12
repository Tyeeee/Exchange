package com.hynet.heebit.components.widget.sticky;

import android.graphics.Rect;
import android.view.View;
import android.widget.LinearLayout;

import com.hynet.heebit.components.widget.sticky.listener.HeaderProvider;
import com.hynet.heebit.components.widget.sticky.listener.OrientationProvider;
import com.hynet.heebit.components.widget.sticky.listener.StickyRecyclerHeadersAdapter;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * Calculates the position and location of header views
 */
public class HeaderPositionCalculator {

    private final StickyRecyclerHeadersAdapter stickyRecyclerHeadersAdapter;
    private final OrientationProvider orientationProvider;
    private final HeaderProvider headerProvider;
    private final DimensionCalculator dimensionCalculator;

    /**
     * The following fields are used as buffers for internal calculations. Their sole purpose is to avoid
     * allocating new Rect every time we need one.
     */
    private final Rect rect1 = new Rect();
    private final Rect rect2 = new Rect();

    public HeaderPositionCalculator(StickyRecyclerHeadersAdapter stickyRecyclerHeadersAdapter, HeaderProvider headerProvider,
                                    OrientationProvider orientationProvider, DimensionCalculator dimensionCalculator) {
        this.stickyRecyclerHeadersAdapter = stickyRecyclerHeadersAdapter;
        this.headerProvider = headerProvider;
        this.orientationProvider = orientationProvider;
        this.dimensionCalculator = dimensionCalculator;
    }

    /**
     * Determines if a view should have a sticky header.
     * The view has a sticky header if:
     * 1. It is the first element in the recycler view
     * 2. It has a valid ID associated to its position
     *
     * @param itemView    given by the RecyclerView
     * @param orientation of the Recyclerview
     * @param position    of the list item in question
     *
     * @return True if the view should have a sticky header
     */
    public boolean hasStickyHeader(View itemView, int orientation, int position) {
        int offset, margin;
        dimensionCalculator.initMargins(rect1, itemView);
        if (orientation == LinearLayout.VERTICAL) {
            offset = itemView.getTop();
            margin = rect1.top;
        } else {
            offset = itemView.getLeft();
            margin = rect1.left;
        }
        return offset <= margin && stickyRecyclerHeadersAdapter.getHeaderId(position) >= 0;
    }

    /**
     * Determines if an item in the list should have a header that is different than the item in the
     * list that immediately precedes it. Items with no headers will always return false.
     *
     * @param position        of the list item in questions
     * @param isReverseLayout TRUE if layout manager has flag isReverseLayout
     *
     * @return true if this item has a different header than the previous item in the list
     *
     * @see {@link StickyRecyclerHeadersAdapter#getHeaderId(int)}
     */
    public boolean hasNewHeader(int position, boolean isReverseLayout) {
        if (indexOutOfBounds(position)) {
            return false;
        }

        long headerId = stickyRecyclerHeadersAdapter.getHeaderId(position);

        if (headerId < 0) {
            return false;
        }

        long nextItemHeaderId = -1;
        int nextItemPosition = position + (isReverseLayout ? 1 : -1);
        if (!indexOutOfBounds(nextItemPosition)) {
            nextItemHeaderId = stickyRecyclerHeadersAdapter.getHeaderId(nextItemPosition);
        }
        int firstItemPosition = isReverseLayout ? stickyRecyclerHeadersAdapter.getItemCount() - 1 : 0;

        return position == firstItemPosition || headerId != nextItemHeaderId;
    }

    private boolean indexOutOfBounds(int position) {
        return position < 0 || position >= stickyRecyclerHeadersAdapter.getItemCount();
    }

    public void initHeaderBounds(Rect bounds, RecyclerView recyclerView, View header, View firstView, boolean firstHeader) {
        int orientation = orientationProvider.getOrientation(recyclerView);
        initDefaultHeaderOffset(bounds, recyclerView, header, firstView, orientation);

        if (firstHeader && isStickyHeaderBeingPushedOffscreen(recyclerView, header)) {
            View viewAfterNextHeader = getFirstViewUnobscuredByHeader(recyclerView, header);
            int firstViewUnderHeaderPosition = recyclerView.getChildAdapterPosition(viewAfterNextHeader);
            View secondHeader = headerProvider.getHeader(recyclerView, firstViewUnderHeaderPosition);
            translateHeaderWithNextHeader(recyclerView, orientationProvider.getOrientation(recyclerView), bounds,
                                          header, viewAfterNextHeader, secondHeader);
        }
    }

    private void initDefaultHeaderOffset(Rect headerMargins, RecyclerView recyclerView, View header, View firstView, int orientation) {
        int translationX, translationY;
        dimensionCalculator.initMargins(rect1, header);
        if (orientation == LinearLayoutManager.VERTICAL) {
            translationX = firstView.getLeft() + rect1.left;
            translationY = Math.max(
                    firstView.getTop() - header.getHeight() - rect1.bottom,
                    getListTop(recyclerView) + rect1.top);
        } else {
            translationY = firstView.getTop() + rect1.top;
            translationX = Math.max(
                    firstView.getLeft() - header.getWidth() - rect1.right,
                    getListLeft(recyclerView) + rect1.left);
        }

        headerMargins.set(translationX, translationY, translationX + header.getWidth(),
                          translationY + header.getHeight());
    }

    private boolean isStickyHeaderBeingPushedOffscreen(RecyclerView recyclerView, View stickyHeader) {
        View viewAfterHeader = getFirstViewUnobscuredByHeader(recyclerView, stickyHeader);
        if (viewAfterHeader != null) {
            int firstViewUnderHeaderPosition = recyclerView.getChildAdapterPosition(viewAfterHeader);
            if (firstViewUnderHeaderPosition == RecyclerView.NO_POSITION) {
                return false;
            }
            boolean isReverseLayout = orientationProvider.isReverseLayout(recyclerView);
            if (firstViewUnderHeaderPosition > 0 && hasNewHeader(firstViewUnderHeaderPosition, isReverseLayout)) {
                View nextHeader = headerProvider.getHeader(recyclerView, firstViewUnderHeaderPosition);
                dimensionCalculator.initMargins(rect1, nextHeader);
                dimensionCalculator.initMargins(rect2, stickyHeader);

                if (orientationProvider.getOrientation(recyclerView) == LinearLayoutManager.VERTICAL) {
                    int topOfNextHeader = viewAfterHeader.getTop() - rect1.bottom - nextHeader.getHeight() - rect1.top;
                    int bottomOfThisHeader = recyclerView.getPaddingTop() + stickyHeader.getBottom() + rect2.top + rect2.bottom;
                    if (topOfNextHeader < bottomOfThisHeader) {
                        return true;
                    }
                } else {
                    int leftOfNextHeader = viewAfterHeader.getLeft() - rect1.right - nextHeader.getWidth() - rect1.left;
                    int rightOfThisHeader = recyclerView.getPaddingLeft() + stickyHeader.getRight() + rect2.left + rect2.right;
                    if (leftOfNextHeader < rightOfThisHeader) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void translateHeaderWithNextHeader(RecyclerView recyclerView, int orientation, Rect translation,
                                               View currentHeader, View viewAfterNextHeader, View nextHeader) {
        dimensionCalculator.initMargins(rect1, nextHeader);
        dimensionCalculator.initMargins(rect2, currentHeader);
        if (orientation == LinearLayoutManager.VERTICAL) {
            int topOfStickyHeader = getListTop(recyclerView) + rect2.top + rect2.bottom;
            int shiftFromNextHeader = viewAfterNextHeader.getTop() - nextHeader.getHeight() - rect1.bottom - rect1.top - currentHeader.getHeight() - topOfStickyHeader;
            if (shiftFromNextHeader < topOfStickyHeader) {
                translation.top += shiftFromNextHeader;
            }
        } else {
            int leftOfStickyHeader = getListLeft(recyclerView) + rect2.left + rect2.right;
            int shiftFromNextHeader = viewAfterNextHeader.getLeft() - nextHeader.getWidth() - rect1.right - rect1.left - currentHeader.getWidth() - leftOfStickyHeader;
            if (shiftFromNextHeader < leftOfStickyHeader) {
                translation.left += shiftFromNextHeader;
            }
        }
    }

    /**
     * Returns the first item currently in the RecyclerView that is not obscured by a header.
     *
     * @param parent Recyclerview containing all the list items
     *
     * @return first item that is fully beneath a header
     */
    private View getFirstViewUnobscuredByHeader(RecyclerView parent, View firstHeader) {
        boolean isReverseLayout = orientationProvider.isReverseLayout(parent);
        int step = isReverseLayout ? -1 : 1;
        int from = isReverseLayout ? parent.getChildCount() - 1 : 0;
        for (int i = from; i >= 0 && i <= parent.getChildCount() - 1; i += step) {
            View child = parent.getChildAt(i);
            if (!itemIsObscuredByHeader(parent, child, firstHeader, orientationProvider.getOrientation(parent))) {
                return child;
            }
        }
        return null;
    }

    /**
     * Determines if an item is obscured by a header
     *
     * @param parent
     * @param item        to determine if obscured by header
     * @param header      that might be obscuring the item
     * @param orientation of the {@link RecyclerView}
     *
     * @return true if the item view is obscured by the header view
     */
    private boolean itemIsObscuredByHeader(RecyclerView parent, View item, View header, int orientation) {
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) item.getLayoutParams();
        dimensionCalculator.initMargins(rect1, header);

        int adapterPosition = parent.getChildAdapterPosition(item);
        if (adapterPosition == RecyclerView.NO_POSITION || headerProvider.getHeader(parent, adapterPosition) != header) {
            // Resolves https://github.com/timehop/sticky-headers-recyclerview/issues/36
            // Handles an edge case where a trailing header is smaller than the current sticky header.
            return false;
        }

        if (orientation == LinearLayoutManager.VERTICAL) {
            int itemTop = item.getTop() - layoutParams.topMargin;
            int headerBottom = header.getBottom() + rect1.bottom + rect1.top;
            if (itemTop > headerBottom) {
                return false;
            }
        } else {
            int itemLeft = item.getLeft() - layoutParams.leftMargin;
            int headerRight = header.getRight() + rect1.right + rect1.left;
            if (itemLeft > headerRight) {
                return false;
            }
        }

        return true;
    }

    private int getListTop(RecyclerView view) {
        if (view.getLayoutManager().getClipToPadding()) {
            return view.getPaddingTop();
        } else {
            return 0;
        }
    }

    private int getListLeft(RecyclerView view) {
        if (view.getLayoutManager().getClipToPadding()) {
            return view.getPaddingLeft();
        } else {
            return 0;
        }
    }
}
