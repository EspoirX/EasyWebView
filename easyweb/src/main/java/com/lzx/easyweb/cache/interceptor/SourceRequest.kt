package com.lzx.easyweb.cache.interceptor

import com.lzx.easyweb.cache.CacheRequest

class SourceRequest(
    request: CacheRequest, var isCacheable: Boolean
) {
    var url: String? = request.url
    var headers: Map<String, String>? = request.headers
    var userAgent: String? = request.userAgent
    var webViewCache = request.webViewCacheMode
}