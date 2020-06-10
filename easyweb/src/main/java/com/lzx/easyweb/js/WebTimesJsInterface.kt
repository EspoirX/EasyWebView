package com.lzx.easyweb.js

import android.util.Log
import android.webkit.JavascriptInterface
import com.lzx.easyweb.js.BaseJavascriptInterface
import com.lzx.easyweb.js.Performance

class WebTimesJsInterface : BaseJavascriptInterface() {
    @JavascriptInterface
    fun sendResource(timing: String?) {
        val performance: Performance = Performance.toPerformance(timing)
        Log.i("WebTimesJsInterface", "WebView 请求 time: " + performance.getWebRequestTime())
        Log.i("WebTimesJsInterface", "DOM 解析 time: " + performance.getWebDomCompleteTime())
        Log.i("WebTimesJsInterface", "DOM 加载 time: " + performance.getWebDomLoadedTime())
        Log.i("WebTimesJsInterface", "DOM渲染 time: " + performance.getWebDomTotalTime())
    }
}