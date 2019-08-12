package com.hynet.heebit.components.utils

import android.annotation.TargetApi
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MarginLayoutParamsCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.hynet.heebit.components.R
import com.hynet.heebit.components.constant.Constant
import com.hynet.heebit.components.utils.listener.ContinuteTouchListener
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class ViewUtil {

    companion object {

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ViewUtil()
        }

    }

    fun dp2px(ctx: Context, dpValue: Float): Int {
        return (dpValue * ctx.resources.displayMetrics.density + 0.5f).toInt()
    }

    fun px2dp(ctx: Context, pxValue: Float): Int {
        return (pxValue / ctx.resources.displayMetrics.density + 0.5f).toInt()
    }

    fun dp2px(resources: Resources, dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
        //        return dp * resources.getDisplayMetrics().density + 0.5f;
    }

    fun sp2px(resources: Resources, sp: Float): Float {
        return sp * resources.displayMetrics.scaledDensity
    }

    fun px2sp(context: Context, px: Float): Int {
        return (px / context.resources.displayMetrics.scaledDensity + 0.5f).toInt()
    }

    fun getScreenWidth(ctx: Context): Int {
        return ctx.resources.displayMetrics.widthPixels
    }

    fun getScreenHeight(ctx: Context): Int {
        return ctx.resources.displayMetrics.heightPixels
    }

    fun getDensity(ctx: Context): Float {
        return ctx.resources.displayMetrics.density
    }

    fun getDensityDpi(ctx: Context): Int {
        return ctx.resources.displayMetrics.densityDpi
    }

    fun getDefaultDisplay(ctx: Context): Display {
        return (ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
    }

    fun getScreenPixel(ctx: Context): DisplayMetrics {
        val displayMetrics = DisplayMetrics()
        getDefaultDisplay(ctx).getMetrics(displayMetrics)
        return displayMetrics
    }

    fun getDefaultWidth(ctx: Context): Int {
        return getDefaultDisplay(ctx).width
    }

    fun getDefaultHeight(ctx: Context): Int {
        return getDefaultDisplay(ctx).height
    }

    fun px2sp(resources: Resources, px: Float): Int {
        return (px / resources.displayMetrics.scaledDensity + 0.5f).toInt()
    }

    fun isSoftKeyAvail(activity: Activity): Boolean {
        val isSoftkey = booleanArrayOf(false)
        val rootView = findView<View>(activity.window.decorView, android.R.id.content)
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(ViewTreeObserver.OnGlobalLayoutListener {
            val rootViewHeight = rootView.getRootView().getHeight()
            val viewHeight = rootView.getHeight()
            val heightDiff = rootViewHeight - viewHeight
            if (heightDiff > 100) { // 99% of the time the height diff will be due to a keyboard.
                isSoftkey[0] = true
            }
        })
        return isSoftkey[0]
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    fun getNavigationBarStatus(ctx: Context): Int {
        val hasMenuKey = ViewConfiguration.get(ctx).hasPermanentMenuKey()
        val hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
        return if (!hasMenuKey && !hasBackKey) {
            ctx.resources.getDimensionPixelSize(ctx.resources.getIdentifier("navigation_bar_height", "dimen", "android"))
        } else {
            0
        }
    }

    fun getStatusBarHeight(ctx: Context): Int {
        var height = 0
        val resourceId = ctx.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            height = ctx.resources.getDimensionPixelSize(resourceId)
        }
        return height
    }

    fun getNavigationBarHeight(ctx: Context): Int {
        var height = 0
        val resources = ctx.resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0 && checkDeviceHasNavigationBar(ctx)) {
            height = resources.getDimensionPixelSize(resourceId)
        }
        return height
    }

    fun getStatusAndTitleBarHeight(view: View?): Int {
        if (view == null) {
            return 0
        }
        val frame = Rect()
        view.getWindowVisibleDisplayFrame(frame)
        return (view.context as Activity).window.findViewById<View>(Window.ID_ANDROID_CONTENT).top + frame.top

    }

    private fun checkDeviceHasNavigationBar(context: Context): Boolean {
        var hasNavigationBar = false
        val resources = context.resources
        val id = resources.getIdentifier("config_showNavigationBar", "bool", "android")
        if (id > 0) {
            hasNavigationBar = resources.getBoolean(id)
        }
        try {
            val clazz = Class.forName("android.os.SystemProperties")
            val method = clazz.getMethod("get", String::class.java)
            val navBarOverride = method.invoke(clazz, "qemu.hw.mainkeys") as String
            if ("1" == navBarOverride) {
                hasNavigationBar = false
            } else if ("0" == navBarOverride) {
                hasNavigationBar = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return hasNavigationBar
    }

    fun getTopBarHeight(activity: Activity): Int {
        return activity.window.findViewById<View>(Window.ID_ANDROID_CONTENT).top
    }

    fun setToolBar(activity: AppCompatActivity?, toolbarId: Int, isHomeButtonEnable: Boolean) {
        if (activity != null && toolbarId != View.NO_ID) {
            val toolbar = findView<Toolbar>(activity, toolbarId)
            activity.setSupportActionBar(toolbar)
            if (activity.supportActionBar != null) {
                activity.supportActionBar!!.setDisplayHomeAsUpEnabled(isHomeButtonEnable)
                toolbar.setTitle(activity.title)
                toolbar.setNavigationOnClickListener(View.OnClickListener {
                    LogUtil.instance.print("setNavigationOnClickListener")
                    activity.finish()
                })
            }
        }
    }

    fun getMeasuredWidth(v: View?): Int {
        return v?.measuredWidth ?: 0
    }

    fun getWidth(v: View): Int {
        //        return (v == null) ? 0 : v.getWidth();
        val w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        v.measure(w, h)
        return v.measuredWidth
    }

    fun getWidthWithMargin(v: View): Int {
        return getWidth(v) + getMarginHorizontally(v)
    }

    fun getHeight(v: View): Int {
        val w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        v.measure(w, h)
        return v.measuredHeight
    }

    fun getStart(v: View): Int {
        return getStart(v, false)
    }

    fun getStart(v: View?, withoutPadding: Boolean): Int {
        if (v == null) {
            return 0
        }
        return if (isLayoutRtl(v)) {
            if (withoutPadding) v.right - getPaddingStart(v) else v.right
        } else {
            if (withoutPadding) v.left + getPaddingStart(v) else v.left
        }
    }

    fun getEnd(v: View): Int {
        return getEnd(v, false)
    }

    fun getEnd(v: View?, withoutPadding: Boolean): Int {
        if (v == null) {
            return 0
        }
        return if (isLayoutRtl(v)) {
            if (withoutPadding) v.left + getPaddingEnd(v) else v.left
        } else {
            if (withoutPadding) v.right - getPaddingEnd(v) else v.right
        }
    }

    fun getPaddingStart(v: View?): Int {
        return if (v == null) {
            0
        } else ViewCompat.getPaddingStart(v)
    }

    fun getPaddingEnd(v: View?): Int {
        return if (v == null) {
            0
        } else ViewCompat.getPaddingEnd(v)
    }

    fun getPaddingHorizontally(v: View?): Int {
        return if (v == null) {
            0
        } else v.paddingLeft + v.paddingRight
    }

    fun getMarginStart(v: View?): Int {
        if (v == null) {
            return 0
        }
        val lp = v.layoutParams as ViewGroup.MarginLayoutParams
        return MarginLayoutParamsCompat.getMarginStart(lp)
    }

    fun getMarginEnd(v: View?): Int {
        if (v == null) {
            return 0
        }
        val lp = v.layoutParams as ViewGroup.MarginLayoutParams
        return MarginLayoutParamsCompat.getMarginEnd(lp)
    }

    fun getMarginHorizontally(v: View?): Int {
        if (v == null) {
            return 0
        }
        val lp = v.layoutParams as ViewGroup.MarginLayoutParams
        return MarginLayoutParamsCompat.getMarginStart(lp) + MarginLayoutParamsCompat.getMarginEnd(lp)
    }

    fun isLayoutRtl(v: View): Boolean {
        return ViewCompat.getLayoutDirection(v) == ViewCompat.LAYOUT_DIRECTION_RTL
    }

    fun getDistanceBetween2Points(p0: PointF, p1: PointF): Float {
        return Math.sqrt(Math.pow((p0.y - p1.y).toDouble(), 2.0) + Math.pow((p0.x - p1.x).toDouble(), 2.0)).toFloat()
    }

    fun getMiddlePoint(p1: PointF, p2: PointF): PointF {
        return PointF((p1.x + p2.x) / 2.0f, (p1.y + p2.y) / 2.0f)
    }

    fun getPointByPercent(p1: PointF, p2: PointF, percent: Float): PointF {
        return PointF(evaluateValue(percent, p1.x, p2.x), evaluateValue(percent, p1.y, p2.y))
    }

    fun evaluateValue(fraction: Float, start: Number, end: Number): Float {
        return start.toFloat() + (end.toFloat() - start.toFloat()) * fraction
    }

    fun getIntersectionPoints(pMiddle: PointF, radius: Float, lineK: Double?): Array<PointF?> {
        val points = arrayOfNulls<PointF>(2)
        val radian: Float
        val xOffset: Float
        val yOffset: Float
        if (lineK != null) {
            radian = Math.atan(lineK).toFloat()
            xOffset = (Math.sin(radian.toDouble()) * radius).toFloat()
            yOffset = (Math.cos(radian.toDouble()) * radius).toFloat()
        } else {
            xOffset = radius
            yOffset = 0f
        }
        points[0] = PointF(pMiddle.x + xOffset, pMiddle.y - yOffset)
        points[1] = PointF(pMiddle.x - xOffset, pMiddle.y + yOffset)

        return points
    }

    private fun checkAppCompatTheme(context: Context) {
        val typedArray = context.obtainStyledAttributes(intArrayOf(R.attr.colorPrimary))
        val failed = !typedArray.hasValue(0)
        typedArray?.recycle()
        if (failed) {
            throw IllegalArgumentException("You need to use typedArray Theme.AppCompat theme " + "(or descendant) with the design library.")
        }
    }

    @ColorInt
    fun getAppColorPrimary(context: Context): Int {
        checkAppCompatTheme(context)
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        return typedValue.data
    }

    @ColorInt
    fun getIconColor(positionOffset: Float, @ColorInt startColor: Int, @ColorInt middleColor: Int, @ColorInt endColor: Int, step: Int): Int {
        return if (startColor == Color.TRANSPARENT) {
            if (positionOffset < 0.5) {
                if (middleColor == Color.TRANSPARENT) {
                    middleColor
                } else Color.argb((0xff * positionOffset * 2f).toInt(), Color.red(middleColor), Color.green(middleColor), Color.blue(middleColor))
            } else if (positionOffset.toDouble() == 0.5) {
                middleColor
            } else {
                if (middleColor == Color.TRANSPARENT) {
                    if (endColor == Color.TRANSPARENT) {
                        middleColor
                    } else Color.argb((0xff - 2f * 0xff * positionOffset).toInt(), Color.red(endColor), Color.green(endColor), Color.blue(endColor))
                } else {
                    if (endColor == Color.TRANSPARENT) {
                        Color.argb((0xff - 2f * 0xff * positionOffset).toInt(), Color.red(endColor), Color.green(endColor), Color.blue(endColor))
                    } else getOffsetColor(((positionOffset - 0.5) * 2).toFloat(), middleColor, endColor, step)
                }
            }
        } else if (middleColor == Color.TRANSPARENT) {
            if (positionOffset < 0.5) {
                Color.argb((0xff - 2f * 0xff * positionOffset).toInt(), Color.red(startColor), Color.green(startColor), Color.blue(startColor))
            } else if (positionOffset.toDouble() == 0.5) {
                middleColor
            } else {
                if (endColor == Color.TRANSPARENT) {
                    Color.TRANSPARENT
                } else Color.argb((0xff - 2f * 0xff * positionOffset).toInt(), Color.red(endColor), Color.green(endColor), Color.blue(endColor))
            }
        } else if (endColor == Color.TRANSPARENT) {
            if (positionOffset < 0.5) {
                getOffsetColor(positionOffset * 2, startColor, middleColor, step)
            } else if (positionOffset.toDouble() == 0.5) {
                middleColor
            } else {
                Color.argb((0xff - 2f * 0xff * positionOffset).toInt(), Color.red(middleColor), Color.green(middleColor), Color.blue(middleColor))
            }
        } else {
            if (positionOffset < 0.5) {
                getOffsetColor(positionOffset * 2, startColor, middleColor, step)
            } else if (positionOffset.toDouble() == 0.5) {
                middleColor
            } else {
                getOffsetColor(((positionOffset - 0.5) * 2).toFloat(), middleColor, endColor, step)
            }
        }
    }

    @ColorInt
    fun getOffsetColor(offset: Float, @ColorInt startColor: Int, @ColorInt endColor: Int, steps: Int): Int {
        if (offset <= 0.04) {
            return startColor
        }
        return if (offset >= 0.96) {
            endColor
        } else Color.rgb((Color.red(startColor) + (Color.red(endColor) - Color.red(startColor)) / steps * (offset * steps)).toInt(),
                (Color.green(startColor) + (Color.green(endColor) - Color.green(startColor)) / steps * (offset * steps)).toInt(),
                (Color.blue(startColor) + (Color.blue(endColor) - Color.blue(startColor)) / steps * (offset * steps)).toInt())
    }

    fun isVisible(view: View?): Boolean {
        return view?.visibility == View.VISIBLE
    }

    fun isInvisible(view: View?): Boolean {
        return view?.visibility == View.INVISIBLE
    }

    fun isGone(view: View?): Boolean {
        return view?.visibility == View.GONE
    }

    fun setViewVisible(view: View?) {
        if (isInvisible(view) || isGone(view)) {
            view?.visibility = View.VISIBLE
        }
    }

    fun setViewGone(view: View?) {
        if (isVisible(view)) {
            view?.visibility = View.GONE
        }
    }

    fun setViewInvisible(view: View?) {
        if (isVisible(view)) {
            view?.visibility = View.INVISIBLE
        }
    }

    fun isScrollable(viewGroup: ViewGroup): Boolean {
        var totalHeight = 0
        for (i in 0 until viewGroup.childCount) {
            totalHeight += viewGroup.getChildAt(i).measuredHeight
        }
        return viewGroup.measuredHeight < totalHeight
    }

    fun isScrollTop(recyclerView: RecyclerView?): Boolean {
        if (recyclerView != null && recyclerView.childCount > 0) {
            if (recyclerView.getChildAt(0).top < 0) {
                return false
            }
        }
        return true
    }

    fun isScrollTop(listView: ListView?): Boolean {
        if (listView != null && listView.childCount > 0) {
            if (listView.getChildAt(0).top < 0) {
                return false
            }
        }
        return true
    }

    fun isScrollTop(listView: ExpandableListView?): Boolean {
        if (listView != null && listView.childCount > 0) {
            if (listView.getChildAt(0).top < 0) {
                return false
            }
        }
        return true
    }

    fun isScrollTop(scrollView: ScrollView?): Boolean {
        if (scrollView != null) {
            if (scrollView.scrollY > 0) {
                return false
            }
        }
        return true
    }

    fun toggleView(view: View, show: Boolean) {
        if (show) {
            setViewVisible(view)
        } else {
            setViewGone(view)
        }
    }

    fun setText(textView: TextView?, format: String, content: String?, drawable: Drawable?, width: Int, height: Int, padding: Int, drawablePosition: Int, gravity: Int, textColor: Int, clickable: Boolean) {
        if (textView != null) {
            if (TextUtils.isEmpty(format)) {
                textView.text = content
            } else {
                textView.text = String.format(format, content)
            }
            if (drawable != null) {
                LogUtil.instance.print("width：$width，height：$height")
                drawable.setBounds(0, 0, width, height)
                when (drawablePosition) {
                    Constant.View.DRAWABLE_TOP -> {
                        textView.compoundDrawablePadding = padding
                        textView.setCompoundDrawables(null, drawable, null, null)
                    }
                    Constant.View.DRAWABLE_LEFT -> {
                        textView.compoundDrawablePadding = padding
                        textView.setCompoundDrawables(drawable, null, null, null)
                    }
                    Constant.View.DRAWABLE_RIGHT -> {
                        textView.compoundDrawablePadding = padding
                        textView.setCompoundDrawables(null, null, drawable, null)
                    }
                    Constant.View.DRAWABLE_BOTTOM -> {
                        textView.compoundDrawablePadding = padding
                        textView.setCompoundDrawables(null, null, null, drawable)
                    }
                    else -> {
                        textView.compoundDrawablePadding = padding
                        textView.setCompoundDrawables(drawable, null, null, null)
                    }
                }
            }
            if (textColor != View.NO_ID) {
                textView.setTextColor(textColor)
            }
            if (gravity != View.NO_ID) {
                textView.gravity = gravity
            }
            textView.isClickable = clickable
        }
    }

    fun setText(ctx: Context, textView: TextView?, format: String, content: String?, imageId: Int, width: Int, height: Int, padding: Int, drawablePosition: Int, gravity: Int, textColor: Int, clickable: Boolean) {
        if (textView != null) {
            if (TextUtils.isEmpty(format)) {
                textView.text = content
            } else {
                textView.text = String.format(format, content)
            }
            if (imageId != View.NO_ID) {
                val drawable = ctx.resources.getDrawable(imageId)
                LogUtil.instance.print("width：$width，height：$height")
                drawable.setBounds(0, 0, width, height)
                //                Drawable drawable = DensityUtil.getInstance(ctx).zoomDrawable(ctx.getResources().getDrawable(imageId), width, height);
                when (drawablePosition) {
                    Constant.View.DRAWABLE_TOP -> {
                        textView.compoundDrawablePadding = padding
                        textView.setCompoundDrawables(null, drawable, null, null)
                    }
                    Constant.View.DRAWABLE_LEFT -> {
                        textView.compoundDrawablePadding = padding
                        textView.setCompoundDrawables(drawable, null, null, null)
                    }
                    Constant.View.DRAWABLE_RIGHT -> {
                        textView.compoundDrawablePadding = padding
                        textView.setCompoundDrawables(null, null, drawable, null)
                    }
                    Constant.View.DRAWABLE_BOTTOM -> {
                        textView.compoundDrawablePadding = padding
                        textView.setCompoundDrawables(null, null, null, drawable)
                    }
                    else -> {
                        textView.compoundDrawablePadding = padding
                        textView.setCompoundDrawables(drawable, null, null, null)
                    }
                }
            }
            if (textColor != View.NO_ID) {
                textView.setTextColor(textColor)
            }
            if (gravity != View.NO_ID) {
                textView.gravity = gravity
            }
            textView.isClickable = clickable
        }
    }

    fun setText(textView: TextView, text: CharSequence?, typeface: Typeface) {
        if (text != null) {
            textView.text = text
            textView.setTypeface(typeface)
        } else {
            setViewGone(textView)
        }
    }

    fun setText(button: Button, text: CharSequence?, typeface: Typeface, onClickListener: View.OnClickListener?) {
        setText(button, text, typeface)
        if (onClickListener != null) {
            button.setOnClickListener(onClickListener)
        }
    }

    fun setButton(ctx: Context, textView: TextView, backgroundId: Int, textColorId: Int, enable: Boolean, clickable: Boolean) {
        if (backgroundId != View.NO_ID) {
            textView.setBackgroundDrawable(ctx.resources.getDrawable(backgroundId))
        }
        if (textColorId != View.NO_ID) {
            textView.setTextColor(ctx.resources.getColor(textColorId))
        }
        if (!clickable) {
            textView.isEnabled = enable
        } else {
            textView.isEnabled = true
        }
    }

    fun setSystemUiVisibility(activity: Activity?) {
        activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                //                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                //                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                //                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                //                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE

    }

    fun runOnUiThread(activity: Activity, prompt: String) {
        activity.runOnUiThread { ToastUtil.instance.showToast(activity, prompt, Toast.LENGTH_SHORT) }
    }

    fun runOnUiThread(activity: Activity, resourceId: Int) {
        runOnUiThread(activity, activity.getString(resourceId))
    }

    fun hideDialog(dialog: Dialog?, activity: Activity?) {
        if (dialog != null && dialog.isShowing && activity != null && !activity.isFinishing) {
            dialog.dismiss()
            unLockScreenOrientation(activity)
        }
    }

    fun hideDialog(dialogFragment: DialogFragment?) {
        if (dialogFragment != null && dialogFragment.fragmentManager != null && !dialogFragment.fragmentManager!!.isDestroyed) {
            dialogFragment.dismissAllowingStateLoss()
        }
    }

    fun lockScreenOrientation(activity: Activity) {
        val newConfig = activity.resources.configuration
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else if (newConfig.hardKeyboardHidden == Configuration.KEYBOARDHIDDEN_NO) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else if (newConfig.hardKeyboardHidden == Configuration.KEYBOARDHIDDEN_YES) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    fun unLockScreenOrientation(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    fun <V> findView(activity: Activity, @IdRes resId: Int): V {
        return activity.findViewById<View>(resId) as V
    }

    fun <V> findView(rootView: View, @IdRes resId: Int): V {
        return rootView.findViewById<View>(resId) as V
    }

    fun <V> findViewAttachOnclick(activity: Activity, @IdRes resId: Int, onClickListener: View.OnClickListener): V {
        val view = activity.findViewById<View>(resId)
        view.setOnClickListener(onClickListener)
        return view as V
    }

    fun <V> findViewAttachOnclick(rootView: View, @IdRes resId: Int, onClickListener: View.OnClickListener): V {
        val view = rootView.findViewById<View>(resId)
        view.setOnClickListener(onClickListener)
        return view as V
    }

    fun <V> findViewAttachOnTouch(activity: Activity, @IdRes resId: Int, continuteTouchListener: ContinuteTouchListener): V {
        val view = activity.findViewById<View>(resId)
        val scheduledExecutor = arrayOf<ScheduledExecutorService>()
        view.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                scheduledExecutor[0] = Executors.newSingleThreadScheduledExecutor()
                scheduledExecutor[0].scheduleWithFixedDelay({ activity.runOnUiThread { continuteTouchListener.continuteTouch(resId) } }, 0, 300, TimeUnit.MILLISECONDS)
            } else if (motionEvent.action == MotionEvent.ACTION_UP) {
                scheduledExecutor[0].shutdownNow()
            }
            true
        }
        return view as V
    }
}