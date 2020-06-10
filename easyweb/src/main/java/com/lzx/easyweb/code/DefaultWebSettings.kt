package com.lzx.easyweb.code

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.webkit.WebSettings
import android.webkit.WebView
import com.lzx.easyweb.AppUtils
import com.lzx.easyweb.cache.WebCacheMode

class DefaultWebSettings(
    private val context: Context?,
    private val cacheMode: WebCacheMode?,
    private val isDebug: Boolean
) : BaseWebViewSetting() {


    @SuppressLint("SetJavaScriptEnabled")
    override fun setUpSetting(webSettings: WebSettings?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings?.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        if (isDebug && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
        webSettings?.loadWithOverviewMode = true
        webSettings?.useWideViewPort = true
        //允许js代码
        webSettings?.javaScriptEnabled = true
        //禁用放缩
        webSettings?.displayZoomControls = false
        webSettings?.builtInZoomControls = false
        //禁用文字缩放
        webSettings?.textZoom = 100
        if (cacheMode == WebCacheMode.DEFAULT) {
            //允许SessionStorage/LocalStorage存储
            webSettings?.domStorageEnabled = true
            //10M缓存，api 18后，系统自动管理。
            webSettings?.setAppCacheMaxSize(10 * 1024 * 1024)
            //允许缓存，设置缓存位置
            webSettings?.setAppCacheEnabled(true)
            webSettings?.setAppCachePath(context?.getDir("WebViewAppCache", 0)?.path)
            webSettings?.cacheMode = WebSettings.LOAD_DEFAULT
        } else if (cacheMode == WebCacheMode.NOCACHE) {
            webSettings?.cacheMode = WebSettings.LOAD_NO_CACHE
        }
        //允许WebView使用File协议
        webSettings?.allowFileAccess = true
        //不保存密码
        webSettings?.savePassword = false
        //设置UA
        val ua = webSettings?.userAgentString + " WebView/" + AppUtils.getAppVersionName(
            context
        )
        webSettings?.userAgentString = ua
        //自动加载图片
        webSettings?.loadsImagesAutomatically = true
    }

}