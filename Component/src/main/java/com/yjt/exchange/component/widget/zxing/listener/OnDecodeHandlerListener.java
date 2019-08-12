package com.hynet.heebit.components.widget.zxing.listener;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;

import com.google.zxing.Result;
import com.hynet.heebit.components.widget.zxing.view.ViewfinderView;


public interface OnDecodeHandlerListener {

    void drawViewfinder();

    ViewfinderView getViewfinderView();

    Handler getHandler();

    void handleDecode(Result result, Bitmap barcode, float scaleFactor);

    void returnScanResult(int resultCode, Intent data);

    void launchProductQuery(String url);
}
