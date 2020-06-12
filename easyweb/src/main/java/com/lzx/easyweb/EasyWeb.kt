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
import com.lzx.easyweb.js.BaseJavascriptInterface

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
    fun getJsInterface(key: String) = getJsInterface()?.getJsInterface(key)
    fun getUrlLoader() = builder.urlLoader
    fun getJsLoader() = builder.jsLoader
    fun isDebug() = builder.isDebug

    fun loadUrl(
        url: String?,
        headers: Map<String?, String?>? = null
    ) = apply { getUrlLoader()?.loadUrl(url, headers) }

    fun reload() = apply { getUrlLoader()?.reload() }

    fun stopLoading() = apply { getUrlLoader()?.stopLoading() }

    fun postUrl(url: String?, params: ByteArray?) = apply { getUrlLoader()?.postUrl(url, params) }

    fun loadData(
        data: String?,
        mimeType: String?,
        encoding: String?
    ) = apply { getUrlLoader()?.loadData(data, mimeType, encoding) }

    fun loadDataWithBaseURL(
        baseUrl: String?, data: String?,
        mimeType: String?, encoding: String?, historyUrl: String?
    ) = apply { getUrlLoader()?.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl) }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun loadJs(
        method: String?,
        callback: WebValueCallback<String?>? = null,
        vararg params: Any?
    ) = apply { getJsLoader()?.loadJs(method, callback, params) }

    fun loadJs(method: String?, vararg params: Any?) =
        apply { getJsLoader()?.loadJs(method, params) }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun loadJs(method: String?, callback: WebValueCallback<String?>? = null) =
        apply { getJsLoader()?.loadJs(method, callback) }

    fun addJsInterface(obj: BaseJavascriptInterface, name: String) = apply {
        if (getJsInterface() == null) {
            throw IllegalArgumentException("addJsInterface 方法要在 ready 方法后调用")
        }
        getJsInterface()?.addJsInterface(obj, name)
    }

    fun addJsInterfaces(maps: Map<String, BaseJavascriptInterface>) = apply {
        if (getJsInterface() == null) {
            throw IllegalArgumentException("addJsInterfaces 方法要在 ready 方法后调用")
        }
        getJsInterface()?.addJsInterfaces(maps)
    }

    fun goBack() = getProxyWebView()?.goWebBack()
}
