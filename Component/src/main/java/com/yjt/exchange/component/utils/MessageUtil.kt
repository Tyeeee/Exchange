package com.hynet.heebit.components.utils

import android.os.Bundle
import android.os.Message

class MessageUtil {

    companion object {

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            MessageUtil()
        }

    }

    fun getMessage(state: Int): Message {
        val message = Message.obtain()
        message.what = state
        return message
    }

    fun getMessage(state: Int, bundle: Bundle): Message {
        val message = Message.obtain()
        message.data = bundle
        message.what = state
        return message
    }

    fun getMessage(state: Int, obj: Any): Message {
        val message = Message.obtain()
        message.obj = obj
        message.what = state
        return message
    }

    fun getMessage(state: Int, parameter: String): Message {
        val message = Message.obtain()
        message.what = state
        message.obj = parameter
        return message
    }


    fun getErrorMessage(state: Int, e: Exception, error: String): Message {
        val message = Message.obtain()
        message.what = state
        if (e.message != null) {
            message.obj = e.message
        } else {
            message.obj = error
        }
        return message
    }

}