package com.lzx.easyweb.cache.interceptor

class SourceRequest(
    request: CacheRequest, var isCacheable: Boolean
) {
    var url: String? = request.fileUrl
    var headers: Map<String, String>? = request.headers
    var userAgent: String? = request.userAgent
    var webViewCache: Int = request.webViewCacheMode
}