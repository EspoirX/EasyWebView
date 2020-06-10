package com.lzx.easyweb.cache

import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import com.lzx.easyweb.cache.interceptor.*
import com.lzx.easyweb.utils.CacheUtils
import java.io.ByteArrayInputStream
import java.io.InputStream

class WebViewCacheManager constructor(private val context: Context) {

    private val interceptors = mutableListOf<ResourceInterceptor?>()

    fun requestResource(
        webResourceRequest: WebResourceRequest?,
        cacheMode: WebCacheMode?,
        userAgent: String
    ): WebResourceResponse? {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            val url = webResourceRequest?.url.toString()
            val extension = CacheUtils.getFileExtensionFromUrl(url)
            val mimeType = CacheUtils.getMimeTypeFromExtension(extension)

            val cacheRequest = CacheRequest()
            cacheRequest.url = url
            cacheRequest.mimeType = mimeType
            cacheRequest.userAgent = userAgent
            cacheRequest.webViewCacheMode = cacheMode
            cacheRequest.headers = webResourceRequest?.requestHeaders
            return buildCacheChain(cacheRequest)
        }
        return null
    }

    private fun buildCacheChain(cacheRequest: CacheRequest): WebResourceResponse? {
        interceptors.add(MemResourceInterceptor.instance)
        interceptors.add(DiskResourceInterceptor())
        interceptors.add(HttpResourceInterceptor(context))
        val chain = Chain(interceptors)
        val resource = chain.process(cacheRequest)
        return createWebResourceResponse(resource, cacheRequest.mimeType)
    }

    private fun createWebResourceResponse(
        resource: WebResource?,
        mimeType: String?
    ): WebResourceResponse? {
        if (resource == null) {
            return null
        }
        var urlMime = mimeType
        val headers = resource.responseHeaders
        var contentType: String? = null
        var charset: String? = null
        if (headers != null) {
            val contentTypeKey = "Content-Type"
            if (headers.containsKey(contentTypeKey)) {
                val contentTypeValue = headers[contentTypeKey]
                if (!contentTypeValue.isNullOrEmpty()) {
                    val contentTypeArray = contentTypeValue.split(";".toRegex()).toTypedArray()
                    if (contentTypeArray.isNotEmpty()) {
                        contentType = contentTypeArray[0]
                    }
                    if (contentTypeArray.size >= 2) {
                        charset = contentTypeArray[1]
                        val charsetArray = charset.split("=".toRegex()).toTypedArray()
                        if (charsetArray.size >= 2) {
                            charset = charsetArray[1]
                        }
                    }
                }
            }
        }
        if (!TextUtils.isEmpty(contentType)) {
            urlMime = contentType
        }
        if (TextUtils.isEmpty(urlMime)) {
            return null
        }
        if (TextUtils.isEmpty(charset)) {
            charset = "UTF-8"
        }
        val resourceBytes = resource.originBytes
        if (resourceBytes == null || resourceBytes.size < 0) {
            return null
        }
        val bis: InputStream = ByteArrayInputStream(resourceBytes)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val status = resource.responseCode
            var reasonPhrase = resource.message
            if (reasonPhrase.isNullOrEmpty() && status == 200) {
                reasonPhrase = "OK"
            }
            return WebResourceResponse(
                urlMime,
                charset,
                status,
                reasonPhrase ?: "",
                resource.responseHeaders,
                bis
            )
        }
        return WebResourceResponse(urlMime, charset, bis)
    }


    fun addResourceInterceptor(interceptor: ResourceInterceptor?) {
        interceptors.add(interceptor)
    }
}