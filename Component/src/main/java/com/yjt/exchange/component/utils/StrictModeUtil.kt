package com.hynet.heebit.components.utils

import android.os.Build
import android.os.StrictMode
import com.hynet.heebit.components.BuildConfig

class StrictModeUtil {

    companion object {

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            StrictModeUtil()
        }

    }

    fun initialize() {
        if (BuildConfig.DEBUG) {
            val builder = StrictMode.VmPolicy.Builder()
            builder.detectAll()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                builder.detectLeakedClosableObjects()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    builder.detectLeakedRegistrationObjects()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        builder.detectFileUriExposure()
                    }
                }
            }
            builder.detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().penaltyDropBox()
            StrictMode.setVmPolicy(builder.build())
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                    .detectNetwork()
                    .penaltyLog()
                    .penaltyDropBox()
                    .penaltyDialog()
                    .build())
        }
    }
}