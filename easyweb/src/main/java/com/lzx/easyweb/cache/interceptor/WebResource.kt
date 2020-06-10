package com.lzx.easyweb.cache.interceptor

class WebResource {
    var responseCode = 0
    var message: String? = null
    var responseHeaders: Map<String, String>? = null
    var isModified = true
    var isCache = false
    var originBytes: ByteArray? = null

}