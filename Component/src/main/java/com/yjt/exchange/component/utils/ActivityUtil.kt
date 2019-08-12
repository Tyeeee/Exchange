package com.hynet.heebit.components.utils

import android.app.Activity
import android.content.ContextWrapper
import android.os.Process
import android.view.View
import java.util.*

class ActivityUtil {

    companion object {

        @JvmField
        val activities: Stack<Activity> = Stack()

        fun getActivity(view: View): Activity {
            var context = view.context
            while (context is ContextWrapper) {
                if (context is Activity) {
                    return context
                }
                context = context.baseContext
            }
            throw IllegalStateException("View $view is not attached to an Activity")
        }

        fun add(activity: Activity) {
            activities.add(activity)
             LogUtil.instance.print("$activity has added")
        }

        fun remove(activity: Activity) {
            if (activities.contains(activity)) {
                activities.remove(activity)
                 LogUtil.instance.print("$activity has removed")
            }
        }

        fun removeAll() {
            if (!activities.isEmpty()) {
                activities.forEach {
                    it.finish()
                }
            }
            activities.clear()
            Process.killProcess(Process.myPid())
        }

        fun contains(activity: Activity): Boolean {
            return activities.contains(activity)
        }

        fun size(): Int {
            return activities.size
        }
    }
}