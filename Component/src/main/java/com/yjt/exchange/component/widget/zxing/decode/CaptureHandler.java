package com.hynet.heebit.components.widget.zxing.decode;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.hynet.heebit.components.R;
import com.hynet.heebit.components.widget.lock.State;
import com.hynet.heebit.components.utils.LogUtil;
import com.hynet.heebit.components.widget.zxing.camera.CameraManager;
import com.hynet.heebit.components.widget.zxing.listener.OnDecodeHandlerListener;
import com.hynet.heebit.components.widget.zxing.view.ViewfinderResultPointCallback;

import java.util.Collection;
import java.util.Map;

public final class CaptureHandler extends Handler {

    private final OnDecodeHandlerListener onDecodeHandlerListener;
    private final DecodeThread decodeThread;
    private State state;
    private final CameraManager cameraManager;

    public CaptureHandler(OnDecodeHandlerListener onDecodeHandlerListener, Collection<BarcodeFormat> decodeFormats, Map<DecodeHintType, Object> baseHints, String characterSet, CameraManager cameraManager) {
        this.onDecodeHandlerListener = onDecodeHandlerListener;
        this.decodeThread = new DecodeThread(onDecodeHandlerListener, decodeFormats, baseHints, characterSet, new ViewfinderResultPointCallback(onDecodeHandlerListener.getViewfinderView()));
        decodeThread.start();
        this.state = State.SUCCESS;
        this.cameraManager = cameraManager;
        cameraManager.startPreview();
        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message message) {
        if (message.what == R.id.restart_preview) {
             LogUtil.Companion.getInstance().print("Got restart preview message");
            restartPreviewAndDecode();
        } else if (message.what == R.id.decode_succeeded) {
             LogUtil.Companion.getInstance().print("Got decode succeeded message");
            state = State.SUCCESS;
            Bundle bundle = message.getData();
            Bitmap barcode = null;
            float scaleFactor = 1.0f;
            if (bundle != null) {
                byte[] compressedBitmap = bundle.getByteArray(DecodeThread.BARCODE_BITMAP);
                if (compressedBitmap != null) {
                    barcode = BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.length, null);
                    barcode = barcode.copy(Bitmap.Config.ARGB_8888, true);
                }
                scaleFactor = bundle.getFloat(DecodeThread.BARCODE_SCALED_FACTOR);
            }
            onDecodeHandlerListener.handleDecode((Result) message.obj, barcode, scaleFactor);
//                onDecodeHandlerListener.handleDecode((Result) message.obj/*, barcode*/);
        } else if (message.what == R.id.decode_failed) {
             LogUtil.Companion.getInstance().print("Got decode failed message");
            // We're decoding as fast as possible, so when one decode fails, start another.
            state = State.PREVIEW;
            cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
        } else if (message.what == R.id.return_scan_result) {
             LogUtil.Companion.getInstance().print("Got return scan result message");
            onDecodeHandlerListener.returnScanResult(Activity.RESULT_OK, (Intent) message.obj);
        } else if (message.what == R.id.launch_product_query) {
             LogUtil.Companion.getInstance().print("Got product query message");
            onDecodeHandlerListener.launchProductQuery((String) message.obj);
        }
    }

    public void quitSynchronously() {
        state = State.DONE;
        cameraManager.stopPreview();
        Message message = Message.obtain(decodeThread.getHandler(), R.id.quit);
        message.sendToTarget();
        try {
            decodeThread.join(500L);
        } catch (InterruptedException e) {
            // continue
        }
        removeMessages(R.id.decode_succeeded);
        removeMessages(R.id.decode_failed);
    }

    private void restartPreviewAndDecode() {
        if (state == State.SUCCESS) {
            state = State.PREVIEW;
            cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
            onDecodeHandlerListener.drawViewfinder();
        }
    }

//    private final OnDecodeHandlerListener onDecodeHandlerListener;
//    private final DecodeThread decodeThread;
//    private State state;
//
//    public CaptureHandler(OnDecodeHandlerListener listener, Vector<BarcodeFormat> decodeFormats, String characterSet) {
//        this.onDecodeHandlerListener = listener;
//        decodeThread = new DecodeThread(onDecodeHandlerListener, decodeFormats, characterSet, new ViewfinderResultPointCallback(onDecodeHandlerListener.getViewfinderView()));
//        decodeThread.start();
//        state = State.SUCCESS;
//        cameraManager.startPreview();
//        restartPreviewAndDecode();
//    }
//
//    @Override
//    public void handleMessage(Message message) {
//        if (message.what == R.id.auto_focus) {
//            if (state == State.PREVIEW) {
//                cameraManager.requestAutoFocus(this, R.id.auto_focus);
//            }
//        } else if (message.what == R.id.restart_preview) {
//             LogUtil.Companion.getInstance().print("Got restart preview message");
//            restartPreviewAndDecode();
//        } else if (message.what == R.id.decode_succeeded) {
//             LogUtil.Companion.getInstance().print("Got decode succeeded message");
//            state = State.SUCCESS;
//            onDecodeHandlerListener.handleDecode((Result) message.obj, null, 0);
//        } else if (message.what == R.id.decode_failed) {
//            state = State.PREVIEW;
//            cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
//        } else if (message.what == R.id.return_scan_result) {
//             LogUtil.Companion.getInstance().print("Got return scan result message");
//            onDecodeHandlerListener.returnScanResult(Activity.RESULT_OK, (Intent) message.obj);
//        } else if (message.what == R.id.launch_product_query) {
//             LogUtil.Companion.getInstance().print("Got product query message");
//            onDecodeHandlerListener.launchProductQuery((String) message.obj);
//        }
//    }
//
//    public void quitSynchronously() {
//        state = State.DONE;
//        cameraManager.stopPreview();
//        Message quit = Message.obtain(decodeThread.getHandler(), R.id.quit);
//        quit.sendToTarget();
//        try {
//            decodeThread.join();
//        } catch (InterruptedException e) {
//            // continue
//        }
//        removeMessages(R.id.decode_succeeded);
//        removeMessages(R.id.decode_failed);
//    }
//
//    private void restartPreviewAndDecode() {
//        if (state == State.SUCCESS) {
//            state = State.PREVIEW;
//            cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
//            cameraManager.requestAutoFocus(this, R.id.auto_focus);
//            onDecodeHandlerListener.drawViewfinder();
//        }
//    }
}
