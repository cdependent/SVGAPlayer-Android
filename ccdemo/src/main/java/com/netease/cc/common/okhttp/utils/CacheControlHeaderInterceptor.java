package com.netease.cc.common.okhttp.utils;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by g7603 on 2017/5/31.
 * <p/>
 * 添加默认的请求头CacheControl 拦截器(把所有的http请求设置为noCache模式)
 */
public class CacheControlHeaderInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originRequest = chain.request();
        boolean isNoCache=originRequest.cacheControl().noCache();
//        Log.i("CacheControl","CacheControl  isNoCache: "+isNoCache,false);
        if (isNoCache){
            return chain.proceed(originRequest);
        }else {
            Request newRequest = originRequest.newBuilder().cacheControl(CacheControl.FORCE_NETWORK).build();
            return chain.proceed(newRequest);
        }
    }

}
