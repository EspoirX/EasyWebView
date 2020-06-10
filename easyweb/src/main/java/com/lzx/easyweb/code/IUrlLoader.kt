package com.lzx.easyweb.code

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