package com.lzx.easywebview

import android.webkit.JavascriptInterface
import android.widget.Toast
import com.lzx.easyweb.js.BaseJavascriptInterface

class JsInterfaceTest : BaseJavascriptInterface() {
    @JavascriptInterface
    fun callAndroidMethod(param: String) {
        Toast.makeText(activity, "我是js里面传过来的参数:$param", Toast.LENGTH_SHORT).show()
    }
}