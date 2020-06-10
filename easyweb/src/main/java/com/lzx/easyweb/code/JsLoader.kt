package com.lzx.easyweb.code

import android.os.Build

class JsLoader(private val proxyWebView: IProxyWebView?) : IJsLoader {

    override fun loadJs(
        method: String?,
        callback: WebValueCallback<String?>?,
        vararg params: Any?
    ) {
        val sb = StringBuilder()
        sb.append("javascript:").append(method)
        if (params.isEmpty()) {
            sb.append("()")
        } else {
            sb.append("(").append(splice(*params))
        }
        loadJs(sb.toString(), callback)
    }

    private fun splice(vararg params: Any?): CharSequence? {
        val sb = java.lang.StringBuilder()
        for (param in params) {
            if (param is String) {
                sb.append("'").append(param).append("'")
            } else {
                sb.append(param)
            }
            sb.append(",")
        }
        sb.deleteCharAt(sb.length - 1)
        sb.append(")")
        return sb.toString()
    }

    override fun loadJs(method: String?, callback: WebValueCallback<String?>?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            proxyWebView?.loadJs(method, callback)
        } else {
            proxyWebView?.loadJs(method)
        }
    }

    override fun loadJs(method: String?, vararg params: Any?) {
        loadJs(method, null, params)
    }
}