package com.hynet.heebit.components.widget.zxing.camera;

import android.graphics.Point;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;


final class PreviewCallback implements Camera.PreviewCallback {

    private final CameraConfigurationManager cameraConfigurationManager;
    private Handler previewHandler;
    private int previewMessage;

    PreviewCallback(CameraConfigurationManager cameraConfigurationManager) {
        this.cameraConfigurationManager = cameraConfigurationManager;
    }

    void setHandler(Handler previewHandler, int previewMessage) {
        this.previewHandler = previewHandler;
        this.previewMessage = previewMessage;
    }

    public void onPreviewFrame(byte[] data, Camera camera) {
//        Point cameraResolution = cameraConfigurationManager.getCameraResolution();
//        if (cameraResolution != null && previewHandler != null) {
//            Message message = previewHandler.obtainMessage(previewMessage, cameraResolution.x, cameraResolution.y, data);
//            message.sendToTarget();
//            previewHandler = null;
//        } else {
//             LogUtil.Companion.getInstance().print("Got preview callback, but no handler for it");
//        }
        Point cameraResolution = cameraConfigurationManager.getCameraResolution();
        Handler handler = previewHandler;
        if (cameraResolution != null && handler != null) {
            Point screenResolution = cameraConfigurationManager.getScreenResolution();
            Message message;
            if (screenResolution.x < screenResolution.y) {
                // portrait
                message = handler.obtainMessage(previewMessage, cameraResolution.y, cameraResolution.x, data);
            } else {
                // landscape
                message = handler.obtainMessage(previewMessage, cameraResolution.x, cameraResolution.y, data);
            }
            message.sendToTarget();
        }
    }

//    private final CameraConfigurationManager cameraConfigurationManager;
//    private final boolean useOneShotPreviewCallback;
//    private Handler previewHandler;
//    private int previewMessage;
//
//    PreviewCallback(CameraConfigurationManager cameraConfigurationManager, boolean useOneShotPreviewCallback) {
//        this.cameraConfigurationManager = cameraConfigurationManager;
//        this.useOneShotPreviewCallback = useOneShotPreviewCallback;
//    }
//
//    void setHandler(Handler previewHandler, int previewMessage) {
//        this.previewHandler = previewHandler;
//        this.previewMessage = previewMessage;
//    }
//
//    public void onPreviewFrame(byte[] data, Camera camera) {
//        Point cameraResolution = cameraConfigurationManager.getCameraResolution();
//        if (!useOneShotPreviewCallback) {
//            camera.setPreviewCallback(null);
//        }
//        if (previewHandler != null) {
//            Message message = previewHandler.obtainMessage(previewMessage, cameraResolution.x, cameraResolution.y, data);
//            message.sendToTarget();
//            previewHandler = null;
//        } else {
//             LogUtil.Companion.getInstance().print("Got preview callback, but no handler for it");
//        }
//    }
}
