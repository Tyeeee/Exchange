package com.hynet.heebit.components.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.telephony.TelephonyManager
import com.hynet.heebit.components.constant.NetType
import java.io.*
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.Proxy 
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class NetworkUtil {

    companion object {

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            NetworkUtil()
        }

    }

    private var proxy: Proxy? = null

    init {
        setDefaultHostnameVerifier()
    }

    fun hasInternetConnected(ctx: Context): Boolean {
        val connectivityManager = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (network in connectivityManager.allNetworks) {
                var networkInfo = connectivityManager.getNetworkInfo(network)
                if (networkInfo?.state == NetworkInfo.State.CONNECTED) {
                    return true
                }
            }
        } else {
            for (networkInfo in connectivityManager.allNetworkInfo) {
                if (networkInfo.state == NetworkInfo.State.CONNECTED) {
                    return true
                }
            }
        }


        return false
    }

    private fun setDefaultHostnameVerifier() {
        HttpsURLConnection.setDefaultHostnameVerifier({ s, sslSession -> true })
    }

    fun download(url: String, file: File): Boolean {
        var result = false
        val connection: HttpURLConnection
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        try {
            if (proxy != null) {
                connection = URL(url).openConnection(proxy) as HttpURLConnection
            } else {
                connection = URL(url).openConnection() as HttpURLConnection
            }
            connection.connectTimeout = 60 * 1000
            connection.readTimeout = 60 * 1000
            connection.doInput = true
            connection.connect()
            LogUtil.instance.print("----size:" + connection.contentLength)

            val buffer = ByteArray(1024)
            var length: Int
            val startPosition = 0
            var currPosition: Int

            if (!file.exists()) {
                file.createNewFile()
            }
            currPosition = startPosition
            if (connection.responseCode == HttpURLConnection.HTTP_OK || connection.responseCode == HttpURLConnection.HTTP_PARTIAL) {
                inputStream = connection.inputStream
                outputStream = FileOutputStream(file, true)
                do {
                    length = inputStream!!.read(buffer)
                    if (length > 0) {
                        outputStream.write(buffer, 0, length)
                        currPosition += length
                    }
                } while (length != -1)
                result = true
            } else {
                result = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            result = false
        } finally {
            try {
                inputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                outputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return result
    }

    fun getNetworkType(ctx: Context): String? {
        var type: String? = null
        val info = (ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
        if (info != null && info.isConnected) {
            if (info.type == ConnectivityManager.TYPE_WIFI) {
                type = NetType.WIFI.content
            } else if (info.type == ConnectivityManager.TYPE_MOBILE) {
                // TD-SCDMA   networkType is 17
                val networkType = info.subtype
                when (networkType) {
                    TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN //api<8 : replace by 11
                    -> type = NetType.GG.content
                    TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B //api<9 : replace by 14
                        , TelephonyManager.NETWORK_TYPE_EHRPD  //api<11 : replace by 12
                        , TelephonyManager.NETWORK_TYPE_HSPAP  //api<13 : replace by 15
                    -> type = NetType.GGG.content
                    TelephonyManager.NETWORK_TYPE_LTE    //api<11 : replace by 13
                    -> type = NetType.GGGG.content
                    else -> if (info.subtypeName.equals(NetType.TD_SCDMA.content, ignoreCase = true)
                            || info.subtypeName.equals(NetType.WCDMA.content, ignoreCase = true)
                            || info.subtypeName.equals(NetType.CDMA2000.content, ignoreCase = true)) {
                        type = "3G"
                    } else {
                        type = info.subtypeName
                    }
                }
            }
        }
        return type
    }

    private fun detectProxy(ctx: Context) {
        val manager = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = manager.activeNetworkInfo
        if (info != null && info.isAvailable && info.type == ConnectivityManager.TYPE_MOBILE) {
            val proxyHost = android.net.Proxy.getDefaultHost()
            val port = android.net.Proxy.getDefaultPort()
            if (proxyHost != null) {
                val sa = InetSocketAddress(proxyHost, port)
                proxy = Proxy(Proxy.Type.HTTP, sa)
            }
        }
    }

}