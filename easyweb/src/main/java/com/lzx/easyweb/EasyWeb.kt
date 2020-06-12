package com.lzx.easyweb

import android.app.Activity
import android.os.Build
import android.os.SystemClock
import androidx.annotation.RequiresApi
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.lzx.easyweb.code.WebBuilder
import com.lzx.easyweb.code.WebValueCallback

class EasyWeb(private val builder: WebBuilder) : LifecycleObserver {

    companion object {
        const val TAG = "EasyWeb_"
        var startTime = 0L
        var initStartTime = 0L

        fun with(activity: Activity?): WebBuilder {
            if (activity == null) {
                throw NullPointerException("context can not be null")
            }
            return WebBuilder(activity)
        }
    }

    init {
        startTime = SystemClock.uptimeMillis()
        initStartTime = SystemClock.uptimeMillis()
        builder.lifecycle?.get()?.removeObserver(this)
        builder.lifecycle?.get()?.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        getProxyWebView()?.onWebResume()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        getProxyWebView()?.onWebPause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        getJsInterface()?.clearJsInterface()
        getProxyWebView()?.onWebDestroy()
    }

    fun getWebView() = builder.view
    fun getProxyWebView() = builder.proxyWebView
    fun getActivity() = builder.activityWeak?.get() ?: throw NullPointerException("context is null")
    fun getUiManager() = builder.uiManager
    fun getViewGroup() = builder.viewGroup
    fun getLayoutParams() = builder.layoutParams
    fun getWebViewClient() = builder.webViewClient
    fun getWebChromeClient() = builder.webChromeClient
    fun getOnWebViewLongClick() = builder.onWebViewLongClick
    fun getWebViewSetting() = builder.webViewSetting?.getWebSetting()
    fun getJsInterface() = builder.jsInterface
    fun getUrlLoader() = builder.urlLoader
    fun getJsLoader() = builder.jsLoader
    fun isDebug() = builder.isDebug

    fun loadUrl(
        url: String?,
        headers: Map<String?, String?>? = null
    ): EasyWeb {
        getUrlLoader()?.loadUrl(url, headers)
        return this
    }

    fun reload(): EasyWeb {
        getUrlLoader()?.reload()
        return this
    }

    fun stopLoading(): EasyWeb {
        getUrlLoader()?.stopLoading()
        return this
    }

    fun postUrl(url: String?, params: ByteArray?): EasyWeb {
        getUrlLoader()?.postUrl(url, params)
        return this
    }

    fun loadData(
        data: String?,
        mimeType: String?,
        encoding: String?
    ): EasyWeb {
        getUrlLoader()?.loadData(data, mimeType, encoding)
        return this
    }

    fun loadDataWithBaseURL(
        baseUrl: String?, data: String?,
        mimeType: String?, encoding: String?, historyUrl: String?
    ): EasyWeb {
        getUrlLoader()?.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl)
        return this
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun loadJs(
        method: String?,
        callback: WebValueCallback<String?>? = null,
        vararg params: Any?
    ): EasyWeb {
        getJsLoader()?.loadJs(method, callback, params)
        return this
    }

    fun loadJs(method: String?, vararg params: Any?): EasyWeb {
        getJsLoader()?.loadJs(method, params)
        return this
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun loadJs(method: String?, callback: WebValueCallback<String?>? = null): EasyWeb {
        getJsLoader()?.loadJs(method, callback)
        return this
    }
}
