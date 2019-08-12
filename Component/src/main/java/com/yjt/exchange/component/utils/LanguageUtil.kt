package com.hynet.heebit.components.utils

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import com.hynet.heebit.components.R
import com.hynet.heebit.components.constant.Constant
import com.hynet.heebit.components.constant.Temp
import java.io.UnsupportedEncodingException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException

class LanguageUtil {

    companion object {

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            LanguageUtil()
        }

    }

    fun getLanguage(tag: String): Int {
        when (tag) {
            Constant.Language.SIMPLIFIED_CHINESE -> return R.string.zh_cn
            Constant.Language.TRADITIONAL_CHINESE -> return R.string.zh_tw
            Constant.Language.ENGLISH -> return R.string.en
            else -> return -1
        }
    }

    fun getLocaleByLanguage(tag: String): Locale {
        when (tag) {
            Constant.Language.SIMPLIFIED_CHINESE -> return Locale.SIMPLIFIED_CHINESE
            Constant.Language.TRADITIONAL_CHINESE -> return Locale.TRADITIONAL_CHINESE
            Constant.Language.ENGLISH -> return Locale.US
            else -> return Locale.SIMPLIFIED_CHINESE
        }
    }

    fun getLanguageByLocale(locale: Locale): Locale {
        when (locale.language) {
            Constant.Language.CHINESE -> when (locale.country) {
                Constant.Country.CHINA -> return Locale.SIMPLIFIED_CHINESE
                Constant.Country.TAIWAN -> return Locale.TRADITIONAL_CHINESE
                else -> return Locale.SIMPLIFIED_CHINESE
            }
            Constant.Language.ENGLISH -> return Locale.US
            else -> return Locale.SIMPLIFIED_CHINESE
        }
    }

    fun getSystemLocale(): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //            LocaleList.getDefault().get(0);
            Resources.getSystem().configuration.locales.get(0)
        } else {
            Resources.getSystem().configuration.locale
        }
    }

    fun attachBaseContext(context: Context, locale: Locale): Context {
        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocales(LocaleList(locale))
        }
        return context.createConfigurationContext(configuration)
    }

    fun changeLanguage(context: Context, locale: Locale): Context? {
        try {
            SharedPreferenceUtil.instance.putString(context, Constant.Profile.LANGUAGE, Context.MODE_PRIVATE, Temp.LANGUAGE.content, locale.language)
            SharedPreferenceUtil.instance.putString(context, Constant.Profile.LANGUAGE, Context.MODE_PRIVATE, Temp.COUNTRY.content, locale.country)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                SharedPreferenceUtil.instance.putString(context, Constant.Profile.LANGUAGE, Context.MODE_PRIVATE, Temp.SCRIPT.content, locale.script)
            }
            Locale.setDefault(locale)
            val resources = context.resources
            val configuration = resources.configuration
            configuration.setLocale(locale)
            configuration.setLayoutDirection(locale)
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context.createConfigurationContext(configuration)
            } else {
                resources.updateConfiguration(configuration, resources.displayMetrics)
                return context
            }
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        } catch (e: BadPaddingException) {
            e.printStackTrace()
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        }
        return null
    }

    fun getApplicationLocale(context: Context): Locale {
        val locale = Locale(SharedPreferenceUtil.instance.getString(context, Constant.Profile.LANGUAGE, Context.MODE_PRIVATE, Temp.LANGUAGE.content, Locale.getDefault().language, false)!!, SharedPreferenceUtil.instance.getString(context, Constant.Profile.LANGUAGE, Context.MODE_PRIVATE, Temp.COUNTRY.content, Locale.getDefault().country, false)!!)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Locale.Builder().setLocale(locale).setScript(SharedPreferenceUtil.instance.getString(context, Constant.Profile.LANGUAGE, Context.MODE_PRIVATE, Temp.SCRIPT.content, Locale.getDefault().script, false)).build()
        } else {
            locale
        }
    }

    private fun isConsistentLocale(context: Context): Boolean {
        return getApplicationLocale(context) != Locale.getDefault()
    }

    fun isConsistentLanguage(context: Context): Boolean {
        val current = context.resources.configuration.locale
        return current == getApplicationLocale(context)
    }
}