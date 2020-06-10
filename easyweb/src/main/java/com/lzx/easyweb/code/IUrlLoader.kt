package com.lzx.easyweb.code

import com.lzx.easyweb.utils.MainLooper

interface IUrlLoader {

    fun loadUrl(
        url: String?,
        headers: Map<String?, String?>?
    )

    fun reload()

    fun stopLoading()

    fun postUrl(url: String?, params: ByteArray?)

    fun loadData(
        data: String?,
        mimeType: String?,
        encoding: String?
    )

    fun loadDataWithBaseURL(
        baseUrl: String?, data: String?,
        mimeType: String?, encoding: String?, historyUrl: String?
    )
}

class UrlLoader(private val proxyWebView: IProxyWebView?) : IUrlLoader {
    override fun loadUrl(url: String?, headers: Map<String?, String?>?) {
        MainLooper.instance.runOnUiThread(Runnable {
            proxyWebView?.loadWebUrl(url, headers)
        })
    }

    override fun reload() {
        MainLooper.instance.runOnUiThread(Runnable {
            proxyWebView?.reloadUrl()
        })
    }

    override fun stopLoading() {
        MainLooper.instance.runOnUiThread(Runnable {
            proxyWebView?.stopLoadingUrl()
        })
    }

    override fun postUrl(url: String?, params: ByteArray?) {
        MainLooper.instance.runOnUiThread(Runnable {
            proxyWebView?.posWebUrl(url, params)
        })
    }

    override fun loadData(data: String?, mimeType: String?, encoding: String?) {
        MainLooper.instance.runOnUiThread(Runnable {
            proxyWebView?.loadWebData(data, mimeType, encoding)
        })
    }

    override fun loadDataWithBaseURL(
        baseUrl: String?,
        data: String?,
        mimeType: String?,
        encoding: String?,
        historyUrl: String?
    ) {
        MainLooper.instance.runOnUiThread(Runnable {
            proxyWebView?.loadWebDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl)
        })
    }
}