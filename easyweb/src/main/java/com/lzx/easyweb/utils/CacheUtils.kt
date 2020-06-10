package com.lzx.easyweb.utils

import android.webkit.MimeTypeMap
import okhttp3.Headers
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.security.MessageDigest
import java.util.*

object CacheUtils {

    private const val MB = 1024 * 1024
    private const val MB_15 = 15 * MB
    private const val MB_10 = 10 * MB
    private const val MB_5 = 5 * MB

    fun getMemorySize(): Int {
        val maxMemorySize = Runtime.getRuntime().maxMemory()
        val maxSizeByMB = (maxMemorySize / MB).toInt()
        if (maxSizeByMB >= 512) {
            return MB_15
        }
        if (maxSizeByMB >= 256) {
            return MB_10
        }
        return if (maxSizeByMB > 128) {
            MB_5
        } else 0
    }

    fun getFileExtensionFromUrl(value: String?): String? {
        var url = value?.toLowerCase(Locale.getDefault())
        if (!url.isNullOrEmpty()) {
            val fragment = url.lastIndexOf('#')
            if (fragment > 0) {
                url = url.substring(0, fragment)
            }
            val query = url.lastIndexOf('?')
            if (query > 0) {
                url = url.substring(0, query)
            }
            val filenamePos = url.lastIndexOf('/')
            val filename = if (0 <= filenamePos) url.substring(filenamePos + 1) else url
            // if the filename contains special characters, we don't
            // consider it valid for our matching purposes:
            if (filename.isNotEmpty()) {
                val dotPos = filename.lastIndexOf('.')
                if (0 <= dotPos) {
                    return filename.substring(dotPos + 1)
                }
            }
        }
        return ""
    }

    fun getMimeTypeFromExtension(extension: String?): String? {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }

    fun getMD5(message: String, upperCase: Boolean): String? {
        var md5str: String? = ""
        try {
            val md = MessageDigest.getInstance("MD5")
            val input = message.toByteArray()
            val buff = md.digest(input)
            md5str = bytesToHex(buff, upperCase)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return md5str
    }

    fun bytesToHex(bytes: ByteArray, upperCase: Boolean): String? {
        val md5str = StringBuffer()
        var digital: Int
        for (i in bytes.indices) {
            digital = bytes[i].toInt()
            if (digital < 0) {
                digital += 256
            }
            if (digital < 16) {
                md5str.append("0")
            }
            md5str.append(Integer.toHexString(digital))
        }
        return if (upperCase) md5str.toString().toUpperCase(Locale.getDefault())
        else md5str.toString().toLowerCase(Locale.getDefault())
    }

    fun generateHeadersMap(headers: Headers): Map<String, String>? {
        val headersMap: MutableMap<String, String> =
            HashMap()
        var index = 0
        for (key in headers.names()) {
            val values = StringBuilder()
            for (value in headers.values(key)) {
                values.append(value)
                if (index++ > 0) {
                    values.append(",")
                }
            }
            index = 0
            headersMap[key] = values.toString()
        }
        return headersMap
    }

    @Throws(IOException::class)
    fun streamToBytes(`in`: InputStream): ByteArray? {
        val out = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var len: Int
        while (`in`.read(buffer).also { len = it } > -1) {
            out.write(buffer, 0, len)
        }
        out.flush()
        `in`.close()
        return out.toByteArray()
    }
}