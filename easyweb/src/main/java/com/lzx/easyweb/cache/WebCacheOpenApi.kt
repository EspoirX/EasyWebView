package com.lzx.easyweb.cache

import com.lzx.easyweb.cache.interceptor.ResourceInterceptor


interface WebCacheOpenApi {
    fun setCacheMode(mode: WebCacheMode?)

    fun addResourceInterceptor(interceptor: ResourceInterceptor?)
}