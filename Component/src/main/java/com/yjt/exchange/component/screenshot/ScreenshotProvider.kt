package com.hynet.heebit.components.screenshot

import android.app.Activity
import android.graphics.Bitmap
import android.view.View

import rx.Observable

class ScreenshotProvider {

    fun getScreenshotBitmap(activity: Activity, removedViews: Array<out View>): Observable<Bitmap> = ViewsBitmapObservable[activity, removedViews]
}
