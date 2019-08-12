package com.hynet.heebit.components.utils

import android.content.Context
import android.graphics.Typeface
import androidx.collection.SimpleArrayMap
import com.hynet.heebit.components.constant.Regex

class TypefaceUtil {

    companion object {

        private val cache: SimpleArrayMap<String, Typeface>? = SimpleArrayMap()

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            TypefaceUtil()
        }
    }

    fun get(ctx: Context, name: String): Typeface? {
        if (cache != null) {
            return if (!cache.containsKey(name)) {
                val typeface = Typeface.createFromAsset(ctx.assets, String.format(Regex.FILE_TTF.regext, name))
                cache.put(name, typeface)
                return typeface
            } else cache.get(name)
        } else {
            return null
        }
    }
}