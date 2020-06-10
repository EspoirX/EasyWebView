package com.lzx.easyweb.cache.interceptor;

import java.util.List;


public class Chain {

    private List<ResourceInterceptor> mInterceptors;
    private int mIndex = -1;
    private CacheRequest mRequest;

    Chain(List<ResourceInterceptor> interceptors) {
        mInterceptors = interceptors;
    }

    public WebResource process(CacheRequest request) {
        if (++mIndex >= mInterceptors.size()) {
            return null;
        }
        mRequest = request;
        ResourceInterceptor interceptor = mInterceptors.get(mIndex);
        return interceptor.load(this);
    }

    public CacheRequest getRequest() {
        return mRequest;
    }
}
