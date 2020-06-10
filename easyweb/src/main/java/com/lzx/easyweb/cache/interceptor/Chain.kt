package com.lzx.easyweb.cache.interceptor

import com.lzx.easyweb.cache.CacheRequest

class Chain internal constructor(private val mInterceptors: MutableList<ResourceInterceptor?>) {
    private var mIndex = -1
    var request: CacheRequest? = null
        private set

    fun process(request: CacheRequest?): WebResource? {
        if (++mIndex >= mInterceptors.size) {
            return null
        }
        this.request = request
        val interceptor = mInterceptors[mIndex]
        return interceptor?.load(this)
    }

}