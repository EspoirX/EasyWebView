package com.lzx.easyweb.ui

import android.view.ViewGroup


interface IProgressView {
    fun show()

    fun hide()

    fun reset()

    fun setProgress(progress: Int)

    fun getProLayoutParams(): ViewGroup.LayoutParams?
}