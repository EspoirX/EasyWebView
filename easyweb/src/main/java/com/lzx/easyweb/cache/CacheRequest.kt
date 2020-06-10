package com.lzx.easyweb.cache

import com.lzx.easyweb.utils.CacheUtils.getMD5
import java.net.URLEncoder

class CacheRequest {
    var key: String? = null
        private set
    var url: String? = null
        set(url) {
            field = url
            key = generateKey(url)
        }
    var requestUrl: String? = null
    var mimeType: String? = null
    var headers: Map<String, String>? = null
    var userAgent: String? = null
    var webViewCacheMode: WebCacheMode? = null

    companion object {
        private fun generateKey(url: String?): String? {
            return getMD5(URLEncoder.encode(url), false)
        }
    }
}