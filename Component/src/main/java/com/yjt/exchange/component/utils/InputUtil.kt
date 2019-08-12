package com.hynet.heebit.components.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.text.Spanned
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ScrollView
import com.hynet.heebit.components.constant.Constant
import com.hynet.heebit.components.constant.Regex
import java.util.regex.Pattern

class InputUtil {

    companion object {

        private var lastClickTime: Long = 0

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            InputUtil()
        }

    }

    fun hideKeyBoard(event: MotionEvent?, activity: Activity?) {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(activity?.currentFocus?.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun hideKeyBoard(context: Context?, scrollView: ScrollView?) {
        scrollView?.setOnTouchListener { v, event ->
            val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val view = (context as Activity).currentFocus
            if (inputMethodManager.isActive) {
                inputMethodManager.hideSoftInputFromWindow(view?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
            false
        }
    }

    fun closeKeyBoard(context: Context?) {
        val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputMethodManager.isActive) {
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    fun hideKeyBoard(context: Context?, view: View?) {
        val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputMethodManager.isActive) {
            inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
        }
    }

    fun showKeyBoard(context: Context?, view: View?) {
        val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputMethodManager.isActive) {
            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_FORCED)
        }
    }

    fun isActiveSoftInput(context: Context?): Boolean {
        return (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).isActive
    }

    fun isDoubleClick(): Boolean {
        val timeS = System.currentTimeMillis()
        val timeE = lastClickTime - timeS
        if (timeE > Constant.View.CLICK_PERIOD) {
            return true
        }
        lastClickTime = timeS
        return false
    }

    fun isDoubleClick(interval: Long): Boolean {
        val timeS = System.currentTimeMillis()
        val timeE = lastClickTime - timeS
        if (timeE > interval) {
            return true
        }
        lastClickTime = timeS
        return false
    }

    fun getInputFilter(maxLength: Int): InputFilter {
        return InputFilter(maxLength)
    }

    class InputFilter(private val maxLength: Int) : android.text.InputFilter {

        override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence {
            val destCount = dest.toString().length + getCharacterCount(dest.toString())
            val sourceCount = source.toString().length + getCharacterCount(source.toString())
            var data: String
            var count = 0
            var i = 0
            if (destCount + sourceCount > maxLength) {
                if (destCount < maxLength) {
                    while (destCount + count < maxLength) {
                        ++i
                        data = source.subSequence(0, i).toString()
                        count = data.length + getCharacterCount(data)
                        if (destCount + count > maxLength) {
                            --i
                        }
                    }
                    return if (i == 0) Regex.NONE.regext else source.subSequence(0, i).toString()
                }
                return Regex.NONE.regext
            } else {
                return source
            }
        }

        private fun getCharacterCount(character: String): Int {
            var count = 0
            val pattern = Pattern.compile(Regex.CHINESE_CHARACTERS.regext)
            val matcher = pattern.matcher(character)
            while (matcher.find()) {
                for (i in 0..matcher.groupCount()) {
                    count = count + 1
                }
            }
            return count
        }
    }

}