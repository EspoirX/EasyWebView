package com.lzx.easyweb.code

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Message
import android.view.View
import android.webkit.*
import android.webkit.WebStorage.QuotaUpdater
import androidx.annotation.RequiresApi
import com.lzx.easyweb.ui.WebViewUIManager

class ProxyWebChromeClient constructor(
    private val uiManager: WebViewUIManager?
) : WebChromeClient() {

    private var mDelegate: WebChromeClient? = null

    fun setUpProxyChromeClient(webChromeClient: WebChromeClient?) {
        mDelegate = webChromeClient
    }

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        uiManager?.updateProgress(newProgress)
        mDelegate?.onProgressChanged(view, newProgress) ?: return
        super.onProgressChanged(view, newProgress)
    }

    override fun onReceivedTitle(view: WebView?, title: String?) {
        mDelegate?.onReceivedTitle(view, title) ?: return
        super.onReceivedTitle(view, title)
    }

    override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
        mDelegate?.onReceivedIcon(view, icon) ?: return
        super.onReceivedIcon(view, icon)
    }

    override fun onReceivedTouchIconUrl(
        view: WebView?, url: String?,
        precomposed: Boolean
    ) {
        mDelegate?.onReceivedTouchIconUrl(view, url, precomposed) ?: return
        super.onReceivedTouchIconUrl(view, url, precomposed)
    }

    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
        mDelegate?.onShowCustomView(view, callback) ?: return
        super.onShowCustomView(view, callback)
    }

    override fun onShowCustomView(
        view: View?,
        requestedOrientation: Int,
        callback: CustomViewCallback?
    ) {
        mDelegate?.onShowCustomView(view, requestedOrientation, callback) ?: return
        super.onShowCustomView(view, requestedOrientation, callback)
    }

    override fun onHideCustomView() {
        mDelegate?.onHideCustomView() ?: return
        super.onHideCustomView()
    }

    override fun onCreateWindow(
        view: WebView?, isDialog: Boolean,
        isUserGesture: Boolean, resultMsg: Message?
    ): Boolean {
        return mDelegate?.onCreateWindow(view, isDialog, isUserGesture, resultMsg) ?: false
    }


    override fun onRequestFocus(view: WebView?) {
        mDelegate?.onRequestFocus(view) ?: return
        super.onRequestFocus(view)
    }

    override fun onCloseWindow(window: WebView?) {
        mDelegate?.onCloseWindow(window) ?: return
        super.onCloseWindow(window)
    }

    override fun onJsAlert(
        view: WebView?,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        return mDelegate?.onJsAlert(view, url, message, result) ?: super.onJsAlert(
            view,
            url,
            message,
            result
        )
    }

    override fun onJsConfirm(
        view: WebView?,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        return mDelegate?.onJsConfirm(view, url, message, result) ?: super.onJsConfirm(
            view,
            url,
            message,
            result
        )
    }

    override fun onJsPrompt(
        view: WebView?,
        url: String?,
        message: String?,
        defaultValue: String?,
        result: JsPromptResult?
    ): Boolean {
        return mDelegate?.onJsPrompt(view, url, message, defaultValue, result) ?: super.onJsPrompt(
            view,
            url,
            message,
            defaultValue,
            result
        )
    }

    override fun onJsBeforeUnload(
        view: WebView?,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        return mDelegate?.onJsBeforeUnload(view, url, message, result) ?: super.onJsBeforeUnload(
            view,
            url,
            message,
            result
        )
    }

    override fun onExceededDatabaseQuota(
        url: String?,
        databaseIdentifier: String?,
        quota: Long,
        estimatedDatabaseSize: Long,
        totalQuota: Long,
        quotaUpdater: QuotaUpdater?
    ) {
        mDelegate?.onExceededDatabaseQuota(
            url,
            databaseIdentifier,
            quota,
            estimatedDatabaseSize,
            totalQuota,
            quotaUpdater
        ) ?: return
        super.onExceededDatabaseQuota(
            url,
            databaseIdentifier,
            quota,
            estimatedDatabaseSize,
            totalQuota,
            quotaUpdater
        )
    }

    override fun onReachedMaxAppCacheSize(
        requiredStorage: Long,
        quota: Long,
        quotaUpdater: QuotaUpdater?
    ) {
        mDelegate?.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater) ?: return
        super.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater)
    }

    override fun onGeolocationPermissionsShowPrompt(
        origin: String?,
        callback: GeolocationPermissions.Callback?
    ) {
        mDelegate?.onGeolocationPermissionsShowPrompt(origin, callback) ?: return
        super.onGeolocationPermissionsShowPrompt(origin, callback)
    }

    override fun onGeolocationPermissionsHidePrompt() {
        mDelegate?.onGeolocationPermissionsHidePrompt() ?: return
        super.onGeolocationPermissionsHidePrompt()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onPermissionRequest(request: PermissionRequest?) {
        mDelegate?.onPermissionRequest(request) ?: return
        super.onPermissionRequest(request)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onPermissionRequestCanceled(request: PermissionRequest?) {
        mDelegate?.onPermissionRequestCanceled(request) ?: return
        super.onPermissionRequestCanceled(request)
    }

    override fun onJsTimeout(): Boolean {
        return mDelegate?.onJsTimeout() ?: super.onJsTimeout()
    }

    override fun onConsoleMessage(message: String?, lineNumber: Int, sourceID: String?) {
        mDelegate?.onConsoleMessage(message, lineNumber, sourceID) ?: return
        super.onConsoleMessage(message, lineNumber, sourceID)
    }

    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        return mDelegate?.onConsoleMessage(consoleMessage) ?: super.onConsoleMessage(consoleMessage)
    }

    override fun getDefaultVideoPoster(): Bitmap? {
        return mDelegate?.defaultVideoPoster ?: super.getDefaultVideoPoster()
    }

    override fun getVideoLoadingProgressView(): View? {
        return mDelegate?.videoLoadingProgressView ?: super.getVideoLoadingProgressView()
    }

    override fun getVisitedHistory(callback: ValueCallback<Array<String>>?) {
        mDelegate?.getVisitedHistory(callback) ?: return
        super.getVisitedHistory(callback)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: WebChromeClient.FileChooserParams?
    ): Boolean {
        return mDelegate?.onShowFileChooser(webView, filePathCallback, fileChooserParams)
            ?: super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
    }
}
