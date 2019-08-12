package com.hynet.heebit.components.widget.zxing.camera;

import android.graphics.Rect;

import com.hynet.heebit.components.utils.LogUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class PreviewScalingStrategy {

    public Size getBestPreviewSize(List<Size> sizes, final Size desired) {
        List<Size> ordered = getBestPreviewOrder(sizes, desired);
         LogUtil.Companion.getInstance().print("Viewfinder size: " + desired);
         LogUtil.Companion.getInstance().print("Preview in order of preference: " + ordered);
        return ordered.get(0);
    }

    public List<Size> getBestPreviewOrder(List<Size> sizes, final Size desired) {
        if (desired == null) {
            return sizes;
        }
        Collections.sort(sizes, new Comparator<Size>() {
            @Override
            public int compare(Size a, Size b) {
                float aScore = getScore(a, desired);
                float bScore = getScore(b, desired);
                // Bigger score first
                return Float.compare(bScore, aScore);
            }

            @Override
            public boolean equals(Object obj) {
                return false;
            }
        });
        return sizes;
    }

    protected float getScore(Size size, Size desired) {
        return 0.5f;
    }

    public abstract Rect scalePreview(Size previewSize, Size viewfinderSize);
}
