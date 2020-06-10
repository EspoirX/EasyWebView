package com.lzx.easyweb.ui

import android.content.Context
import android.view.ViewGroup
import android.widget.ProgressBar

class DefaultProgressView @JvmOverloads constructor(
    context: Context
) : IProgressView {

    var progressBar: ProgressBar? = null

    init {
        progressBar = ProgressBar(context)
    }



    override fun show() {
        TODO("Not yet implemented")
    }

    override fun hide() {
        TODO("Not yet implemented")
    }

    override fun reset() {
        TODO("Not yet implemented")
    }

    override fun setProgress(progress: Int) {
        TODO("Not yet implemented")
    }

    override fun getProLayoutParams(): ViewGroup.LayoutParams? {
        TODO("Not yet implemented")
    }




}