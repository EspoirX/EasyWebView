package com.lzx.easyweb.cache.interceptor

import android.content.Context
import android.os.Build
import android.util.Log
import android.webkit.WebSettings
import com.lzx.easyweb.BuildConfig
import com.lzx.easyweb.EasyWeb
import com.lzx.easyweb.cache.CacheConfig
import com.lzx.easyweb.cache.WebCacheMode
import com.lzx.easyweb.utils.CacheUtils
import okhttp3.*
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier

class HttpResourceInterceptor(private val context: Context) : ResourceInterceptor {

    private val mClient: OkHttpClient

    companion object {
        private const val HEADER_USER_AGENT = "User-Agent"
        private const val DEFAULT_USER_AGENT = "WebView" + BuildConfig.VERSION_NAME
    }

    init {
        val path = CacheConfig.instance.defaultCachePath + "/http"
        val file = File(path)
        val cache = Cache(file, CacheConfig.instance.cacheSize)
        mClient = OkHttpClient.Builder()
            .cache(cache)
            .hostnameVerifier(HostnameVerifier { _, _ -> true })
            .readTimeout(
                CacheConfig.instance.readTimeout.toLong(),
                TimeUnit.MILLISECONDS
            )
            .connectTimeout(
                CacheConfig.instance.connectTimeout.toLong(),
                TimeUnit.MILLISECONDS
            )
            .addNetworkInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    val originResponse = chain.proceed(chain.request())
                    return originResponse.newBuilder()
                        .removeHeader("pragma")
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", "max-age=86400").build()
                }
            })
            .build()
    }

    override fun load(chain: Chain?): WebResource? {
        val request = chain?.request
        val sourceRequest = request?.let { SourceRequest(it, true) }
        val resource = sourceRequest?.let { getResource(it) }
        return resource ?: chain?.process(request)
    }

    private fun getResource(sourceRequest: SourceRequest): WebResource? {
        val url = sourceRequest.url
        if (url.isNullOrEmpty()) return null
        val isCacheByOkHttp = sourceRequest.isCacheable
        val cacheControl = CacheControl.Builder().noStore().build()
        var userAgent = sourceRequest.userAgent
        if (userAgent.isNullOrEmpty()) {
            userAgent = DEFAULT_USER_AGENT
        }
        val locale = Locale.getDefault()
        var acceptLanguage: String
        acceptLanguage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            locale.toLanguageTag()
        } else {
            locale.language
        }
        if (!acceptLanguage.equals("en-US", ignoreCase = true)) {
            acceptLanguage += ",en-US;q=0.9"
        }
        val requestBuilder = Request.Builder()
            .removeHeader(HEADER_USER_AGENT)
            .addHeader(HEADER_USER_AGENT, userAgent)
            .addHeader("Upgrade-Insecure-Requests", "1")
            .addHeader("X-Requested-With", context.packageName)
            .addHeader("Accept", "*/*")
            .addHeader("Accept-Language", acceptLanguage)
        val headers = sourceRequest.headers
        if (headers != null && headers.isNotEmpty()) {
            for ((key, value) in headers) {
                requestBuilder.removeHeader(key)
                requestBuilder.addHeader(key, value)
            }
        }
        val request = requestBuilder
            .url(url)
            .cacheControl(cacheControl)
            .get()
            .build()
        var response: Response
        try {
            val remoteResource = WebResource()
            response = mClient.newCall(request).execute()
            val cacheRes = response.cacheResponse
            if (cacheRes != null) {
                Log.i(EasyWeb.TAG, String.format("http file from cache: %s", url))
            } else {
                Log.i(EasyWeb.TAG, String.format("http file from server: %s", url))
            }
            remoteResource.responseCode = response.code
            remoteResource.message = response.message
            remoteResource.isModified = response.code != HttpURLConnection.HTTP_NOT_MODIFIED
            val responseBody = response.body
            remoteResource.originBytes = responseBody?.bytes()
            remoteResource.responseHeaders = CacheUtils.generateHeadersMap(response.headers)
            remoteResource.isCache = !isCacheByOkHttp
            return remoteResource
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

}