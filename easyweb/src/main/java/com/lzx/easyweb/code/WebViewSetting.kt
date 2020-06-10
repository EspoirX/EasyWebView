package com.lzx.easyweb.code

import android.webkit.WebSettings


interface IWebViewSetting {
    fun getWebSetting(): WebSettings?

    fun setWebSetting(webView: IProxyWebView?): IWebViewSetting?
}

abstract class BaseWebViewSetting : IWebViewSetting {

    private var settings: WebSettings? = null

    override fun getWebSetting(): WebSettings? = settings

    override fun setWebSetting(webView: IProxyWebView?): IWebViewSetting? {
        try {
            settings = webView?.getWebSetting()
            setUpSetting(settings)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return this
    }

    protected abstract fun setUpSetting(webSettings: WebSettings?)
}