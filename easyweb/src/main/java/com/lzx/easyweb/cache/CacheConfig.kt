package com.lzx.easyweb.cache

class CacheConfig {
    companion object {
        val instance: CacheConfig by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            CacheConfig()
        }
    }
}