package com.lzx.easyweb.builder

import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import com.lzx.easyweb.ui.IProgressView

class WebUIBuilder(private val webBuilder: WebBuilder) {

    fun setWebParent(
        v: ViewGroup,
        lp: ViewGroup.LayoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ),
        index: Int = 0
    ): WebUIBuilder {
        webBuilder.viewGroup = v
        webBuilder.layoutParams = lp
        webBuilder.index = index
        return this
    }

    fun addErrorView(@LayoutRes errorLayoutId: Int, @IdRes reloadViewId: Int = -1) = apply {
        this.webBuilder.errorLayout = errorLayoutId
        this.webBuilder.reloadViewId = reloadViewId
    }

    fun addErrorView(errorView: View, @IdRes reloadViewId: Int = -1) = apply {
        this.webBuilder.errorView = errorView
        this.webBuilder.reloadViewId = reloadViewId
    }

    fun addLoadView(@LayoutRes loadLayoutId: Int) = apply {
        this.webBuilder.loadLayout = loadLayoutId
    }

    fun addLoadView(loadView: View) = apply {
        this.webBuilder.loadView = loadView
    }

    fun addProgressBar(isUser: Boolean, isCustom: Boolean = false) = apply {
        this.webBuilder.needProgressBar = isUser
        this.webBuilder.isCustomProgressBar = isCustom
    }

    fun setProgressView(progressView: IProgressView) = apply {
        this.webBuilder.progressView = progressView
    }

    fun progressBarColor(@ColorInt color: Int) = apply {
        this.webBuilder.progressBarColor = color
    }

    fun progressBarHeight(height: Int) = apply {
        this.webBuilder.progressBarHeight = height
    }

    fun okUI() = webBuilder

    fun ready() = this.webBuilder.ready()
}