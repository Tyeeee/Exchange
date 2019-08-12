package com.hynet.heebit.components.widget.progressbar.listener;

public interface OnProgressUpdateListener {

    void onProgressUpdate(float degree);

    void onAnimationStarted();

    void onAnimationEnded();

    void onAnimationSuccess();

    void onAnimationFailed();

    void onManualProgressStarted();

    void onManualProgressEnded();
}
