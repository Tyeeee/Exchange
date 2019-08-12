package com.hynet.heebit.components.utils

import android.annotation.TargetApi
import android.app.ActivityManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Vibrator
import android.text.TextUtils
import com.hynet.heebit.components.constant.Regex
import java.io.IOException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

class ApplicationUtil {

    companion object {

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ApplicationUtil()
        }

    }

    fun isDebug(context: Context): Boolean {
        try {
            val applicationInfo = context.applicationInfo
            return applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        } catch (e: Exception) {
            return false
        }
    }

    fun getPackageName(ctx: Context?): String? {
        return ctx?.packageName
    }

    @Throws(PackageManager.NameNotFoundException::class)
    fun getPackageInfo(ctx: Context?): PackageInfo? {
        return ctx?.packageManager?.getPackageInfo(getPackageName(ctx), 0)
    }

    fun getVersionName(ctx: Context?): String? {
        return getPackageInfo(ctx)?.versionName
    }

    fun getVersionCode(ctx: Context?): Int? {
        return getPackageInfo(ctx)?.versionCode
    }

    fun isProcessPerceptible(ctx: Context?): Boolean {
        if (ctx == null) {
            return false
        }
        val runningAppProcessInfos = (ctx.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).runningAppProcesses
        if (runningAppProcessInfos != null && runningAppProcessInfos.size > 0) {
            for (runningAppProcessInfo in runningAppProcessInfos) {
                if (runningAppProcessInfo.processName == ctx.packageName) {
                    if (runningAppProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE || runningAppProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        return false
                    }
                }
            }
        }
        return true
    }

    fun getProcessName(ctx: Context?): String? {
        for (runningAppProcessInfo in (ctx?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).runningAppProcesses) {
            if (runningAppProcessInfo.pid == android.os.Process.myPid()) {
                return runningAppProcessInfo.processName
            }
        }
        return null
    }

    fun isProcessRunning(context: Context?, processName: String?): Boolean {
        val activityManager = context?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcessInfos = activityManager.runningAppProcesses
        for (runningAppProcessInfo in runningAppProcessInfos) {
            if (runningAppProcessInfo.processName == processName) {
                return true
            }
        }
        return false
    }

    fun isProcessRunningTop(ctx: Context?, processName: String?): Boolean {
        val activityManager = ctx?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val list = activityManager.getRunningTasks(1)
        if (list != null && list.size > 0) {
            val componentName = list[0].topActivity
            LogUtil.instance.print("proessName:" + processName + ", componentName.getClassName():" + componentName?.getClassName())
            if (processName == componentName?.getClassName()) {
                return true
            }
        }
        return false
    }

    private fun isAppRunning(ctx: Context?, packageName: String?, number: Int): Boolean {
        val activityManager = ctx?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningTaskInfos = activityManager.getRunningTasks(number)
        for (runningTaskInfo in runningTaskInfos) {
            if (runningTaskInfo != null && runningTaskInfo.baseActivity?.getPackageName() == packageName) {
                return true
            }
        }
        return false
    }

    fun isAppRunningTop(ctx: Context?, packageName: String?): Boolean {
        val activityManager = ctx?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningTaskInfos = activityManager.getRunningTasks(1)
        if (runningTaskInfos != null && runningTaskInfos.size > 0) {
            val componentName = runningTaskInfos[0].topActivity
            LogUtil.instance.print("packageName:" + packageName + ", cpn.getPackageName():" + componentName?.getPackageName())
            if (packageName == componentName?.getClassName()) {
                return true
            }
        }
        return false
    }

    fun isServiceRunning(ctx: Context?, className: String?): Boolean {
        val activityManager = ctx?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val infos = activityManager.getRunningServices(Integer.MAX_VALUE)
        if (infos.size != 0) {
            for (i in infos.indices) {
                if (infos[i].service.className == className) {
                    return true
                }
            }
        }
        return false
    }

    fun copyToClipBoard(ctx: Context?, content: String?, onPrimaryClipChangedListener: ClipboardManager.OnPrimaryClipChangedListener?) {
        val clipboardManager = ctx?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.setPrimaryClip(ClipData.newPlainText("label", content))
        if (onPrimaryClipChangedListener != null) {
            clipboardManager.addPrimaryClipChangedListener(onPrimaryClipChangedListener)
        }
    }

    fun getClipBoardData(ctx: Context?): CharSequence? {
        val clipboardManager = ctx?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = clipboardManager.primaryClip
        return if (clipData != null && clipData.itemCount > 0) {
            clipData.getItemAt(0).text
        } else {
            null
        }
    }

    fun hasInstalled(ctx: Context?, packageName: String?): Boolean {
        val packageInfos = ctx?.packageManager?.getInstalledPackages(0)
        if (packageInfos != null) {
            for (packageInfo in packageInfos) {
                if (packageName == packageInfo.packageName) {
                    return true
                }
            }
        }
        return false
    }

    fun getInstallStatus(ctx: Context?, packageName: String?): Boolean? {
        if (TextUtils.isEmpty(packageName)) {
            for (packageInfo in ctx?.packageManager?.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES)!!) {
                if (packageInfo.packageName.equals(packageName, ignoreCase = true)) {
                    return true
                }
            }
        }
        return false
    }

    fun supportHardwareAccelerated(): Boolean {
        return android.os.Build.VERSION.SDK_INT >= 11
    }

    fun vibrate(ctx: Context?, duration: Long) {
        val vibrator = ctx?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(longArrayOf(0, duration), -1)
    }

    @Throws(PackageManager.NameNotFoundException::class)
    fun getUid(ctx: Context?): Int? {
        return ctx?.packageManager?.getApplicationInfo(getPackageName(ctx), PackageManager.GET_META_DATA)?.uid
    }

    fun getMetaData(ctx: Context, key: String): Any? {
        try {
            return ctx.packageManager.getApplicationInfo(getPackageName(ctx), PackageManager.GET_META_DATA).metaData.get(key)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return null
        }
    }

    fun chmod(permission: String, path: String): Boolean {
        try {
            val pr = Runtime.getRuntime().exec(StringBuilder(Regex.CHMOD.regext).append(permission).append(Regex.SPACE.regext).append(path).toString())
            pr.waitFor()
            return true
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return false
    }

    fun getSha1(ctx: Context?): String? {
        try {
            val info = ctx?.packageManager?.getPackageInfo(ctx?.packageName, PackageManager.GET_SIGNATURES)
            val publicKey = MessageDigest.getInstance("SHA1").digest(info?.signatures?.get(0)?.toByteArray())
            val hexString = StringBuffer()
            for (i in publicKey.indices) {
                val appendString = Integer.toHexString(0xFF and publicKey[i].toInt()).toUpperCase(Locale.US)
                if (appendString.length == 1)
                    hexString.append("0")
                hexString.append(appendString)
                hexString.append(":")
            }
            return hexString.toString()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return null
    }
}