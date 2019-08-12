package com.hynet.heebit.components.widget.zxing.listener;

import android.graphics.Bitmap;

import com.google.zxing.Result;


public interface OnScannerCompletionListener {
   
    void onScannerCompletion(Result result, Bitmap barcode);
}
