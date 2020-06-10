package com.lzx.easyweb.js

import android.app.Activity
import com.lzx.easyweb.code.IProxyWebView

class JsInterfaceImpl(private val activity: Activity?, private val proxyWebView: IProxyWebView?) :
    IJsInterface {

    private val jsInterfacesCacheMap = hashMapOf<String, BaseJavascriptInterface>()

    override fun addJsInterfaces(maps: Map<String, BaseJavascriptInterface>): IJsInterface {
        val entrySet = maps.entries
        for ((key, value) in entrySet) {
            try {
                value.activity = activity
                value.webView = proxyWebView
                proxyWebView?.addWebJavascriptInterface(value, key)
                jsInterfacesCacheMap[key] = value
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return this
    }

    override fun addJsInterface(obj: BaseJavascriptInterface, name: String): IJsInterface {
        try {
            obj.activity = activity
            obj.webView = proxyWebView
            proxyWebView?.addWebJavascriptInterface(obj, name)
            jsInterfacesCacheMap[name] = obj
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return this
    }

    override fun getJsInterface(name: String): BaseJavascriptInterface? {
        return jsInterfacesCacheMap[name]
    }

    override fun removeJsInterface(name: String): IJsInterface {
        try {
            proxyWebView?.removeJavascriptInterfaceByName(name)
            jsInterfacesCacheMap.remove(name)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return this
    }

    override fun clearJsInterface(): IJsInterface {
        jsInterfacesCacheMap.forEach {
            removeJsInterface(it.key)
        }
        jsInterfacesCacheMap.clear()
        return this
    }

}