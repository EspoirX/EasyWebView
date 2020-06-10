package com.lzx.easyweb.cache.interceptor;


public interface ResourceInterceptor {

    WebResource load(Chain chain);

}
