package com.hynet.heebit.components.widget.actionsheet;

import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hynet.heebit.components.R;
import com.hynet.heebit.components.widget.actionsheet.listener.OnMenuItemClickListener;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class ActionSheetMenu extends BottomSheetDialog implements OnMenuItemClickListener {

    BottomSheetBehavior.BottomSheetCallback bottomSheetCallback;
    BottomSheetBehavior bottomSheetBehavior;
    private OnMenuItemClickListener onMenuItemClickListener;
    private AppBarLayout appBarLayout;
    private boolean expandOnStart;
    boolean requestedExpand;
    boolean clicked;
    boolean requestCancel;
    boolean requestDismiss;
    OnCancelListener onCancelListener;

    public ActionSheetMenu(Context context) {
        super(context);
    }

    public ActionSheetMenu(Context context, int theme) {
        super(context, theme);
    }

    public void dismissWithAnimation() {
        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    @Override
    public void setOnCancelListener(OnCancelListener listener) {
        super.setOnCancelListener(listener);
        onCancelListener = listener;
    }

    @Override
    public void cancel() {
        requestCancel = true;
        super.cancel();
    }

    @Override
    public void dismiss() {
        requestDismiss = true;
        if (requestCancel) {
            dismissWithAnimation();
        } else {
            super.dismiss();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        final FrameLayout sheet = findViewById(R.id.design_bottom_sheet);
        if (sheet != null) {
            bottomSheetBehavior = BottomSheetBehavior.from(sheet);
            bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

                @Override
                public void onStateChanged(@NonNull View bottomSheet, @BottomSheetBehavior.State int newState) {
                    if (bottomSheetCallback != null) {
                        bottomSheetCallback.onStateChanged(bottomSheet, newState);
                    }
                    //noinspection WrongConstant
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        bottomSheetBehavior.setBottomSheetCallback(null);
                        try {
                            ActionSheetMenu.super.dismiss();
                        } catch (IllegalArgumentException e) {
                            // Ignore exception handling
                        }

                        // User dragged the sheet.
                        if (!clicked && !requestDismiss && !requestCancel && onCancelListener != null) {
                            onCancelListener.onCancel(ActionSheetMenu.this);
                        }
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    if (bottomSheetCallback != null) {
                        bottomSheetCallback.onSlide(bottomSheet, slideOffset);
                    }
                }
            });
            if (getContext().getResources().getBoolean(R.bool.tablet_landscape)) {
                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) sheet.getLayoutParams();
                layoutParams.width = getContext().getResources().getDimensionPixelSize(R.dimen.dp_500);
                sheet.setLayoutParams(layoutParams);
            }
            // Make sure the sheet doesn't overlap the appbar
            if (appBarLayout != null) {
                if (appBarLayout.getHeight() == 0) {
                    appBarLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                        @Override
                        public void onGlobalLayout() {
                            applyAppBarLayoutMargin(sheet);
                        }
                    });
                } else {
                    applyAppBarLayoutMargin(sheet);
                }
            }
            if (expandOnStart) {
                sheet.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_SETTLING && requestedExpand) {
                            sheet.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                        requestedExpand = true;
                    }
                });
            } else if (getContext().getResources().getBoolean(R.bool.landscape)) {
                fixLandscapePeekHeight(sheet);
            }
        }
    }

    public void setAppBarLayout(AppBarLayout appBarLayout) {
        this.appBarLayout = appBarLayout;
    }

    public void expandOnStart(boolean expand) {
        expandOnStart = expand;
    }

    public void setBottomSheetCallback(BottomSheetBehavior.BottomSheetCallback callback) {
        bottomSheetCallback = callback;
    }

    public void setBottomSheetItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        this.onMenuItemClickListener = onMenuItemClickListener;
    }

    public BottomSheetBehavior getBehavior() {
        return bottomSheetBehavior;
    }

    @Override
    public void onMenuItemClick(MenuItem menuItem) {
        if (!clicked) {
            if (bottomSheetBehavior != null) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
            if (onMenuItemClickListener != null) {
                onMenuItemClickListener.onMenuItemClick(menuItem);
            }
            clicked = true;
        }
    }

    private void fixLandscapePeekHeight(final View sheet) {
        // On landscape, we shouldn't use the 16:9 keyline alignment
        final int peek = sheet.getResources()
                .getDimensionPixelOffset(R.dimen.dp_400);
        if (sheet.getHeight() != 0) {
            bottomSheetBehavior.setPeekHeight(Math.max(sheet.getHeight() / 2, peek));
        } else {
            sheet.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (sheet.getHeight() > 0) {
                        sheet.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        bottomSheetBehavior.setPeekHeight(Math.max(sheet.getHeight() / 2, peek));
                    }
                }
            });
        }
    }

    private void applyAppBarLayoutMargin(View sheet) {
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) sheet.getLayoutParams();
        layoutParams.topMargin = appBarLayout.getHeight();
        sheet.setLayoutParams(layoutParams);
    }
}
