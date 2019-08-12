package com.hynet.heebit.components.utils

import android.app.ActivityManager
import android.content.Context
import android.content.res.Configuration
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.TelephonyManager
import android.text.format.Formatter
import com.alibaba.fastjson.JSONObject
import com.hynet.heebit.components.constant.Constant
import java.io.*
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import java.util.*
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException

class DeviceUtil {


    companion object {

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            DeviceUtil()
        }

    }

    fun getDeviceModel(): String {
        return Build.MODEL
    }

    private fun getSDKVersion(): String {
        return Build.VERSION.SDK_INT.toString()
    }

    fun getSystemVersion(): String {
        return Build.VERSION.RELEASE
    }

    fun getSystemName(): String {
        return Build.VERSION.CODENAME
    }

    private fun getDeviceBrand(): String {
        return Build.BRAND
    }

    private fun getDeviceSoftwareVersion(ctx: Context?): String? {
        return (ctx?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).deviceSoftwareVersion
    }

    fun getDeviceId(ctx: Context?): String? {
        var deviceId: String? = null
        try {
            if (ctx != null) {
                val telephonyManager = ctx.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                deviceId = telephonyManager.deviceId
                if (deviceId == null) {
                    deviceId = installationId(ctx)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return deviceId
    }

    @Synchronized
    @Throws(IOException::class)
    private fun installationId(context: Context): String {
        val file = File(context.filesDir, Constant.Profile.CKPAY_ID)
        if (!file.exists()) {
            writeInstallationFile(file)
        }
        return readInstallationFile(file)
    }

    @Throws(IOException::class)
    private fun readInstallationFile(installation: File): String {
        val file = RandomAccessFile(installation, "r")
        val bytes = ByteArray(file.length().toInt())
        file.readFully(bytes)
        file.close()
        return String(bytes)
    }

    @Throws(IOException::class)
    private fun writeInstallationFile(installation: File) {
        val out = FileOutputStream(installation)
        val id = UUID.randomUUID().toString()
        out.write(id.toByteArray())
        out.close()
    }

    private fun getLine1Number(ctx: Context?): String? {
        return (ctx?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).line1Number
    }

    private fun getNetworkCountryIso(ctx: Context?): String? {
        return (ctx?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).networkCountryIso
    }

    private fun getNetworkOperator(ctx: Context?): String? {
        return (ctx?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).networkOperator
    }

    private fun getNetworkOperatorName(ctx: Context?): String? {
        return (ctx?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).networkOperatorName
    }

    private fun getNetworkType(ctx: Context?): String? {
        return (ctx?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).networkType.toString()
    }

    private fun getPhoneType(ctx: Context?): String? {
        return (ctx?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).phoneType.toString()
    }

    private fun getSimCountryIso(ctx: Context?): String? {
        return (ctx?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).simCountryIso
    }

    private fun getSimOperator(ctx: Context?): String? {
        return (ctx?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).simOperator
    }

    private fun getSimOperatorName(ctx: Context?): String? {
        return (ctx?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).simOperatorName
    }

    private fun getSimSerialNumber(ctx: Context?): String? {
        return (ctx?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).simSerialNumber
    }

    private fun getSimState(ctx: Context?): String? {
        return (ctx?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).simState.toString()
    }

    private fun getSubscriberId(ctx: Context?): String? {
        return (ctx?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).subscriberId
    }

    private fun getMacAddress(ctx: Context?): String? {
        return (ctx?.getSystemService(Context.WIFI_SERVICE) as WifiManager).connectionInfo.macAddress
    }

    fun getDeviceIp(): String? {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val address = enumIpAddr.nextElement()
                    if (!address.isLoopbackAddress && address is Inet4Address) {
                        return address.getHostAddress()
                    }
                }
            }
        } catch (e: SocketException) {
            e.printStackTrace()
        }
        return null
    }

    fun getDeviceIp(name: String): ByteArray? {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                if (intf.name == name) {
                    val enumIpAddr = intf.inetAddresses
                    while (enumIpAddr.hasMoreElements()) {
                        val inetAddress = enumIpAddr.nextElement()
                        if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                            return inetAddress.getAddress()
                        }
                    }
                }
            }
        } catch (e: SocketException) {
            e.printStackTrace()
        }
        return null
    }

    fun getCpuCores(): Int {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            return 1
        }
        var cores: Int
        try {
            cores = File("/sys/devices/system/cpu/").listFiles(FileFilter { pathname ->
                val path = pathname.name
                //regex is slow, so checking char by char.
                if (path.startsWith("cpu")) {
                    for (i in 3 until path.length) {
                        if (path[i] < '0' || path[i] > '9') {
                            return@FileFilter false
                        }
                    }
                    return@FileFilter true
                }
                false
            })!!.size
        } catch (e: SecurityException) {
            cores = 0
        } catch (e: NullPointerException) {
            cores = 0
        }
        return cores
    }

    private fun getDeviceMemory(ctx: Context): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            val info = ActivityManager.MemoryInfo()
            val manager = ctx.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            manager.getMemoryInfo(info)
            return info.totalMem.toString()
        } else {
            var memory: Long = 0
            try {
                val fileReader = FileReader("/proc/meminfo")
                val bufferReader = BufferedReader(fileReader, 8192)
                memory = (Integer.valueOf(bufferReader.readLine().split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]) * 1024).toLong()
                bufferReader.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return Formatter.formatFileSize(ctx, memory)
        }
    }

    fun isPhone(context: Context): Boolean {
        return (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).phoneType != TelephonyManager.PHONE_TYPE_NONE
    }

    fun getDeviceType(context: Context): String {
        return if (context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
            "Android_Table"
        } else if (Build.BRAND == "SUNMI") {
            Build.BRAND
        } else {
            "Android_Mobile"
        }
    }

    fun getDeviceInfo(ctx: Context?, isEncrypt: Boolean, appVersion: String, clientVersion: String): String? {
        try {
            val jsonObject = JSONObject()
            //            jsonObject.put(Constant.Device.DEVICE_VERSION, getSystemVersion());
            //            jsonObject.put(Constant.Device.DEVICE_VERSION_NAME, getSystemName());
            //            jsonObject.put(Constant.Device.DEVICE_TYPE, getPhoneType(ctx));
            //            jsonObject.put(Constant.Device.DEVICE_ID, getDeviceId(ctx));
            //            jsonObject.put(Constant.Device.DEVICE_NAME, getDeviceModel());
            //            jsonObject.put(Constant.Device.DEVICE_CORE, getCpuCores());
            //            jsonObject.put(Constant.Device.SUBSCRIBER_ID, getSubscriberId(ctx));
            //            jsonObject.put(Constant.Device.DEVICE_IP, getDeviceIp());
            //             LogUtil.Companion.getInstance().print(Constant.Device.DEVICE_VERSION + Regex.EQUALS.getRegext() + getSystemVersion() + Regex.COMMA.getRegext());
            //             LogUtil.Companion.getInstance().print(Constant.Device.DEVICE_VERSION_NAME + Regex.EQUALS.getRegext() + getSystemName() + Regex.COMMA.getRegext());
            //             LogUtil.Companion.getInstance().print( Constant.Device.DEVICE_TYPE + Regex.EQUALS.getRegext() + getPhoneType(ctx) + Regex.COMMA.getRegext());
            //             LogUtil.Companion.getInstance().print(Constant.Device.DEVICE_ID + Regex.EQUALS.getRegext() + getDeviceId(ctx) + Regex.COMMA.getRegext());
            //             LogUtil.Companion.getInstance().print(Constant.Device.DEVICE_NAME + Regex.EQUALS.getRegext() + getDeviceModel() + Regex.COMMA.getRegext());
            //             LogUtil.Companion.getInstance().print(Constant.Device.DEVICE_CORE + Regex.EQUALS.getRegext() + getCpuCores() + Regex.COMMA.getRegext());
            //             LogUtil.Companion.getInstance().print(Constant.Device.SUBSCRIBER_ID + Regex.EQUALS.getRegext() + getSubscriberId(ctx) + Regex.COMMA.getRegext());
            //             LogUtil.Companion.getInstance().print(Constant.Device.DEVICE_IP + Regex.EQUALS.getRegext() + getDeviceIp() + Regex.COMMA.getRegext());
            //             LogUtil.Companion.getInstance().print(object.toString());

            jsonObject[Constant.Device.APP_VERSION] = appVersion
            jsonObject[Constant.Device.OS_TYPE] = "Android"
            jsonObject[Constant.Device.CLIENT_VERSION] = clientVersion
            jsonObject[Constant.Device.UID] = getDeviceId(ctx)
            jsonObject[Constant.Device.OS_VERSION] = getSystemVersion()
            jsonObject[Constant.Device.DEVICE_TYPE] = "Android"
            LogUtil.instance.print("jsonObject:$jsonObject")
            return if (isEncrypt) {
                SecurityUtil.getInstance().encrypt3Des(jsonObject.toString(), Constant.Data.KEY, Constant.Data.FROMAT)
            } else {
                jsonObject.toString()
            }
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        } catch (e: InvalidKeySpecException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        } catch (e: BadPaddingException) {
            e.printStackTrace()
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: InvalidAlgorithmParameterException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        }
        return null
    }
}