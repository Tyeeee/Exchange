package com.hynet.heebit.components.utils

import android.os.Handler
import com.hynet.heebit.components.constant.Regex
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.*

class HttpUtil {

    companion object {

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            HttpUtil()
        }

    }

    fun doGet(parameter: HashMap<String, String>?, url: String, handler: Handler, vararg message: Int) {
        var connection: HttpURLConnection? = null
        try {
            val builder = StringBuilder(url)
            if (parameter != null && parameter.size != 0) {
                builder.append(Regex.QUESTION_MARK1.regext)
                var isFirst = true
                for ((key, value) in parameter) {
                    if (!isFirst) {
                        builder.append(Regex.AND1.regext)
                    }
                    builder.append(key)
                    builder.append(Regex.EQUALS1.regext)
                    builder.append(value)
                    isFirst = false
                }
            }
            connection = URL(builder.toString()).openConnection() as HttpURLConnection
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
            connection.requestMethod = "GET"
            connection.doInput = true
            connection.useCaches = false
            connection.instanceFollowRedirects = true
            connection.connectTimeout = 30 * 1000
            connection.readTimeout = 18 * 1000
            connection.connect()
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                handler.sendMessage(MessageUtil.instance.getMessage(message[0], convertStreamToString(connection.inputStream)!!))
            } else {
                handler.sendMessage(MessageUtil.instance.getMessage(message[1], "服务器连接出错,请稍后重试"))
            }
        } catch (e: Exception) {
            handler.sendMessage(MessageUtil.instance.getMessage(message[2], "服务器连接异常,请稍后重试"))
        } finally {
            connection?.disconnect()
        }
    }

    fun doPost(parameter: HashMap<String, String>, url: String, handler: Handler, vararg message: Int) {
        var connection: HttpURLConnection? = null
        try {
            val builder = StringBuilder()
            for ((key, value) in parameter) {
                builder.append(key).append(Regex.EQUALS1.regext).append(URLEncoder.encode(value, Regex.UTF_8.regext)).append(Regex.AND1.regext)
            }
            builder.deleteCharAt(builder.length - 1)
            LogUtil.instance.print("post url:$url")
            LogUtil.instance.print("post request:$builder")
            connection = URL(url).openConnection() as HttpURLConnection
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Length", builder.toString().toByteArray().size.toString())
            connection.doOutput = true
            connection.doInput = true
            connection.useCaches = false
            connection.instanceFollowRedirects = true
            connection.connectTimeout = 30 * 1000
            connection.readTimeout = 18 * 1000
            connection.connect()

            val outputStream = DataOutputStream(connection.outputStream)
            outputStream.writeBytes(builder.toString())
            outputStream.flush()
            outputStream.close()
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                handler.sendMessage(MessageUtil.instance.getMessage(message[0], convertStreamToString(connection.inputStream)!!))
            } else {
                handler.sendMessage(MessageUtil.instance.getMessage(message[1], "服务器连接出错,请稍后重试"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            handler.sendMessage(MessageUtil.instance.getMessage(message[2], "http excep"))
        } finally {
            connection?.disconnect()
        }
    }

    private fun convertStreamToString(inputStream: InputStream): String? {
        try {
            val bufferedReader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
            val stringBuilder = StringBuilder()
            var line: String? = null
            try {
                while ({ line = bufferedReader.readLine();line }() != null) {
                    stringBuilder.append(line)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            return stringBuilder.toString()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            return null
        }

    }

}