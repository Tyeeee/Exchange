package com.hynet.heebit.components.utils

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

class BundleUtil {

    companion object {

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            BundleUtil()
        }

    }

    fun hasIntentExtraValue(activity: Activity, key: String): Boolean {
        return activity!!.intent != null && activity!!.intent.hasExtra(key)
    }

    fun hasBundleExtraValue(activity: Activity?, key: String): Boolean {
        return activity?.intent?.extras != null && activity?.intent.hasExtra(key)
    }

    fun getIntData(activity: Activity?, key: String, defaultValue: Int): Int {
        return if (hasBundleExtraValue(activity, key)) {
            getIntData(activity?.intent?.extras, key, defaultValue)
        } else defaultValue
    }

    fun getIntData(bundle: Bundle?, key: String, defaultValue: Int): Int {
        return bundle?.getInt(key, defaultValue) ?: defaultValue
    }

    fun getFloatData(activity: Activity?, key: String, defaultValue: Float): Float {
        return if (hasBundleExtraValue(activity, key)) {
            getFloatData(activity?.intent?.extras, key, defaultValue)
        } else defaultValue
    }

    fun getFloatData(bundle: Bundle?, key: String, defaultValue: Float): Float {
        return bundle?.getFloat(key, defaultValue) ?: defaultValue
    }

    fun getDoubleData(activity: Activity?, key: String, defaultValue: Double): Double {
        return if (hasBundleExtraValue(activity, key)) {
            getDoubleData(activity?.intent?.extras, key, defaultValue)
        } else defaultValue
    }

    fun getDoubleData(bundle: Bundle?, key: String, defaultValue: Double): Double {
        return bundle?.getDouble(key, defaultValue) ?: defaultValue
    }

    fun getLongData(activity: Activity?, key: String, defaultValue: Long): Long {
        return if (hasBundleExtraValue(activity, key)) {
            getLongData(activity?.intent?.extras, key, defaultValue)
        } else defaultValue
    }

    fun getLongData(bundle: Bundle?, key: String, defaultValue: Long): Long {
        return bundle?.getLong(key, defaultValue) ?: defaultValue
    }

    fun getBooleanData(activity: Activity?, key: String): Boolean? {
        return if (hasBundleExtraValue(activity, key)) {
            getBooleanData(activity?.intent?.extras, key)
        } else null
    }

    fun getBooleanData(bundle: Bundle?, key: String): Boolean? {
        return bundle?.getBoolean(key)
    }

    fun getCharSequenceData(activity: Activity?, key: String): CharSequence? {
        return if (hasBundleExtraValue(activity, key)) {
            getCharSequenceData(activity?.intent?.extras, key)
        } else null
    }

    fun getCharSequenceData(bundle: Bundle?, key: String): CharSequence? {
        return bundle?.getCharSequence(key)
    }

    fun getStringData(activity: Activity?, key: String): String? {
        return if (hasBundleExtraValue(activity, key)) {
            getStringData(activity?.intent?.extras, key)
        } else null
    }

    fun getStringData(bundle: Bundle?, key: String): String? {
        return bundle?.getString(key)
    }

    fun getBundleData(intent: Intent?, key: String): Bundle? {
        return intent?.getBundleExtra(key)
    }

    fun <T : Serializable> getSerializableData(bundle: Bundle?, key: String, clazz: Class<*>?): T? {
        bundle?.classLoader = clazz?.classLoader
        return bundle?.getSerializable(key) as T?
    }

    fun <T : Parcelable> getParcelableData(activity: Activity?, key: String, clazz: Class<*>?): T? {
        return if (hasBundleExtraValue(activity, key)) {
            val intent = activity?.intent
            intent?.setExtrasClassLoader(clazz?.classLoader)
            return getParcelableData(intent?.extras, key, clazz)
        } else null
    }

    fun <T : Parcelable> getParcelableData(bundle: Bundle?, key: String, clazz: Class<*>?): T? {
        bundle?.classLoader = clazz?.classLoader
        return bundle?.getParcelable(key)
    }
}