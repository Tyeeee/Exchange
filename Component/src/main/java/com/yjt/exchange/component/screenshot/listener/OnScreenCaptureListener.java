package com.hynet.heebit.components.screenshot.listener;

import android.graphics.Bitmap;

public interface OnScreenCaptureListener {
    
    void onCaptureStarted();
    
    void onCaptureFailed(Throwable throwable);

    void onCaptureSuccess(Bitmap bitmap);
}
