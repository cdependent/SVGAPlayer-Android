package com.netease.cc.common.okhttp.utils;

/**
 * Created by n5320 on 2016/8/24.
 */
public interface IOkHttpErrorWatchDog {
    void onError(int statusCode, String url, String errorReason, String host, Exception errorException);
}
