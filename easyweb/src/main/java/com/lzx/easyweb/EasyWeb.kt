package com.lzx.easyweb

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import com.lzx.easyweb.builder.WebBuilder
import com.lzx.easyweb.code.WebValueCallback

class EasyWeb(private val builder: WebBuilder) {

    companion object {
        fun with(activity: Activity?): WebBuilder {
            if (activity == null) {
                throw NullPointerException("context can not be null")
            }
            return WebBuilder(activity)
        }
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
