package com.hynet.heebit.components.utils

import com.hynet.heebit.components.BuildConfig

class LogUtil {

    companion object {

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            LogUtil()
        }

    }

    fun v(tag: String, msg: String) {
        if (BuildConfig.DEBUG)
            android.util.Log.v(tag, msg)
    }

    fun v(tag: String, msg: String, t: Throwable) {
        if (BuildConfig.DEBUG)
            android.util.Log.v(tag, msg, t)
    }

    fun d(tag: String, msg: String) {
        if (BuildConfig.DEBUG)
            android.util.Log.d(tag, msg)
    }

    fun d(tag: String, msg: String, t: Throwable) {
        if (BuildConfig.DEBUG)
            android.util.Log.d(tag, msg, t)
    }

    fun i(tag: String, msg: String) {
        if (BuildConfig.DEBUG)
            android.util.Log.i(tag, msg)
    }

    fun i(tag: String, msg: String, t: Throwable) {
        if (BuildConfig.DEBUG)
            android.util.Log.i(tag, msg, t)
    }

    fun w(tag: String, msg: String) {
        if (BuildConfig.DEBUG)
            android.util.Log.w(tag, msg)
    }

    fun w(tag: String, msg: String, t: Throwable) {
        if (BuildConfig.DEBUG)
            android.util.Log.w(tag, msg, t)
    }

    fun e(tag: String, msg: String) {
        if (BuildConfig.DEBUG)
            android.util.Log.e(tag, msg)
    }

    fun e(tag: String, msg: String, t: Throwable) {
        if (BuildConfig.DEBUG)
            android.util.Log.e(tag, msg, t)
    }

    fun print(any: Any?) {
        if (BuildConfig.DEBUG) {
            println(format(any.toString()))
        }
    }

    fun format(msg: String): String {
        return String.format("---->>>>%s", msg)
    }
}