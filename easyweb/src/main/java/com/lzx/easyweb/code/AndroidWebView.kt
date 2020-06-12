package com.lzx.easyweb.code

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import com.lzx.easyweb.cache.WebCacheMode
import com.lzx.easyweb.cache.interceptor.ResourceInterceptor
import com.lzx.easyweb.ui.WebViewUIManager
import com.lzx.easyweb.utils.MainLooper
import java.lang.reflect.Field


class AndroidWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : WebView(context, attrs, defStyleAttr),
    IProxyWebView,
    View.OnLongClickListener {

    init {
        initWebView()
    }

    private var onWebViewLongClick: OnWebViewLongClick? = null
    private var result: HitTestResult? = null
    private var proxyWebViewClient: ProxyWebViewClient? = null
    private var proxyWebChromeClient: ProxyWebChromeClient? = null
    private var userWebViewClient: WebViewClient? = null
    private var userWebChromeClient: WebChromeClient? = null
    private var webViewUIManager: WebViewUIManager? = null
    private var isDebug = false
    private var recycled = false

    private fun initWebView() {
        setOnLongClickListener(this)
    }

    override fun onLongClick(v: View?): Boolean {
        result = this.hitTestResult
        if (null == result) return false
        val type: Int = result?.type ?: HitTestResult.UNKNOWN_TYPE
        return if (type == HitTestResult.IMAGE_TYPE || type == HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
            val saveImgUrl = result?.extra
            onWebViewLongClick?.onClickWebImage(saveImgUrl) ?: false
        } else {
            onWebViewLongClick?.onClick(type, result) ?: false
        }
    }

    override fun removeRiskJavascriptInterface() {
        //显式移除有风险的 WebView 系统隐藏接口
        removeJavascriptInterface("searchBoxJavaBridge_")
        removeJavascriptInterface("accessibility")
        removeJavascriptInterface("accessibilityTraversal")
    }

    override fun removeJavascriptInterfaceByName(name: String?) {
        name?.let { this.removeJavascriptInterface(it) }
    }

    @SuppressLint("JavascriptInterface", "AddJavascriptInterface")
    override fun addWebJavascriptInterface(obj: Any?, name: String?) {
        addJavascriptInterface(obj, name)
    }

    override fun getWebView(): View? = this

    override fun getWebSetting(): WebSettings = this.settings

    override fun getWebUrl(): String? = this.url

    override fun loadWebUrl(url: String?, headers: Map<String?, String?>?) {
        if (headers.isNullOrEmpty()) {
            this.loadUrl(url)
        } else {
            this.loadUrl(url, headers)
        }
        proxyWebViewClient?.setRequestUrl(url)
    }

    override fun reloadUrl() {
        this.reload()
    }

    override fun stopLoadingUrl() {
        this.stopLoadingUrl()
    }

    override fun posWebUrl(url: String?, params: ByteArray?) {
        this.posWebUrl(url, params)
    }

    override fun loadWebData(data: String?, mimeType: String?, encoding: String?) {
        this.loadData(data, mimeType, encoding)
    }

    override fun loadWebDataWithBaseURL(
        baseUrl: String?,
        data: String?,
        mimeType: String?,
        encoding: String?,
        historyUrl: String?
    ) {
        this.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl)
    }

    override fun loadJs(js: String?) = this.loadUrl(js)

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun loadJs(js: String?, callback: WebValueCallback<String?>?) {
        this.evaluateJavascript(js) { value -> callback?.onReceiveValue(value) }
    }

    override fun onWebResume() {
        if (Build.VERSION.SDK_INT >= 11) {
            onResume()
        }
        resumeTimers()
    }

    override fun onWebPause() {
        if (Build.VERSION.SDK_INT >= 11) {
            onPause()
        }
        pauseTimers()
    }

    override fun onWebDestroy() {
        resumeTimers()
        if (MainLooper.instance.isInMainThread()) {
            return
        }
        stopLoading()
        if (this.handler != null) {
            this.handler.removeCallbacksAndMessages(null)
        }
        removeAllViews()
        if (this.parent != null && this.parent is ViewGroup) {
            val viewGroup = parent as ViewGroup
            viewGroup.removeAllViews()
        }
        this.onWebViewLongClick = null
        this.webChromeClient = null
        this.webViewClient = null
        this.tag = null
        clearHistory()
        proxyWebViewClient?.destroy()
        destroy()
    }

    override fun destroy() {
        releaseConfigCallback()
        clearWeb()
        super.destroy()
    }

    /**
     * 解决WebView内存泄漏问题
     */
    private fun releaseConfigCallback() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) { // JELLY_BEAN
            try {
                var field: Field = this::class.java.getDeclaredField("mWebViewCore")
                field = field.type.getDeclaredField("mBrowserFrame")
                field = field.type.getDeclaredField("sConfigCallback")
                field.isAccessible = true
                field.set(null, null)
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) { // KITKAT
            try {
                @SuppressLint("PrivateApi")
                val sConfigCallback: Field? = Class.forName("android.webkit.BrowserFrame")
                    .getDeclaredField("sConfigCallback")
                if (sConfigCallback != null) {
                    sConfigCallback.isAccessible = true
                    sConfigCallback.set(null, null)
                }
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
    }


    override fun clearWeb() {
        stopLoading()
        this.loadUrl("")
        setRecycled(true)
        this.handler?.removeCallbacksAndMessages(null)
        this.webChromeClient = null
        this.webViewClient = null
        this.tag = null
        clearCache(true)
        clearHistory()
    }

    override fun setWebViewClient(webViewClient: WebViewClient?) {
        if (proxyWebViewClient != null) {
            proxyWebViewClient?.setUpProxyClient(webViewClient)
        } else {
            super.setWebViewClient(webViewClient)
        }
        userWebViewClient = webViewClient
    }

    override fun setWebChromeClient(webChromeClient: WebChromeClient?) {
        if (proxyWebChromeClient != null) {
            proxyWebChromeClient?.setUpProxyChromeClient(webChromeClient)
        } else {
            super.setWebChromeClient(webChromeClient)
        }
        userWebChromeClient = webChromeClient
    }

    override fun setOnWebViewLongClick(onWebViewLongClick: OnWebViewLongClick?) {
        this.onWebViewLongClick = onWebViewLongClick
    }

    override fun setWebUiManager(webViewUIManager: WebViewUIManager?) {
        this.webViewUIManager = webViewUIManager
    }

    override fun setDebug(debug: Boolean) {
        this.isDebug = debug
    }

    override fun setCacheMode(mode: WebCacheMode?) {
        proxyWebViewClient = ProxyWebViewClient(this, webViewUIManager, isDebug)
        if (userWebViewClient != null) {
            proxyWebViewClient?.setUpProxyClient(userWebViewClient)
        }
        proxyWebViewClient?.setCacheMode(mode, context)
        super.setWebViewClient(proxyWebViewClient)

        proxyWebChromeClient = ProxyWebChromeClient(isDebug)
        if (userWebChromeClient != null) {
            proxyWebChromeClient?.setUpProxyChromeClient(userWebChromeClient)
        }
        super.setWebChromeClient(proxyWebChromeClient)
    }

    override fun addResourceInterceptor(interceptor: ResourceInterceptor?) {
        proxyWebViewClient?.addResourceInterceptor(interceptor)
    }

    override fun canGoBack(): Boolean {
        return !recycled && super.canGoBack()
    }

    fun isRecycled(): Boolean {
        return recycled
    }

    fun setRecycled(recycled: Boolean) {
        this.recycled = recycled
    }
}