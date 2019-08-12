package com.hynet.heebit.components.utils

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Base64
import java.io.*
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException


class SharedPreferenceUtil {

    companion object {

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            SharedPreferenceUtil()
        }

    }

    private var sharedPreferences: SharedPreferences? = null

    fun getSharedPreferences(ctx: Context?, fileName: String, mode: Int) {
        if (sharedPreferences == null) {
            sharedPreferences = ctx?.getSharedPreferences(fileName, mode)
        }
    }

    fun putLong(ctx: Context?, fileName: String, mode: Int, key: String, value: Long) {
        getSharedPreferences(ctx, fileName, mode)
        sharedPreferences?.edit()?.putLong(key, value)?.apply()
    }

    fun putInt(ctx: Context?, fileName: String, mode: Int, key: String, value: Int) {
        getSharedPreferences(ctx, fileName, mode)
        sharedPreferences?.edit()?.putInt(key, value)?.apply()
    }

    @Throws(NoSuchPaddingException::class, InvalidKeyException::class, NoSuchAlgorithmException::class, IllegalBlockSizeException::class, BadPaddingException::class, UnsupportedEncodingException::class)
    fun putString(ctx: Context?, fileName: String, mode: Int, key: String, value: String?) {
        putString(ctx, fileName, mode, key, value, false)
    }

    @Throws(NoSuchPaddingException::class, BadPaddingException::class, NoSuchAlgorithmException::class, IllegalBlockSizeException::class, UnsupportedEncodingException::class, InvalidKeyException::class)
    fun putString(ctx: Context?, fileName: String, mode: Int, key: String?, value: String?, isEncrypt: Boolean) {
        getSharedPreferences(ctx, fileName, mode)
        if (isEncrypt) {
            var key = SecurityUtil.getInstance().encryptMD5(key)
            sharedPreferences?.edit()?.putString(key, Base64Util.encode(SecurityUtil.getInstance().encryptAES(value, key)))?.apply()
        } else {
            sharedPreferences?.edit()?.putString(key, value)?.apply()
        }
    }

    fun putBoolean(ctx: Context?, fileName: String, mode: Int, key: String, value: Boolean) {
        getSharedPreferences(ctx, fileName, mode)
        sharedPreferences?.edit()?.putBoolean(key, value)?.apply()
    }

    fun putObject(ctx: Context?, fileName: String, mode: Int, key: String, value: List<*>) {
        try {
            getSharedPreferences(ctx, fileName, mode)
            val byteArrayOutputStream = ByteArrayOutputStream()
            ObjectOutputStream(byteArrayOutputStream).writeObject(value)
            sharedPreferences?.edit()?.putString(key, String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)))?.apply()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Throws(IllegalBlockSizeException::class, InvalidKeyException::class, BadPaddingException::class, NoSuchAlgorithmException::class, NoSuchPaddingException::class, IOException::class)
    fun putEncryptObject(ctx: Context?, fileName: String, mode: Int, key: String?, value: List<*>, isEncrypt: Boolean) {
        getSharedPreferences(ctx, fileName, mode)
        val byteArrayOutputStream = ByteArrayOutputStream()
        ObjectOutputStream(byteArrayOutputStream).writeObject(value)
        if (isEncrypt) {
            var key = SecurityUtil.getInstance().encryptMD5(key)
            sharedPreferences?.edit()?.putString(key, Arrays.toString(SecurityUtil.getInstance().encryptAES(String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)), key)))?.apply()
        } else {
            sharedPreferences?.edit()?.putString(key, String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)))?.apply()
        }
    }

    fun getLong(ctx: Context?, fileName: String, mode: Int, key: String, defValue: Long): Long? {
        getSharedPreferences(ctx, fileName, mode)
        return sharedPreferences?.getLong(key, defValue)
    }

    fun getInt(ctx: Context?, fileName: String, mode: Int, key: String, defValue: Int): Int? {
        getSharedPreferences(ctx, fileName, mode)
        return sharedPreferences?.getInt(key, defValue)
    }

    fun getString(ctx: Context?, fileName: String, mode: Int, key: String, defValue: String?): String? {
        return getString(ctx, fileName, mode, key, defValue, false)
    }

    fun getString(ctx: Context?, fileName: String, mode: Int, key: String?, defValue: String?, isEncrypt: Boolean): String? {
        try {
            if (!TextUtils.isEmpty(fileName) && !TextUtils.isEmpty(key)) {
                getSharedPreferences(ctx, fileName, mode)
                return if (isEncrypt) {
                    var key = SecurityUtil.getInstance().encryptMD5(key)
                    val value = sharedPreferences?.getString(key, defValue)
                    return if (!TextUtils.isEmpty(value)) {
                        String(SecurityUtil.getInstance().decryptAES(Base64Util.decode(value), key))
                    } else {
                        null
                    }
                } else {
                    sharedPreferences?.getString(key, defValue)
                }
            } else {
                return null
            }
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: BadPaddingException) {
            e.printStackTrace()
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        }
        return null
    }

    fun getBoolean(ctx: Context?, fileName: String, mode: Int, key: String, defValue: Boolean): Boolean? {
        getSharedPreferences(ctx, fileName, mode)
        return sharedPreferences?.getBoolean(key, defValue)
    }

    fun getObject(ctx: Context?, fileName: String, mode: Int, key: String, defValue: String): List<Map<String, String>>? {
        return getObject(ctx, fileName, mode, key, defValue, false)
    }

    fun getObject(ctx: Context?, fileName: String, mode: Int, key: String?, defValue: String?, isEncrypt: Boolean): List<Map<String, String>>? {
        try {
            if (!TextUtils.isEmpty(fileName) && !TextUtils.isEmpty(key)) {
                getSharedPreferences(ctx, fileName, mode)
                val value = sharedPreferences?.getString(key, defValue)
                return if (!TextUtils.isEmpty(value)) {
                    return if (isEncrypt) {
                        var key = SecurityUtil.getInstance().encryptMD5(key)
                        ObjectInputStream(ByteArrayInputStream(SecurityUtil.getInstance().decryptAES(Base64.decode(value, Base64.DEFAULT), key))).readObject() as List<Map<String, String>>
                    } else {
                        ObjectInputStream(ByteArrayInputStream(Base64.decode(value, Base64.DEFAULT))).readObject() as List<Map<String, String>>
                    }
                } else {
                    null
                }
            } else {
                return null
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        } catch (e: BadPaddingException) {
            e.printStackTrace()
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return null
    }

    fun remove(ctx: Context?, fileName: String, mode: Int, key: String) {
        remove(ctx, fileName, mode, key, false)
    }

    fun remove(ctx: Context?, fileName: String, mode: Int, key: String?, isEncrypt: Boolean) {
        getSharedPreferences(ctx, fileName, mode)
        sharedPreferences?.edit()?.remove(if (isEncrypt) SecurityUtil.getInstance().encryptMD5(key) else key)?.apply()
    }

    fun clear(ctx: Context?, fileName: String, mode: Int) {
        getSharedPreferences(ctx, fileName, mode)
        sharedPreferences?.edit()?.clear()?.apply()
    }
}