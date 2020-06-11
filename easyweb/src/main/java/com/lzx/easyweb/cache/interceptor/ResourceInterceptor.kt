package com.lzx.easyweb.cache.interceptor

interface ResourceInterceptor {
    fun load(chain: Chain?): WebResource?
    fun destroy()
}