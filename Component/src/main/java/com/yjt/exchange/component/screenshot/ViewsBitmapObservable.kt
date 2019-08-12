package com.hynet.heebit.components.screenshot

import android.app.Activity
import android.graphics.Bitmap
import android.view.View
import com.hynet.heebit.components.screenshot.exception.IllegalScreenSizeException
import rx.Observable

object ViewsBitmapObservable {

    operator fun get(activity: Activity, removedViews: Array<out View>?): Observable<Bitmap> {

        return Observable.defer {
            val screenBitmap = ScreenshotTaker.getScreenshotBitmap(activity, removedViews)
            if (screenBitmap != null) {
                Observable.just(screenBitmap)
            } else {
                Observable.error<Bitmap>(IllegalScreenSizeException())
            }
        }
    }
}
