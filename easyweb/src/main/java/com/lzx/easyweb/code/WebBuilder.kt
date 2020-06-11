package com.lzx.easyweb.code

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import com.lzx.easyweb.EasyWeb
import com.lzx.easyweb.cache.WebCacheMode
import com.lzx.easyweb.cache.interceptor.ResourceInterceptor
import com.lzx.easyweb.js.IJsInterface
import com.lzx.easyweb.js.JsInterfaceImpl
import com.lzx.easyweb.js.WebTimesJsInterface
import com.lzx.easyweb.ui.WebViewUIManager
import java.lang.ref.WeakReference

class WebBuilder(activity: Activity) {
    //上下文
    internal var activityWeak: WeakReference<Activity>? = WeakReference<Activity>(activity)

    //WebView
    internal var proxyWebView: IProxyWebView? = null
    internal var view: View? = null

    //WebView的父layout
    internal var viewGroup: ViewGroup? = null
    internal var layoutParams: ViewGroup.LayoutParams? = null
    private var index: Int = 0

    //WebView配置
    internal var webViewClient: WebViewClient? = null
    internal var webChromeClient: WebChromeClient? = null
    internal var onWebViewLongClick: OnWebViewLongClick? = null
    internal var webViewSetting: IWebViewSetting? = null
    internal var jsInterface: IJsInterface? = null

    //加载器
    internal var urlLoader: IUrlLoader? = null
    internal var jsLoader: IJsLoader? = null

    //是否debug模式
    internal var isDebug = false

    //cache
    private var cacheMode: WebCacheMode? = null
    private val interceptors = mutableListOf<ResourceInterceptor?>()

    //UI相关
    internal var uiManager: WebViewUIManager? = null
    private var errorView: View? = null

    @LayoutRes
    internal var errorLayout = -1

    private var errorUrl = ""

    @IdRes
    internal var reloadViewId = -1

    fun setWebParent(
        v: ViewGroup,
        lp: ViewGroup.LayoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ),
        index: Int = 0
    ) = apply {
        viewGroup = v
        layoutParams = lp
        this.index = index
    }

    fun addErrorView(@LayoutRes errorLayoutId: Int, @IdRes reloadViewId: Int = -1) = apply {
        this.errorLayout = errorLayoutId
        this.reloadViewId = reloadViewId
    }

    fun addErrorView(errorView: View, @IdRes reloadViewId: Int = -1) = apply {
        this.errorView = errorView
        this.reloadViewId = reloadViewId
    }

    fun addErrorView(errorUrl: String) = apply {
        this.errorUrl = errorUrl
    }

    fun setWebViewClient(webViewClient: WebViewClient?) = apply {
        this.webViewClient = webViewClient
    }

    fun setWebChromeClient(webChromeClient: WebChromeClient?) =
        apply { this.webChromeClient = webChromeClient }

    fun setWebViewSetting(webViewSetting: IWebViewSetting?) = apply {
        this.webViewSetting = webViewSetting
    }

    fun setJsInterface(jsInterface: IJsInterface) = apply {
        this.jsInterface = jsInterface
    }

    fun setUrlLoader(urlLoader: IUrlLoader) = apply {
        this.urlLoader = urlLoader
    }

    fun setJsLoader(jsLoader: IJsLoader) = apply {
        this.jsLoader = jsLoader
    }

    fun setOnWebViewLongClick(onWebViewLongClick: OnWebViewLongClick?) = apply {
        this.onWebViewLongClick = onWebViewLongClick
    }

    fun setWebView(webView: IProxyWebView) = apply {
        this.proxyWebView = webView
        this.view = proxyWebView?.getWebView()
    }

    fun setWebCacheMode(cacheMode: WebCacheMode) = apply {
        this.cacheMode = cacheMode
    }

    fun addResourceInterceptor(interceptor: ResourceInterceptor?) = apply {
        this.interceptors.add(interceptor)
    }

    fun debug(isDebug: Boolean) = apply {
        this.isDebug = isDebug
    }

    fun ready(): EasyWeb {
        try {
            initWebView()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return EasyWeb(this)
    }

    private fun initWebView() {
        val activity =
            this.activityWeak?.get() ?: throw NullPointerException("context is null")
        //WebView
        if (proxyWebView == null) {
            proxyWebView = AndroidWebView(activity)
        }

        //WebViewUIManager
        uiManager = WebViewUIManager.Builder()
            .setActivity(activity)
            .setProxyWebView(proxyWebView)
            .setViewGroup(viewGroup)
            .setLayoutParams(layoutParams)
            .setIndex(index)
            .setErrorLayout(errorLayout)
            .setErrorView(errorView)
            .setErrorUrl(errorUrl)
            .setReloadViewId(reloadViewId)
            .build()

        proxyWebView?.setDebug(isDebug)
        proxyWebView?.setWebUiManager(uiManager)

        //WebViewClient
        if (webViewClient != null) {
            proxyWebView?.setWebViewClient(webViewClient)
        }
        //WebChromeClient
        if (webChromeClient != null) {
            proxyWebView?.setWebChromeClient(webChromeClient)
        }
        //点击事件
        if (onWebViewLongClick != null) {
            proxyWebView?.setOnWebViewLongClick(onWebViewLongClick)
        }
        //缓存模式
        if (cacheMode == null) {
            cacheMode = WebCacheMode.NOCACHE
        }
        proxyWebView?.setCacheMode(cacheMode)
        if (interceptors.isNotEmpty()) {
            interceptors.forEach {
                proxyWebView?.addResourceInterceptor(it)
            }
        }

        //WebSettings
        if (webViewSetting == null) {
            webViewSetting = DefaultWebSettings(activity, cacheMode, isDebug)
        }
        webViewSetting?.setWebSetting(proxyWebView)
        view = proxyWebView?.getWebView() as WebView?

        proxyWebView?.removeRiskJavascriptInterface()

        if (urlLoader == null) {
            urlLoader = UrlLoader(proxyWebView)
        }
        if (jsLoader == null) {
            jsLoader = JsLoader(proxyWebView)
        }

        if (jsInterface == null) {
            jsInterface = JsInterfaceImpl(activity, proxyWebView)
        }
        if (isDebug) {
            jsInterface?.addJsInterface(WebTimesJsInterface(), "android")
        }
    }
}