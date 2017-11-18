package com.netease.cc.common.okhttp.rx;

import com.netease.cc.common.okhttp.OkHttpUtils;

import okhttp3.Headers;

/**
 * Created by tantai on 2016/10/14.
 * 响应体的包装类
 * 参考https://github.com/jeasonlzy/okhttp-OkGo
 */
public final class Response<T> {

    private final okhttp3.Response rawResponse;
    private final T body;

    private Response(okhttp3.Response rawResponse, T body) {
        this.rawResponse = rawResponse;
        this.body = body;
    }

    public okhttp3.Response raw() {
        return rawResponse;
    }

    public int code() {
        return rawResponse.code();
    }

    public String message() {
        return rawResponse.message();
    }

    public Headers headers() {
        return rawResponse.headers();
    }

    public boolean isSuccessful() {
        return rawResponse.isSuccessful();
    }

    public T body() {
        return body;
    }

    public static <T> Response<T> success(T body, okhttp3.Response rawResponse) {
        if (rawResponse == null) throw new NullPointerException("rawResponse == null");
        if (!rawResponse.isSuccessful()) {
            String host =rawResponse.request().header("Host");
            OkHttpUtils.getInstance().sendFailResultCallback(0, rawResponse.code(), new IllegalArgumentException("http"), rawResponse.request().url().toString(), host, null, false);
            throw new IllegalArgumentException("rawResponse must be successful response");
        }
        return new Response<>(rawResponse, body);
    }
}