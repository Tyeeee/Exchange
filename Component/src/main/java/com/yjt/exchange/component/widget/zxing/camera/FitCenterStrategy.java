package com.hynet.heebit.components.widget.zxing.camera;

import android.graphics.Rect;

import com.hynet.heebit.components.utils.LogUtil;

public class FitCenterStrategy extends PreviewScalingStrategy {

    @Override
    protected float getScore(Size size, Size desired) {
        if (size.width <= 0 || size.height <= 0) {
            return 0f;
        }
        Size scaled = size.scaleFit(desired);
        float scaleRatio = scaled.width * 1.0f / size.width;
        float scaleScore;
        if (scaleRatio > 1.0f) {
            scaleScore = (float) Math.pow(1.0f / scaleRatio, 1.1);
        } else {
            scaleScore = scaleRatio;
        }
        float cropRatio = (desired.width * 1.0f / scaled.width) * (desired.height * 1.0f / scaled.height);
        float cropScore = 1.0f / cropRatio / cropRatio / cropRatio;
        return scaleScore * cropScore;
    }

    public Rect scalePreview(Size previewSize, Size viewfinderSize) {
        Size scaledPreview = previewSize.scaleFit(viewfinderSize);
         LogUtil.Companion.getInstance().print("Preview: " + previewSize + "; Scaled: " + scaledPreview + "; Want: " + viewfinderSize);
        int dx = (scaledPreview.width - viewfinderSize.width) / 2;
        int dy = (scaledPreview.height - viewfinderSize.height) / 2;
        return new Rect(-dx, -dy, scaledPreview.width - dx, scaledPreview.height - dy);
    }
}
