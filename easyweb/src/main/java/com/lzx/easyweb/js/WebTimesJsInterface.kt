package com.lzx.easyweb.js

import android.util.Log
import android.webkit.JavascriptInterface
import com.lzx.easyweb.EasyWeb

class WebTimesJsInterface : BaseJavascriptInterface() {
    companion object {
        const val TAG = EasyWeb.TAG + "WebTimes"
    }

    @JavascriptInterface
    fun sendResource(timing: String?) {
        val performance: Performance = Performance.toPerformance(timing)
        Log.i(TAG, "WebView 请求 time: " + performance.getWebRequestTime())
        Log.i(TAG, "DOM 解析 time: " + performance.getWebDomCompleteTime())
        Log.i(TAG, "DOM 加载 time: " + performance.getWebDomLoadedTime())
        Log.i(TAG, "DOM渲染 time: " + performance.getWebDomTotalTime())
    }
}