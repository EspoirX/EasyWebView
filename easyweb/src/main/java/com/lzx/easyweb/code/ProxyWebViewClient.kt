package com.lzx.easyweb.code

import android.content.Context
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.os.Message
import android.os.SystemClock
import android.util.Log
import android.view.KeyEvent
import android.webkit.*
import androidx.annotation.RequiresApi
import com.lzx.easyweb.EasyWeb
import com.lzx.easyweb.cache.WebCacheMode
import com.lzx.easyweb.cache.WebViewCacheManager
import com.lzx.easyweb.cache.interceptor.ResourceInterceptor
import com.lzx.easyweb.js.WebTimesJsInterface
import com.lzx.easyweb.ui.WebViewParentLayout
import com.lzx.easyweb.ui.WebViewUIManager

class ProxyWebViewClient(
    private val webView: WebView,
    private val uiManager: WebViewUIManager?,
    private val isDebug: Boolean
) : WebViewClient() {

    private var mDelegate: WebViewClient? = null
    private var first = true
    private val errorUrls = mutableListOf<String>()
    private val waitLoadUrls = mutableListOf<String>()
    private var cacheMode: WebCacheMode? = null
    private var webViewCacheManager: WebViewCacheManager? = null

    fun setUpProxyClient(webViewClient: WebViewClient?) {
        mDelegate = webViewClient
    }

    override fun onTooManyRedirects(view: WebView?, cancelMsg: Message?, continueMsg: Message?) {
        mDelegate?.onTooManyRedirects(view, cancelMsg, continueMsg) ?: return
        super.onTooManyRedirects(view, cancelMsg, continueMsg)
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onReceivedHttpError(
        view: WebView?,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?
    ) {
        mDelegate?.onReceivedHttpError(view, request, errorResponse) ?: return
        super.onReceivedHttpError(view, request, errorResponse)
    }

    override fun onFormResubmission(view: WebView?, dontResend: Message?, resend: Message?) {
        mDelegate?.onFormResubmission(view, dontResend, resend) ?: return
        super.onFormResubmission(view, dontResend, resend)
    }

    override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
        mDelegate?.doUpdateVisitedHistory(view, url, isReload) ?: return
        super.doUpdateVisitedHistory(view, url, isReload)
    }

    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        mDelegate?.onReceivedSslError(view, handler, error) ?: return
        super.onReceivedSslError(view, handler, error)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onReceivedClientCertRequest(view: WebView?, request: ClientCertRequest?) {
        mDelegate?.onReceivedClientCertRequest(view, request) ?: return
        super.onReceivedClientCertRequest(view, request)
    }

    override fun onReceivedHttpAuthRequest(
        view: WebView?,
        handler: HttpAuthHandler?,
        host: String?,
        realm: String?
    ) {
        mDelegate?.onReceivedHttpAuthRequest(view, handler, host, realm) ?: return
        super.onReceivedHttpAuthRequest(view, handler, host, realm)
    }

    override fun shouldOverrideKeyEvent(view: WebView?, event: KeyEvent?): Boolean {
        return mDelegate?.shouldOverrideKeyEvent(view, event)
            ?: super.shouldOverrideKeyEvent(view, event)
    }

    override fun onUnhandledKeyEvent(view: WebView?, event: KeyEvent?) {
        mDelegate?.onUnhandledKeyEvent(view, event) ?: return
        super.onUnhandledKeyEvent(view, event)
    }

    override fun onScaleChanged(view: WebView?, oldScale: Float, newScale: Float) {
        mDelegate?.onScaleChanged(view, oldScale, newScale) ?: return
        super.onScaleChanged(view, oldScale, newScale)
    }

    override fun onReceivedLoginRequest(
        view: WebView?,
        realm: String?,
        account: String?,
        args: String?
    ) {
        mDelegate?.onReceivedLoginRequest(view, realm, account, args) ?: return
        super.onReceivedLoginRequest(view, realm, account, args)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRenderProcessGone(view: WebView?, detail: RenderProcessGoneDetail?): Boolean {
        return mDelegate?.onRenderProcessGone(view, detail)
            ?: super.onRenderProcessGone(view, detail)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        showErrorLayout(request?.url.toString())
        mDelegate?.onReceivedError(view, request, error) ?: return
        super.onReceivedError(view, request, error)
    }

    override fun onReceivedError(
        view: WebView?,
        errorCode: Int,
        description: String?,
        failingUrl: String?
    ) {
        showErrorLayout(failingUrl)
        mDelegate?.onReceivedError(view, errorCode, description, failingUrl) ?: return
        super.onReceivedError(view, errorCode, description, failingUrl)
    }

    private fun showErrorLayout(failingUrl: String?) {
        failingUrl?.let { errorUrls.add(it) }
        val layout = uiManager?.getFrameLayout()
        if (layout is WebViewParentLayout) {
            layout.showErrorPage()
        }
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        if (!waitLoadUrls.contains(url)) {
            url?.let { waitLoadUrls.add(it) }
        }
        mDelegate?.onPageStarted(view, url, favicon) ?: return
        super.onPageStarted(view, url, favicon)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        if (!errorUrls.contains(url) && waitLoadUrls.contains(url)) {
            val layout = uiManager?.getFrameLayout()
            if (layout is WebViewParentLayout) {
                layout.hideErrorPage()
            }
        }
        if (waitLoadUrls.contains(url)) {
            waitLoadUrls.remove(url)
        }
        if (errorUrls.isNotEmpty()) {
            errorUrls.clear()
        }
        if (webView is AndroidWebView && webView.isRecycled() && url != "about:blank") {
            webView.setRecycled(false)
            webView.clearHistory()
        }
        mDelegate?.onPageFinished(view, url) ?: return
        super.onPageFinished(view, url)
    }

    override fun onLoadResource(view: WebView?, url: String?) {
        mDelegate?.onLoadResource(view, url) ?: return
        super.onLoadResource(view, url)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onPageCommitVisible(view: WebView?, url: String?) {
        mDelegate?.onPageCommitVisible(view, url) ?: return
        super.onPageCommitVisible(view, url)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        if (mDelegate != null) {
            return mDelegate!!.shouldOverrideUrlLoading(view, request)
        }
        view?.loadUrl(request?.url.toString())
        return true
    }

    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        if (mDelegate != null) {
            return mDelegate!!.shouldOverrideUrlLoading(view, url)
        }
        view?.loadUrl(url)
        return true
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        if (isDebug) {
            if (first) {
                Log.i(
                    WebTimesJsInterface.TAG,
                    "实例化 time: " + (SystemClock.uptimeMillis() - EasyWeb.initStartTime)
                )
                first = false
            }
        }
        if (mDelegate != null) {
            val response = mDelegate!!.shouldInterceptRequest(view, request)
            return response ?: super.shouldInterceptRequest(view, request)
        }
        if (cacheMode != WebCacheMode.NOCACHE && cacheMode != WebCacheMode.DEFAULT) {
            return loadFromWebViewCache(request)
        }
        return super.shouldInterceptRequest(view, request)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun loadFromWebViewCache(request: WebResourceRequest?): WebResourceResponse? {
        val scheme = request?.url?.scheme?.trim()
        val method = request?.method?.trim()?.toLowerCase()
        if (scheme?.startsWith("http") == true && method == "get") {
            return webViewCacheManager?.requestResource(
                request,
                cacheMode,
                webView.settings.userAgentString,
                webView.url
            )
        }
        return null
    }

    override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
        return mDelegate?.shouldInterceptRequest(view, url)
            ?: super.shouldInterceptRequest(view, url)
    }

    fun setCacheMode(mode: WebCacheMode?, context: Context) {
        this.cacheMode = mode
        webViewCacheManager = WebViewCacheManager(context)
    }

    fun addResourceInterceptor(interceptor: ResourceInterceptor?) {
        webViewCacheManager?.addResourceInterceptor(interceptor)
    }
}