package com.lzx.easyweb.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import com.lzx.easyweb.R
import com.lzx.easyweb.code.AndroidWebView
import com.lzx.easyweb.code.IProxyWebView

class WebViewUIManager internal constructor(
    builder: Builder
) {
    private val proxyWebView: IProxyWebView? = builder.proxyWebView
    private var webView: View? = proxyWebView?.getWebView()
    private var viewGroup: ViewGroup? = builder.viewGroup
    private var layoutParams: ViewGroup.LayoutParams? = builder.layoutParams
    private val index: Int = builder.index
    private var errorView: View? = builder.errorView

    @LayoutRes
    var errorLayout = builder.errorLayout
    var errorUrl = builder.errorUrl

    @IdRes
    var reloadViewId = builder.reloadViewId

    private val activity = builder.activity

    private var frameLayout: FrameLayout? = null

    init {
        if (proxyWebView != null) {
            webView = proxyWebView.getWebView()
        }
        if (activity == null) {
            throw NullPointerException("mActivity most not to be null!")
        }
        var mViewGroup: ViewGroup? = this.viewGroup
        if (mViewGroup == null) {
            frameLayout = createLayout()
            mViewGroup = frameLayout
            scanForActivity(activity)?.setContentView(mViewGroup)
        } else {
            if (index != -1) {
                mViewGroup.addView(
                    createLayout().also { this.frameLayout = it },
                    index,
                    layoutParams
                )
            } else {
                mViewGroup.addView(createLayout().also { this.frameLayout = it }, layoutParams)
            }
        }
    }

    private fun scanForActivity(context: Context?): Activity? {
        if (context == null) {
            return null
        }
        if (context is Activity) {
            return context
        } else if (context is ContextWrapper) {
            return scanForActivity(context.baseContext)
        }
        return null
    }

    private fun createLayout(): FrameLayout? {
        val layout = activity?.let { WebViewParentLayout(it) }
        layout?.setBackgroundColor(Color.WHITE)
        layout?.id = R.id.webview_parent_layout
        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        layout?.addView(lookupWebView(), layoutParams)
        //
        layout?.bindWebView(proxyWebView)
        //错误显示页面
        layout?.setErrorView(errorView)
        layout?.setErrorUrl(errorUrl)
        layout?.setErrorLayoutRes(errorLayout, reloadViewId)
        layout?.createErrorView()
        layout?.hideErrorPage()
        return layout
    }

    fun getFrameLayout(): FrameLayout? = frameLayout

    private fun lookupWebView(): View? = if (webView != null) {
        val parentViewGroup = webView!!.parent as ViewGroup?
        parentViewGroup?.removeView(webView)
        webView
    } else {
        activity?.let { AndroidWebView(it) }
    }

    class Builder {
        internal var proxyWebView: IProxyWebView? = null
        internal var viewGroup: ViewGroup? = null
        internal var layoutParams: ViewGroup.LayoutParams? = null
        internal var index: Int = 0
        internal var errorView: View? = null
        internal var errorUrl: String? = null
        @LayoutRes
        internal var errorLayout = -1

        @IdRes
        internal var reloadViewId = -1

        internal var activity: Activity? = null

        fun setActivity(activity: Activity?) = apply {
            this.activity = activity
        }

        fun setProxyWebView(proxyWebView: IProxyWebView?) = apply {
            this.proxyWebView = proxyWebView
        }

        fun setViewGroup(viewGroup: ViewGroup?) = apply {
            this.viewGroup = viewGroup
        }

        fun setLayoutParams(layoutParams: ViewGroup.LayoutParams?) = apply {
            this.layoutParams = layoutParams
        }

        fun setIndex(index: Int) = apply {
            this.index = index
        }

        fun setErrorView(errorView: View?) = apply {
            this.errorView = errorView
        }

        fun setErrorUrl(errorUrl: String?) = apply {
            this.errorUrl = errorUrl
        }

        fun setErrorLayout(errorLayout: Int) = apply {
            this.errorLayout = errorLayout
        }

        fun setReloadViewId(reloadViewId: Int) = apply {
            this.reloadViewId = reloadViewId
        }

        fun build(): WebViewUIManager {
            return WebViewUIManager(this)
        }
    }
}