package com.hynet.heebit.components.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import androidx.core.content.FileProvider
import com.hynet.heebit.components.constant.Regex
import java.io.File

class FileProviderUtil {

    companion object {

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            FileProviderUtil()
        }

    }

    fun generateUri(context: Context?, intent: Intent, authority: String, file: File): Uri? {
        val uri = generateUri(context, authority, file)
        return if (uri != null) {
            grantUriPermission(context, intent, uri)
            uri
        } else null
    }

    fun generateUri(context: Context?, authority: String, file: File): Uri? {
        return if (file.path.startsWith(Regex.HTTP.regext) || file.path.startsWith(Regex.HTTPS.regext)) {
            return Uri.parse(file.path)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) FileProvider.getUriForFile(context!!, authority, file) else Uri.fromFile(file)
    }

    private fun grantUriPermission(context: Context?, intent: Intent?, uri: Uri?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return
        }
        if (context == null || intent == null || uri == null) {
            return
        }
        if (uri.scheme!!.startsWith("http") || uri.scheme!!.startsWith("https")) {
            return
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        val resolveInfos = context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resolveInfos) {
            val packageName = resolveInfo.activityInfo.packageName
            LogUtil.instance.print("resolveInfo:$packageName")
            if (!TextUtils.isEmpty(packageName)) {
                context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
        }
    }

    fun revokeUriPermission(context: Context?, intent: Intent?, uri: Uri?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return
        }
        if (context == null || intent == null || uri == null) {
            return
        }
        if (uri.scheme!!.startsWith("http") || uri.scheme!!.startsWith("https")) {
            return
        }
        context.revokeUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    }

}