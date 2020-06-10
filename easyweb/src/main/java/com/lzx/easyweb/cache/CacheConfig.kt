package com.lzx.easyweb.cache

import android.content.Context
import android.os.Environment
import com.lzx.easyweb.utils.CacheUtils
import java.util.*

class CacheConfig {

    companion object {
        val instance: CacheConfig by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            CacheConfig()
        }
    }

    var context: Context? = null
    var connectTimeout = 5000
    var readTimeout = 15000
    var cacheSize = 100 * 1024 * 1024.toLong()
    var version: Long = 0
    var memCacheSize: Int = CacheUtils.getMemorySize()
    var cacheFileLists: List<String> = ArrayList()
    var ignoreUrl: List<String> = ArrayList()
    var defaultCachePath =
        Environment.getExternalStorageDirectory().absolutePath + "/Bilin/.WebViewCache"
}