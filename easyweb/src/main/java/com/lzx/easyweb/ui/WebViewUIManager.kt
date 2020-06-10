package com.lzx.easyweb.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.ColorInt
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
    var needProgressBar: Boolean = builder.needProgressBar
    var isCustomProgressBar: Boolean = builder.isCustomProgressBar
    var progressView: IProgressView? = builder.progressView
    var errorView: View? = builder.errorView
    var loadView: View? = builder.loadView


    @ColorInt
    var progressBarColor = builder.progressBarColor
    var progressBarHeight = builder.progressBarHeight

    @LayoutRes
    var errorLayout = builder.errorLayout

    @IdRes
    var reloadViewId = builder.reloadViewId

    @LayoutRes
    var loadLayout = builder.loadLayout

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
        if (errorView != null) {
            layout?.setErrorView(errorView)
        }
        layout?.setErrorLayoutRes(errorLayout, reloadViewId)
        layout?.createErrorView()
        layout?.hideErrorPage()
        //加载中页面
        if (loadView != null) {
            layout?.setLoadView(loadView)
        }
        layout?.setLoadLayoutRes(loadLayout)
        layout?.createLoadView()
        layout?.hideLoading()

        if (needProgressBar) {
            //自定义
            if (isCustomProgressBar) {
                if (progressView == null) {
                    activity?.let {
                        val defProView = DefaultProgressView(it)
//                        layout?.addView(defProView, defProView.getProLayoutParams())
                        progressView = defProView
                        (defProView as View).visibility = View.GONE
                    }
                } else if (progressView is View || progressView is ViewGroup) {
                    layout?.addView(progressView as View, progressView?.getProLayoutParams())
                    (progressView as View).visibility = View.GONE
                } else {
                    progressView?.hide()
                }
            } else {
                activity?.let {
                    val defProView = DefaultProgressView(it)
                    val proLayoutParams = if (progressBarHeight > 0) {
                        FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            progressBarHeight
                        )
                    } else {
                        defProView.getProLayoutParams()
                    }
//                    if (progressBarColor != -1) {
//                        defProView.setProgressColor(progressBarColor)
//                    }
//                    proLayoutParams?.gravity = Gravity.TOP
                    progressView = defProView
//                    layout?.addView(defProView, proLayoutParams)
                    (defProView as View).visibility = View.GONE
                }
            }
        }
        return layout
    }

    private fun lookupWebView(): View? = if (webView != null) {
        val parentViewGroup = webView!!.parent as ViewGroup?
        parentViewGroup?.removeView(webView)
        webView
    } else {
        activity?.let { AndroidWebView(it) }
    }

    fun updateProgress(progress: Int) {
        when (progress) {
            0 -> {
                progressView?.reset()
            }
            in 1..10 -> {
                progressView?.show()
            }
            in 11..94 -> {
                progressView?.show()
                progressView?.setProgress(progress)
            }
            else -> {
                progressView?.setProgress(progress)
                progressView?.hide()
            }
        }
    }

    class Builder {
        internal var proxyWebView: IProxyWebView? = null
        internal var viewGroup: ViewGroup? = null
        internal var layoutParams: ViewGroup.LayoutParams? = null
        internal var index: Int = 0
        internal var needProgressBar = false
        internal var isCustomProgressBar = false
        internal var errorView: View? = null
        internal var loadView: View? = null
        internal var progressView: IProgressView? = null

        @ColorInt
        internal var progressBarColor = -1
        internal var progressBarHeight = 0

        @LayoutRes
        internal var errorLayout = -1

        @IdRes
        internal var reloadViewId = -1

        @LayoutRes
        internal var loadLayout = -1

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

        fun setNeedProgressBar(needProgressBar: Boolean, isCustomProgressBar: Boolean) = apply {
            this.needProgressBar = needProgressBar
            this.isCustomProgressBar = isCustomProgressBar
        }

        fun setProgressView(progressView: IProgressView?) =
            apply { this.progressView = progressView }

        fun setErrorView(errorView: View?) = apply {
            this.errorView = errorView
        }

        fun setLoadView(loadView: View?) = apply {
            this.loadView = loadView
        }

        fun setProgressBarColor(progressBarColor: Int) = apply {
            this.progressBarColor = progressBarColor
        }

        fun setProgressBarHeight(progressBarHeight: Int) = apply {
            this.progressBarHeight = progressBarHeight
        }

        fun setErrorLayout(errorLayout: Int) = apply {
            this.errorLayout = errorLayout
        }

        fun setReloadViewId(reloadViewId: Int) = apply {
            this.reloadViewId = reloadViewId
        }

        fun setLoadLayout(loadLayout: Int) = apply {
            this.loadLayout = loadLayout
        }

        fun build(): WebViewUIManager {
            return WebViewUIManager(this)
        }
    }
}