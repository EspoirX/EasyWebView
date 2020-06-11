package com.lzx.easyweb.cache.interceptor


import android.util.Log
import com.lzx.easyweb.EasyWeb
import com.lzx.easyweb.cache.CacheConfig
import com.lzx.easyweb.cache.CacheRequest
import com.lzx.easyweb.cache.disklrucache.DiskLruCache
import com.lzx.easyweb.utils.CacheUtils
import okhttp3.Headers
import okio.buffer
import okio.sink
import okio.source
import java.io.File
import java.io.IOException
import java.util.*

class DiskResourceInterceptor : ResourceInterceptor {

    companion object {
        private const val ENTRY_META = 0
        private const val ENTRY_BODY = 1
        private const val ENTRY_COUNT = 2
    }

    private var mDiskLruCache: DiskLruCache? = null

    override fun load(chain: Chain?): WebResource? {
        val request = chain?.request
        createDiskLruCache(request)
        var webResource = getFromDiskCache(request?.key)
        if (isRealMimeTypeCacheable(webResource)) {
            Log.i(EasyWeb.TAG, String.format("WebView file form disk cache: %s", request?.url))
            return webResource
        }
        webResource = chain?.process(request)
        Log.i(EasyWeb.TAG, String.format("WebView file form http: %s", request?.url))
        if (webResource != null && (webResource.isCache || isRealMimeTypeCacheable(webResource))) {
            cacheToDisk(request?.key, webResource)
        }
        return webResource
    }

    private var requestUrl = ""

    @Synchronized
    private fun createDiskLruCache(request: CacheRequest?) {
        Log.i(
            "XIAN",
            "requestUrl = " + requestUrl + " request?.requestUrl = " + request?.requestUrl
        )
        if (requestUrl != request?.requestUrl) {
            mDiskLruCache = null
        }
        var dir = CacheConfig.instance.defaultCachePath
        if (!request?.requestUrl.isNullOrEmpty()) {
            dir += "/" + request?.requestUrl
        }
        if (mDiskLruCache != null && mDiskLruCache?.isClosed == false) {
            return
        }
        val version = CacheConfig.instance.version
        val cacheSize = CacheConfig.instance.cacheSize
        try {
            mDiskLruCache = DiskLruCache.open(File(dir), version.toInt(), ENTRY_COUNT, cacheSize)
            requestUrl = request?.requestUrl ?: ""
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun getFromDiskCache(key: String?): WebResource? {
        try {
            if (mDiskLruCache?.isClosed == true) {
                return null
            }
            val snapshot = mDiskLruCache?.get(key) ?: return null

            val entrySource = snapshot.getInputStream(ENTRY_META).source().buffer()
            // 1. read status
            val responseCode = entrySource.readUtf8LineStrict()
            val reasonPhrase = entrySource.readUtf8LineStrict()
            // 2. read headers
            var headerSize = entrySource.readDecimalLong()
            val headers: Map<String, String>?
            val responseHeadersBuilder = Headers.Builder()
            // read first placeholder line
            val placeHolder = entrySource.readUtf8LineStrict()
            if (placeHolder.trim { it <= ' ' }.isNotEmpty()) {
                responseHeadersBuilder.add(placeHolder)
                headerSize--
            }
            for (i in 0 until headerSize) {
                val line = entrySource.readUtf8LineStrict()
                if (!line.isNullOrEmpty()) {
                    responseHeadersBuilder.add(line)
                }
            }
            headers = CacheUtils.generateHeadersMap(responseHeadersBuilder.build())
            // 3. read body
            val inputStream = snapshot.getInputStream(ENTRY_BODY)
            if (inputStream != null) {
                val webResource = WebResource()
                webResource.message = reasonPhrase
                webResource.responseCode = responseCode.toInt()
                webResource.originBytes = CacheUtils.streamToBytes(inputStream)
                webResource.responseHeaders = headers
                webResource.isModified = false
                return webResource
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun isRealMimeTypeCacheable(resource: WebResource?): Boolean {

        if (resource == null) {
            return false
        }
        val headers = resource.responseHeaders ?: hashMapOf()
        var contentType: String? = null
        if (headers.isNotEmpty()) {
            val uppercaseKey = "Content-Type"
            val lowercaseKey = uppercaseKey.toLowerCase(Locale.getDefault())
            val contentTypeValue =
                if (headers.containsKey(uppercaseKey)) headers[uppercaseKey] else headers[lowercaseKey]
            if (!contentTypeValue.isNullOrEmpty()) {
                val contentTypeArray = contentTypeValue.split(";").toTypedArray()
                if (contentTypeArray.isNotEmpty()) {
                    contentType = contentTypeArray[0]
                }
            }
        }
        return contentType != null
    }

    /**
     * 保存到磁盘
     */
    private fun cacheToDisk(key: String?, webResource: WebResource?) {
        if (webResource == null) {
            return
        }
        if (mDiskLruCache?.isClosed == true) {
            return
        }
        try {
            val editor = mDiskLruCache?.edit(key) ?: return
            val metaOutput = editor.newOutputStream(ENTRY_META)
            var sink = metaOutput.sink().buffer()
            // 1. write status
            sink.writeUtf8(webResource.responseCode.toString()).writeByte('\n'.toInt())
            sink.writeUtf8(webResource.message.toString()).writeByte('\n'.toInt())
            // 2. write response header
            val headers = webResource.responseHeaders ?: hashMapOf()
            sink.writeDecimalLong(headers.size.toLong()).writeByte('\n'.toInt())
            for ((headerKey, headerValue) in headers) {
                sink.writeUtf8(headerKey)
                    .writeUtf8(": ")
                    .writeUtf8(headerValue)
                    .writeByte('\n'.toInt())
            }
            sink.flush()
            sink.close()
            // 3. write response body
            val bodyOutput = editor.newOutputStream(ENTRY_BODY)
            sink = bodyOutput.sink().buffer()
            val originBytes = webResource.originBytes
            if (originBytes != null && originBytes.isNotEmpty()) {
                sink.write(originBytes)
                sink.flush()
                sink.close()
                editor.commit()
            }
        } catch (e: IOException) {
            try {
                // clean the redundant data
                mDiskLruCache?.remove(key)
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}