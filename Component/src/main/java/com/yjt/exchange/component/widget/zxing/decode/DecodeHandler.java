package com.hynet.heebit.components.widget.zxing.decode;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.hynet.heebit.components.R;
import com.hynet.heebit.components.utils.LogUtil;
import com.hynet.heebit.components.widget.zxing.camera.CameraManager;
import com.hynet.heebit.components.widget.zxing.camera.PlanarYUVLuminanceSource;
import com.hynet.heebit.components.widget.zxing.listener.OnDecodeHandlerListener;

import java.io.ByteArrayOutputStream;
import java.util.Map;

final class DecodeHandler extends Handler {

    private final OnDecodeHandlerListener onDecodeHandlerListener;
    private final MultiFormatReader multiFormatReader;
    private boolean running = true;

    DecodeHandler(OnDecodeHandlerListener onDecodeHandlerListener, Map<DecodeHintType, Object> hints) {
        this.onDecodeHandlerListener = onDecodeHandlerListener;
        this.multiFormatReader = new MultiFormatReader();
        multiFormatReader.setHints(hints);
    }

    @Override
    public void handleMessage(Message message) {
        if (message == null || !running) {
            return;
        }
        if (message.what == R.id.decode) {
            decode((byte[]) message.obj, message.arg1, message.arg2);
        }
        if (message.what == R.id.quit) {
            running = false;
            Looper.myLooper().quit();
        }
    }

    private void decode(byte[] data, int width, int height) {
        long start = System.currentTimeMillis();
        if (width < height) {
            byte[] rotatedData = new byte[data.length];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    rotatedData[y * width + width - x - 1] = data[y + x * height];
                }
            }
            data = rotatedData;
        }
        Result result = null;
        PlanarYUVLuminanceSource planarYUVLuminanceSource = CameraManager.get().buildLuminanceSource(data, width, height);
        if (planarYUVLuminanceSource != null) {
            try {
                result = multiFormatReader.decodeWithState(new BinaryBitmap(new HybridBinarizer(planarYUVLuminanceSource)));
                LogUtil.Companion.getInstance().print("decode result:" + result);
            } catch (ReaderException e) {
                // continue
            } finally {
                multiFormatReader.reset();
            }
        }
        Handler handler = onDecodeHandlerListener.getHandler();
        if (result != null) {
            // Don't log the barcode contents for security.
            long end = System.currentTimeMillis();
            LogUtil.Companion.getInstance().print("Found barcode in " + (end - start) + " ms");
            if (handler != null) {
                Message message = Message.obtain(handler, R.id.decode_succeeded, result);
                Bundle bundle = new Bundle();
                bundleThumbnail(planarYUVLuminanceSource, bundle);
                message.setData(bundle);
                message.sendToTarget();
            }
        } else {
            if (handler != null) {
                Message message = Message.obtain(handler, R.id.decode_failed);
                message.sendToTarget();
            }
        }
    }

    private static void bundleThumbnail(PlanarYUVLuminanceSource planarYUVLuminanceSource, Bundle bundle) {
        int[] pixels = planarYUVLuminanceSource.renderThumbnail();
        int width = planarYUVLuminanceSource.getThumbnailWidth();
        int height = planarYUVLuminanceSource.getThumbnailHeight();
        Bitmap bitmap = Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.ARGB_8888);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        bundle.putByteArray(DecodeThread.BARCODE_BITMAP, byteArrayOutputStream.toByteArray());
        bundle.putFloat(DecodeThread.BARCODE_SCALED_FACTOR, (float) width / planarYUVLuminanceSource.getWidth());
    }

//    private final OnDecodeHandlerListener onDecodeHandlerListener;
//    private final MultiFormatReader multiFormatReader;
//
//    DecodeHandler(OnDecodeHandlerListener listener, Hashtable<DecodeHintType, Object> hints) {
//        this.onDecodeHandlerListener = listener;
//        multiFormatReader = new MultiFormatReader();
//        multiFormatReader.setHints(hints);
//    }
//
//    @Override
//    public void handleMessage(Message message) {
//        if (message.what == R.id.decode) {
//            decode((byte[]) message.obj, message.arg1, message.arg2);
//        }
//        if (message.what == R.id.quit) {
//            Looper.myLooper().quit();
//        }
//    }
//
//    private void decode(byte[] data, int width, int height) {
//        long start = System.currentTimeMillis();
//        Result rawResult = null;
//
//        // modify here
//        byte[] rotatedData = new byte[data.length];
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++)
//                rotatedData[x * height + height - y - 1] = data[x + y * width];
//        }
//        int tmp = width; // Here we are swapping, that's the difference to #11
//        width = height;
//        height = tmp;
//        PlanarYUVLuminanceSource source = CameraManager.get().buildLuminanceSource(rotatedData, width, height);
//        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
//        try {
//            rawResult = multiFormatReader.decodeWithState(bitmap);
//        } catch (ReaderException re) {
//            // continue
//        } finally {
//            multiFormatReader.reset();
//        }
//
//        if (rawResult != null) {
//            long end = System.currentTimeMillis();
//             LogUtil.Companion.getInstance().print("Found barcode (" + (end - start) + " ms):\n" + rawResult.toString());
//            Message message = Message.obtain(onDecodeHandlerListener.getHandler(), R.id.decode_succeeded, rawResult);
//            Bundle bundle = new Bundle();
//            bundle.putParcelable(DecodeThread.BARCODE_BITMAP, source.renderCroppedGreyscaleBitmap());
//            message.setData(bundle);
//            message.sendToTarget();
//        } else {
//            Message message = Message.obtain(onDecodeHandlerListener.getHandler(), R.id.decode_failed);
//            message.sendToTarget();
//        }
//    }
}
