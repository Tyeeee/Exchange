package com.hynet.heebit.components.utils

import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.google.android.material.snackbar.Snackbar
import com.hynet.heebit.components.R

class SnackBarUtil {

    companion object {

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            SnackBarUtil()
        }

    }

    private var snackbar: Snackbar? = null

    fun showSnackBar(activity: FragmentActivity, message: CharSequence, length: Int) {
        showSnackBar(activity, message, length, null, null, View.NO_ID.toFloat(), View.NO_ID)
    }

    fun showSnackBar(activity: FragmentActivity, message: CharSequence, length: Int, textSize: Float) {
        showSnackBar(activity, message, length, null, null, textSize, View.NO_ID)
    }

    fun showSnackBar(activity: FragmentActivity, message: CharSequence, length: Int, color: Int) {
        showSnackBar(activity, message, length, null, null, View.NO_ID.toFloat(), color)
    }

    fun showSnackBar(activity: FragmentActivity, message: CharSequence, length: Int, textSize: Float, color: Int) {
        showSnackBar(activity, message, length, null, null, textSize, color)
    }

    fun showSnackBar(activity: FragmentActivity, message1: CharSequence, length: Int, message2: CharSequence, listener: View.OnClickListener) {
        showSnackBar(activity, message1, length, message2, listener, View.NO_ID.toFloat(), View.NO_ID)
    }


    fun showSnackBar(activity: FragmentActivity?, message1: CharSequence, length: Int, message2: CharSequence?, listener: View.OnClickListener?, textSize: Float, color: Int) {
        if (activity != null) {
            if (snackbar == null) {
                snackbar = Snackbar.make(activity.window.decorView, message1, length)
            } else {
                snackbar!!.setText(message1)
            }

            if (listener != null) {
                if (!TextUtils.isEmpty(message2)) {
                    snackbar!!.setAction(message2, listener)
                }
            }

            if (textSize != View.NO_ID.toFloat()) {
                (ViewUtil.instance.findView<Any>(snackbar!!.getView(), R.id.snackbar_text) as TextView).textSize = textSize
            }
            if (color != View.NO_ID) {
                (ViewUtil.instance.findView<Any>(snackbar!!.getView(), R.id.snackbar_text) as TextView).setTextColor(color)
            }
            snackbar!!.show()
        }
    }

    fun hideSnackBar() {
        snackbar!!.dismiss()
    }

    fun isShown(): Boolean {
        return snackbar != null && snackbar!!.isShownOrQueued()
    }
}