package com.hynet.heebit.components.widget.zxing.camera;

import android.graphics.Rect;

import java.util.List;

public class DisplayConfiguration {

    private Size viewfinderSize;
    private int rotation;
    private boolean center = false;
    private PreviewScalingStrategy previewScalingStrategy = new FitCenterStrategy();

    public DisplayConfiguration(int rotation) {
        this.rotation = rotation;
    }

    public DisplayConfiguration(int rotation, Size viewfinderSize) {
        this.rotation = rotation;
        this.viewfinderSize = viewfinderSize;
    }

    public int getRotation() {
        return rotation;
    }

    public Size getViewfinderSize() {
        return viewfinderSize;
    }

    public PreviewScalingStrategy getPreviewScalingStrategy() {
        return previewScalingStrategy;
    }

    public void setPreviewScalingStrategy(PreviewScalingStrategy previewScalingStrategy) {
        this.previewScalingStrategy = previewScalingStrategy;
    }

    public Size getDesiredPreviewSize(boolean rotate) {
        if (viewfinderSize == null) {
            return null;
        } else if (rotate) {
            return viewfinderSize.rotate();
        } else {
            return viewfinderSize;
        }
    }

    public Size getBestPreviewSize(List<Size> sizes, boolean isRotated) {
        return previewScalingStrategy.getBestPreviewSize(sizes, getDesiredPreviewSize(isRotated));
    }

    public Rect scalePreview(Size previewSize) {
        return previewScalingStrategy.scalePreview(previewSize, viewfinderSize);
    }
}
