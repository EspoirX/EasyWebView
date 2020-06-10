package com.lzx.easyweb.js

import android.app.Activity
import com.lzx.easyweb.code.IProxyWebView

open class BaseJavascriptInterface {
    var activity: Activity? = null
    var webView: IProxyWebView? = null
}