package com.hynet.heebit.components.utils

import com.google.common.collect.Lists
import com.hynet.heebit.components.listener.OnNotifyListener

class NotifyUtil {

    companion object {

        private val onNotifyListeners = Lists.newArrayList<OnNotifyListener>()

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            NotifyUtil()
        }
    }

    fun add(onNotifyListener: OnNotifyListener?) {
        if (!contains(onNotifyListener!!)) {
            onNotifyListeners.add(onNotifyListener)
        }
    }

    fun remove(onNotifyListener: OnNotifyListener?) {
        if (contains(onNotifyListener!!)) {
            onNotifyListeners.remove(onNotifyListener)
        }
    }

    fun notify(action: String) {
        for (onNotifyListener in onNotifyListeners) {
            LogUtil.instance.print("onNotifyListener:$onNotifyListener")
            onNotifyListener.onNotify(action)
        }
    }

    fun removeAll() {
        onNotifyListeners.clear()
    }

    operator fun contains(onNotifyListener: OnNotifyListener): Boolean {
        return onNotifyListeners.contains(onNotifyListener)
    }

    fun size(): Int {
        return onNotifyListeners.size
    }
    
}