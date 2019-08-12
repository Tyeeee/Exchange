package com.hynet.heebit.components.widget.badge;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;

import java.lang.ref.WeakReference;

public class BadgeAnimator extends ValueAnimator {

    private BitmapFragment[][] bitmapFragments;
    private WeakReference<BadgeView> badgeViews;

    public BadgeAnimator(Bitmap bitmap, PointF pointF, BadgeView badgeView) {
        badgeViews = new WeakReference<>(badgeView);
        setFloatValues(0f, 1f);
        setDuration(500);
        bitmapFragments = getBitmapFragments(bitmap, pointF);
        addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                BadgeView badgeView = badgeViews.get();
                if (badgeView == null || !badgeView.isShown()) {
                    cancel();
                } else {
                    badgeView.invalidate();
                }
            }
        });
        addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                BadgeView badgeView = badgeViews.get();
                if (badgeView != null) {
                    badgeView.reset();
                }
            }
        });
    }

    public void draw(Canvas canvas) {
        for (int i = 0; i < bitmapFragments.length; i++) {
            for (int j = 0; j < bitmapFragments[i].length; j++) {
                bitmapFragments[i][j].updata(Float.parseFloat(getAnimatedValue().toString()), canvas);
            }
        }
    }
    
    private BitmapFragment[][] getBitmapFragments(Bitmap bitmap, PointF pointF) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float fragmentSize = Math.min(width, height) / 6f;
        float startX = pointF.x - bitmap.getWidth() / 2f;
        float startY = pointF.y - bitmap.getHeight() / 2f;
        BitmapFragment[][] bitmapFragments = new BitmapFragment[(int) (height / fragmentSize)][(int) (width / fragmentSize)];
        for (int i = 0; i < bitmapFragments.length; i++) {
            for (int j = 0; j < bitmapFragments[i].length; j++) {
                BitmapFragment bitmapFragment = new BitmapFragment();
                bitmapFragment.setColor(bitmap.getPixel((int) (j * fragmentSize), (int) (i * fragmentSize)));
                bitmapFragment.setX(startX + j * fragmentSize);
                bitmapFragment.setY(startY + i * fragmentSize);
                bitmapFragment.setSize(fragmentSize);
                bitmapFragment.setMaxSize(Math.max(width, height));
                bitmapFragments[i][j] = bitmapFragment;
            }
        }
        bitmap.recycle();
        return bitmapFragments;
    }
}
