package com.hynet.heebit.components.widget.badge.listener;

import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.view.View;

public interface Badge {

    Badge setBadgeNumber(int badgeNumber);

    int getBadgeNumber();

    Badge setBadgeText(String badgeText);

    String getBadgeText();

    Badge setExactMode(boolean isExact);

    boolean isExactMode();

    Badge setShowShadow(boolean showShadow);

    boolean isShowShadow();

    Badge setBadgeSizeMultiple(int sizeMultiple);

    Badge setBadgeBackgroundColor(int color);

    Badge stroke(int color, float width, boolean isDpValue);

    int getBadgeBackgroundColor();

    Badge setBadgeBackground(Drawable drawable);

    Badge setBadgeBackground(Drawable drawable, boolean clip);

    Drawable getBadgeBackground();

    Badge setBadgeTextColor(int color);

    int getBadgeTextColor();

    Badge setBadgeTextSize(float size, boolean isSpValue);

    float getBadgeTextSize(boolean isSpValue);

    Badge setBadgePadding(float padding, boolean isDpValue);

    float getBadgePadding(boolean isDpValue);

    boolean isDraggable();

    Badge setBadgeGravity(int gravity);

    int getBadgeGravity();

    Badge setOffset(float offset, boolean isDpValue);

    Badge setOffset(float offsetX, float offsetY, boolean isDpValue);

    float getOffsetX(boolean isDpValue);

    float getOffsetY(boolean isDpValue);

    Badge setOnDragStateChangedListener(OnDragStateChangedListener onDragStateChangedListener);

    PointF getDragCenter();

    Badge bindTarget(View view);

    View getTargetView();

    void hide(boolean animate);
}
