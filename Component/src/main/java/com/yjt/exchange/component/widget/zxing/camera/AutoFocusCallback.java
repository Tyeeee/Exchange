package com.hynet.heebit.components.widget.zxing.camera;

import android.content.Context;
import android.hardware.Camera;
import android.os.AsyncTask;

import com.google.common.collect.Lists;
import com.hynet.heebit.components.utils.LogUtil;

import java.util.Collection;
import java.util.concurrent.RejectedExecutionException;

public final class AutoFocusCallback implements Camera.AutoFocusCallback {

    private static final long AUTO_FOCUS_INTERVAL_MS = 2000L;
    private static final Collection<String> FOCUS_MODES_CALLING_AF;

    static {
        FOCUS_MODES_CALLING_AF = Lists.newArrayListWithCapacity(2);
        FOCUS_MODES_CALLING_AF.add(Camera.Parameters.FOCUS_MODE_AUTO);
        FOCUS_MODES_CALLING_AF.add(Camera.Parameters.FOCUS_MODE_MACRO);
    }

    private boolean stopped;
    private boolean focusing;
    private final boolean useAutoFocus;
    private final Camera camera;
    private AsyncTask<?, ?, ?> outstandingTask;

    public AutoFocusCallback(Context context, Camera camera) {
        this.camera = camera;
        String currentFocusMode = camera.getParameters().getFocusMode();
        useAutoFocus = FOCUS_MODES_CALLING_AF.contains(currentFocusMode);
         LogUtil.Companion.getInstance().print("Current focus mode '" + currentFocusMode + "'; use auto focus? " + useAutoFocus);
        start();
    }

    public void onAutoFocus(boolean success, Camera camera) {
        focusing = false;
        autoFocusAgainLater();
    }

    private synchronized void autoFocusAgainLater() {
        if (!stopped && outstandingTask == null) {
            AutoFocusTask autoFocusTask = new AutoFocusTask();
            try {
                autoFocusTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                outstandingTask = autoFocusTask;
            } catch (RejectedExecutionException e) {
                 LogUtil.Companion.getInstance().print("Could not request auto focus " + e);
            }
        }
    }

    synchronized void start() {
        if (useAutoFocus) {
            outstandingTask = null;
            if (!stopped && !focusing) {
                try {
                    camera.autoFocus(this);
                    focusing = true;
                } catch (RuntimeException e) {
                    // Have heard RuntimeException reported in Android 4.0.x+; continue?
                     LogUtil.Companion.getInstance().print("Unexpected exception while focusing " + e);
                    // Try again later to keep cycle going
                    autoFocusAgainLater();
                }
            }
        }
    }

    private synchronized void cancelOutstandingTask() {
        if (outstandingTask != null) {
            if (outstandingTask.getStatus() != AsyncTask.Status.FINISHED) {
                outstandingTask.cancel(true);
            }
            outstandingTask = null;
        }
    }

    synchronized void stop() {
        stopped = true;
        if (useAutoFocus) {
            cancelOutstandingTask();
            try {
                camera.cancelAutoFocus();
            } catch (RuntimeException e) {
                 LogUtil.Companion.getInstance().print("Unexpected exception while cancelling focusing " + e);
            }
        }
    }

    private final class AutoFocusTask extends AsyncTask<Object, Object, Object> {

        @Override
        protected Object doInBackground(Object... voids) {
            try {
                Thread.sleep(AUTO_FOCUS_INTERVAL_MS);
            } catch (InterruptedException e) {
                // continue
            }
            start();
            return null;
        }
    }

//    private static final long AUTOFOCUS_INTERVAL_MS = 1500L;
//
//    private Handler autoFocusHandler;
//    private int autoFocusMessage;
//
//    void setHandler(Handler autoFocusHandler, int autoFocusMessage) {
//        this.autoFocusHandler = autoFocusHandler;
//        this.autoFocusMessage = autoFocusMessage;
//    }
//
//    public void onAutoFocus(boolean success, Camera camera) {
//        if (autoFocusHandler != null) {
//            Message message = autoFocusHandler.obtainMessage(autoFocusMessage, success);
//            autoFocusHandler.sendMessageDelayed(message, AUTOFOCUS_INTERVAL_MS);
//            autoFocusHandler = null;
//        } else {
//             LogUtil.Companion.getInstance().print("Got auto-focus callback, but no handler for it");
//        }
//    }
}
