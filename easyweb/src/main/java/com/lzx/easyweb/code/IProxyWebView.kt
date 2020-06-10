package com.lzx.easyweb.code

import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient
import com.lzx.easyweb.cache.WebCacheOpenApi
import com.lzx.easyweb.ui.WebViewUIManager

interface IProxyWebView : WebCacheOpenApi {
    fun removeRiskJavascriptInterface()
    fun removeJavascriptInterfaceByName(name: String?)
    fun addWebJavascriptInterface(obj: Any?, name: String?)

    fun getWebView(): View?
    fun getWebSetting(): WebSettings

    fun getWebUrl(): String?
    fun loadWebUrl(url: String?, headers: Map<String?, String?>? = null)
    fun reloadUrl()
    fun stopLoadingUrl()
    fun posWebUrl(url: String?, params: ByteArray?);
    fun loadWebData(data: String?, mimeType: String?, encoding: String?)
    fun loadWebDataWithBaseURL(
        baseUrl: String?,
        data: String?,
        mimeType: String?,
        encoding: String?,
        historyUrl: String?
    )

    fun loadJs(js: String?)
    fun loadJs(js: String?, callback: WebValueCallback<String?>?)

    fun onWebResume()
    fun onWebPause()
    fun onWebDestroy()

    fun clearWeb()
    fun clearHistory()

    fun setWebViewClient(webViewClient: WebViewClient?)
    fun setWebChromeClient(webChromeClient: WebChromeClient?)
    fun setOnWebViewLongClick(onWebViewLongClick: OnWebViewLongClick?)

    fun setWebUiManager(webViewUIManager: WebViewUIManager?)
    fun setDebug(debug: Boolean)
}

interface OnWebViewLongClick {
    fun onClick(type: Int, hitTestResult: Any?): Boolean
    fun onClickWebImage(imgUrl: String?): Boolean
}

interface WebValueCallback<T> {
    fun onReceiveValue(value: T)
}