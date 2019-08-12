package com.hynet.heebit.components.utils

import android.content.Context
import android.view.View
import android.view.animation.*
import com.hynet.heebit.components.constant.Constant

class AnimationUtil {

    companion object {

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            AnimationUtil()
        }

    }

    private inner class CustomerAnimationListener(private val view: View) : Animation.AnimationListener {

        override fun onAnimationStart(animation: Animation) {}

        override fun onAnimationEnd(animation: Animation) {
            ViewUtil.instance.setViewGone(view)
        }

        override fun onAnimationRepeat(animation: Animation) {}

    }

    private fun setEffect(animation: Animation, interpolatorType: Int, durationMillis: Long, delayMillis: Long) {
        when (interpolatorType) {
            0 -> animation.interpolator = LinearInterpolator()
            1 -> animation.interpolator = AccelerateInterpolator()
            2 -> animation.interpolator = DecelerateInterpolator()
            3 -> animation.interpolator = AccelerateDecelerateInterpolator()
            4 -> animation.interpolator = BounceInterpolator()
            5 -> animation.interpolator = OvershootInterpolator()
            6 -> animation.interpolator = AnticipateInterpolator()
            7 -> animation.interpolator = AnticipateOvershootInterpolator()
            else -> {
            }
        }
        animation.duration = durationMillis
        animation.startOffset = delayMillis
    }

    fun baseIn(view: View, animation: Animation, durationMillis: Long, delayMillis: Long) {
        setEffect(animation, Constant.View.DEFAULT_INTERPOLATOR_TYPE, durationMillis, delayMillis)
        ViewUtil.instance.setViewVisible(view)
        view.startAnimation(animation)
    }

    fun baseOut(view: View, animation: Animation, durationMillis: Long, delayMillis: Long) {
        setEffect(animation, Constant.View.DEFAULT_INTERPOLATOR_TYPE, durationMillis, delayMillis)
        animation.setAnimationListener(CustomerAnimationListener(view))
        view.startAnimation(animation)
        animation.fillAfter = true
    }

    fun transparent(view: View) {
        ViewUtil.instance.setViewInvisible(view)
    }

    fun fadeInByAlpha(view: View, durationMillis: Long, delayMillis: Long) {
        baseIn(view, AlphaAnimation(0f, 1f), durationMillis, delayMillis)
    }

    fun fadeOutByAlpha(view: View, durationMillis: Long, delayMillis: Long) {
        baseOut(view, AlphaAnimation(1f, 0f), durationMillis, delayMillis)
    }

    fun fadeInByTranslate(view: View, fromXType: Int, toXType: Int, fromYType: Int, toYType: Int, durationMillis: Long, delayMillis: Long) {
        baseIn(view, TranslateAnimation(fromXType, 0f, toXType, 0f, fromYType, 1f, toYType, 0f), durationMillis, delayMillis)
    }

    fun fadeOutByTranslate(view: View, fromXType: Int, toXType: Int, fromYType: Int, toYType: Int, durationMillis: Long, delayMillis: Long) {
        baseIn(view, TranslateAnimation(fromXType, 0f, toXType, 0f, fromYType, 0f, toYType, 1f), durationMillis, delayMillis)
    }

    fun fadeRotate(view: View, durationMillis: Long, delayMillis: Long) {
        baseIn(view, RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f), durationMillis, delayMillis)
    }

    fun startAnimation(context: Context, view: View, animationId: Int) {
        view.startAnimation(AnimationUtils.loadAnimation(context, animationId))
    }
    
}