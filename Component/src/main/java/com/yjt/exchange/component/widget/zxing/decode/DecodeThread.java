package com.hynet.heebit.components.widget.zxing.decode;

import android.os.Handler;
import android.os.Looper;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ResultPointCallback;
import com.hynet.heebit.components.utils.LogUtil;
import com.hynet.heebit.components.widget.zxing.listener.OnDecodeHandlerListener;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

final class DecodeThread extends Thread {

    public static final String BARCODE_BITMAP = "barcode_bitmap";
    public static final String BARCODE_SCALED_FACTOR = "barcode_scaled_factor";
    private final OnDecodeHandlerListener onDecodeHandlerListener;
    private final Map<DecodeHintType, Object> hints;
    private Handler handler;
    private final CountDownLatch handlerInitLatch;

    DecodeThread(OnDecodeHandlerListener onDecodeHandlerListener, Collection<BarcodeFormat> decodeFormats, Map<DecodeHintType, Object> baseHints, String characterSet, ResultPointCallback resultPointCallback) {
        this.onDecodeHandlerListener = onDecodeHandlerListener;
        handlerInitLatch = new CountDownLatch(1);
        hints = new EnumMap<>(DecodeHintType.class);
        if (baseHints != null) {
            hints.putAll(baseHints);
        }
//        // The prefs can't change while the thread is running, so pick them up once here.
//        if (decodeFormats == null || decodeFormats.isEmpty()) {
//            //todp
//            decodeFormats = EnumSet.noneOf(BarcodeFormat.class);
//            decodeFormats.addAll(DecodeFormatManager.PRODUCT_FORMATS);
//            decodeFormats.addAll(DecodeFormatManager.INDUSTRIAL_FORMATS);
//            decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
//            decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
//            decodeFormats.addAll(DecodeFormatManager.AZTEC_FORMATS);
//            decodeFormats.addAll(DecodeFormatManager.PDF417_FORMATS);
//        }
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        if (characterSet != null) {
            hints.put(DecodeHintType.CHARACTER_SET, characterSet);
        }
        hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, resultPointCallback);
        LogUtil.Companion.getInstance().print("Hints: " + hints);
    }

    Handler getHandler() {
        try {
            handlerInitLatch.await();
        } catch (InterruptedException ie) {
            // continue?
        }
        return handler;
    }

    @Override
    public void run() {
        Looper.prepare();
        handler = new DecodeHandler(onDecodeHandlerListener, hints);
        handlerInitLatch.countDown();
        Looper.loop();
    }

//    public static final String BARCODE_BITMAP = "barcode_bitmap";
//    private final OnDecodeHandlerListener mListener;
//    private final Hashtable<DecodeHintType, Object> hints;
//    private Handler handler;
//    private final CountDownLatch handlerInitLatch;
//
//    DecodeThread(OnDecodeHandlerListener listener, Vector<BarcodeFormat> decodeFormats, String characterSet, ResultPointCallback resultPointCallback) {
//        this.mListener = listener;
//        handlerInitLatch = new CountDownLatch(1);
//
//        hints = new Hashtable<>(3);
//
//        if (decodeFormats == null || decodeFormats.isEmpty()) {
//            decodeFormats = new Vector<>();
//            decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
//            decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
//            decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
//        }
//
//        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
//
//        if (characterSet != null) {
//            hints.put(DecodeHintType.CHARACTER_SET, characterSet);
//        }
//
//        hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, resultPointCallback);
//    }
//
//    Handler getHandler() {
//        try {
//            handlerInitLatch.await();
//        } catch (InterruptedException ie) {
//            // continue?
//        }
//        return handler;
//    }
//
//    @Override
//    public void run() {
//        Looper.prepare();
//        handler = new DecodeHandler(mListener, hints);
//        handlerInitLatch.countDown();
//        Looper.loop();
//    }
}
