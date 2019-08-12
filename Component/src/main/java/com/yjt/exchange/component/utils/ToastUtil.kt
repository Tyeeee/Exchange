package com.hynet.heebit.components.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.Toast

class ToastUtil {

    private var toast: Toast? = null
    private var handler: Handler = Handler(Looper.getMainLooper())

    companion object {

//        private var toastUtil: ToastUtil? = null

//        @Synchronized
//        fun getInstance(): ToastUtil {
//            if (toastUtil == null) {
//                toastUtil = ToastUtil()
//            }
//            return toastUtil?
//        }

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ToastUtil()
        }
    }

    fun showToast(context: Context, message: CharSequence, duration: Int) {
        handler.post {
            if (toast == null) {
                toast = Toast.makeText(context, message, duration)
            } else {
                toast?.setText(message)
            }
            toast?.setGravity(Gravity.CENTER, 0, 0)

            toast?.show()
        }
    }

    fun showToast(context: Context, resId: Int, duration: Int) {
        handler.post {
            if (toast == null) {
                toast = Toast.makeText(context, resId, duration)
            } else {
                toast?.setText(resId)
            }
            toast?.setGravity(Gravity.CENTER, 0, 0)
            toast?.show()
        }
    }


    fun hideToast() {
        toast?.cancel()
    }
}