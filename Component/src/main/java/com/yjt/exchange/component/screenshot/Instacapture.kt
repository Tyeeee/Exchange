package com.hynet.heebit.components.screenshot

import android.app.Activity
import android.graphics.Bitmap
import android.view.View
import com.hynet.heebit.components.screenshot.exception.ActivityNotRunningException
import com.hynet.heebit.components.screenshot.listener.OnScreenCaptureListener
import com.hynet.heebit.components.utils.LogUtil
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers

object Instacapture {

    fun capture(activity: Activity, screenCaptureListener: OnScreenCaptureListener, vararg ignoredViews: View) {
        screenCaptureListener.onCaptureStarted()
        captureRx(activity, *ignoredViews).subscribe(object : Subscriber<Bitmap>() {

            override fun onCompleted() {}

            override fun onError(e: Throwable) {
                LogUtil.instance.print("Screenshot capture failed!")
                LogUtil.instance.print(e.printStackTrace())
                screenCaptureListener.onCaptureFailed(e)
            }

            override fun onNext(bitmap: Bitmap) {
                screenCaptureListener.onCaptureSuccess(bitmap)
            }
        })

    }

    fun captureRx(activity: Activity, vararg ignoredViews: View): Observable<Bitmap> {
        val activityReferenceManager = ActivityReferenceManager()
        activityReferenceManager.setActivity(activity)
        val validatedActivity = activityReferenceManager.validatedActivity
                ?: return Observable.error<Bitmap>(ActivityNotRunningException("Is your activity running?"))
        val screenshotProvider = ScreenshotProvider()
        return screenshotProvider.getScreenshotBitmap(validatedActivity, ignoredViews).observeOn(AndroidSchedulers.mainThread())
    }
}
