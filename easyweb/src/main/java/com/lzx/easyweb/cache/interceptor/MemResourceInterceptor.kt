package com.lzx.easyweb.cache.interceptor

import android.util.Log
import android.util.LruCache
import com.lzx.easyweb.EasyWeb
import com.lzx.easyweb.cache.CacheConfig
import javax.security.auth.Destroyable

class MemResourceInterceptor private constructor() : ResourceInterceptor, Destroyable {
    private var lruCache: LruCache<String?, WebResource?>? = null

    companion object {
        @Volatile
        private var sInstance: MemResourceInterceptor? = null

        val instance: MemResourceInterceptor?
            get() {
                if (sInstance == null) {
                    synchronized(MemResourceInterceptor::class.java) {
                        if (sInstance == null) {
                            sInstance = MemResourceInterceptor()
                        }
                    }
                }
                return sInstance
            }
    }

    init {
        val memorySize: Int = CacheConfig.instance.memCacheSize
        if (memorySize > 0) {
            lruCache = ResourceMemCache(memorySize)
        }
    }

    override fun load(chain: Chain?): WebResource? {
        val request = chain?.request
        var resource = lruCache?.get(request?.key)
        if (checkResourceValid(resource)) {
            Log.i(
                EasyWeb.TAG,
                String.format("WebView file form Memory cache: %s", request?.url)
            )
            return resource
        }
        //拿磁盘缓存
        resource = chain?.process(request)
        if (checkResourceValid(resource)) {
            lruCache?.put(request?.key, resource)
        }
        return resource
    }

    private fun checkResourceValid(resource: WebResource?): Boolean {
        return resource?.originBytes != null && resource.responseHeaders?.isNotEmpty() == true
    }

    override fun destroy() {
        lruCache?.evictAll()
        lruCache = null
    }

    private class ResourceMemCache internal constructor(maxSize: Int) :
        LruCache<String?, WebResource?>(maxSize) {
        override fun sizeOf(key: String?, value: WebResource?): Int {
            return value?.originBytes?.size ?: 0
        }
    }
}