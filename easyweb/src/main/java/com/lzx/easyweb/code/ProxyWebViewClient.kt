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
import com.lzx.easyweb.cache.CacheConfig
import com.lzx.easyweb.cache.WebCacheMode
import com.lzx.easyweb.cache.WebViewCacheManager
import com.lzx.easyweb.cache.interceptor.ResourceInterceptor
import com.lzx.easyweb.js.WebTimesJsInterface
import com.lzx.easyweb.ui.WebViewParentLayout
import com.lzx.easyweb.ui.WebViewUIManager
import com.lzx.easyweb.utils.CacheUtils
import java.util.*


class ProxyWebViewClient(
    private val webView: IProxyWebView,
    private val uiManager: WebViewUIManager?,
    private val isDebug: Boolean
) : WebViewClient() {

    private var mDelegate: WebViewClient? = null
    private var first = true
    private val errorUrls = mutableListOf<String>()
    private val waitLoadUrls = mutableListOf<String>()
    private var cacheMode: WebCacheMode? = null
    private var webViewCacheManager: WebViewCacheManager? = null
    private var userAgentString = webView.getWebSetting().userAgentString
    private var requestUrl = webView.getWebUrl()
    private var touchByUser = false
    private var isLoadFinish = false //页面是否加载完毕
    private val urlStack = Stack<String>() //URL栈
    private var isLoading = false
    private var urlBeforeRedirect: String? = null //记录重定向前的链接

    fun setUpProxyClient(webViewClient: WebViewClient?) {
        mDelegate = webViewClient
    }

    fun setRequestUrl(url: String?) {
        this.requestUrl = url
    }

    fun resetAllStateInternal(url: String?) {
        if (!url.isNullOrEmpty() && url.startsWith("javascript:")) {
            return
        }
        resetAllState()
    }

    // 加载url时重置touch状态
    private fun resetAllState() {
        touchByUser = false
    }

    fun setUpTouchByUser() {
        touchByUser = true
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
        isLoadFinish = false
        if (isLoading && urlStack.size > 0) {
            //从url栈中取出栈顶的链接
            urlBeforeRedirect = urlStack.pop()
        }
        recordUrl(url)
        isLoading = true

        mDelegate?.onPageStarted(view, url, favicon) ?: return
        super.onPageStarted(view, url, favicon)
    }

    /**
     * 记录非重定向链接.
     * 并且控制相同链接链接不入栈
     */
    private fun recordUrl(url: String?) {
        //判断当前url，是否和栈中栈顶部的url是否相同。如果不相同，则入栈操作
        if (!url.isNullOrEmpty() && url != getUrl()) {
            //如果重定向之前的链接不为空
            if (!urlBeforeRedirect.isNullOrEmpty()) {
                urlStack.push(urlBeforeRedirect)
                urlBeforeRedirect = null
            }
        }
    }

    /**
     * 获取最后停留页面的链接
     */
    private fun getUrl(): String = if (urlStack.size > 0) urlStack.peek() else ""

    /**
     * 出栈操作
     */
    private fun popUrl(): String = if (urlStack.size > 0) urlStack.pop() else ""

    /**
     * 是否可以回退操作
     */
    fun pageCanGoBack() = urlStack.size >= 2

    fun pageGoBack(): Boolean {
        //判断是否可以回退操作
        if (pageCanGoBack()) {
            //获取最后停留的页面url
            val url = popBackUrl()
            //如果不为空
            if (!url.isNullOrEmpty()) {
                webView.loadWebUrl(url)
                return true
            }
        }
        return false
    }

    private fun popBackUrl(): String? {
        if (urlStack.size >= 2) {
            popUrl()
            return popUrl()
        }
        return null
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
        isLoading = false
        isLoadFinish = true
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
        val handleByChild = mDelegate?.shouldOverrideUrlLoading(view, request) ?: false
        return if (handleByChild) {
            true
        } else if (!touchByUser) {
            super.shouldOverrideUrlLoading(view, request)
        } else {
            webView.loadWebUrl(request?.url.toString())
            true
        }
    }

    //解决正常情况下的回退栈问题。
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        val handleByChild = mDelegate?.shouldOverrideUrlLoading(view, url) ?: false
        return if (handleByChild) {
            true // 开放client接口给上层业务调用，如果返回true，表示业务已处理。
        } else if (!touchByUser) {
            // 如果业务没有处理，并且在加载过程中用户没有再次触摸屏幕，认为是301/302事件，直接交由系统处理。
            super.shouldOverrideUrlLoading(view, url);
        } else {
            //否则，属于二次加载某个链接的情况，为了解决拼接参数丢失问题，重新调用loadUrl方法添加固有参数。
            webView.loadWebUrl(url)
            true
        }
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
        if (cacheMode == WebCacheMode.CACHE_RES) {
            return loadFromWebViewCache(request)
        }
        return super.shouldInterceptRequest(view, request)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun loadFromWebViewCache(request: WebResourceRequest?): WebResourceResponse? {
        val url = request?.url.toString()
        val scheme = request?.url?.scheme?.trim()
        val method = request?.method?.trim()?.toLowerCase(Locale.getDefault())
        val extension = CacheUtils.getFileExtensionFromUrl(url) ?: return null
        if (scheme.isNullOrEmpty() || !scheme.startsWith("http") || method != "get") {
            return null
        }
        if (CacheConfig.instance.isIgnoreUrl(url)) {
            return null
        }
        if (!CacheConfig.instance.isCacheFile(extension)) {
            return null
        }
        return webViewCacheManager?.requestResource(
            request,
            cacheMode,
            userAgentString,
            requestUrl
        )
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

    fun destroy() {
        webViewCacheManager?.destroy()
        errorUrls.clear()
        waitLoadUrls.clear()
        urlStack.clear()
    }
}