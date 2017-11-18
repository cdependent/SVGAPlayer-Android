package com.netease.cc.common.okhttp.rx.request;

import com.netease.cc.common.okhttp.requests.OkHttpRequest;
import com.netease.cc.common.okhttp.rx.RxRequestCall;

import java.util.Map;

import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by tantai on 2016/10/14.
 */

public class RxGetRequest<T> extends OkHttpRequest {
    public RxGetRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers, int id) {
        super(url, tag, params, headers, id);
    }

    @Override
    protected RequestBody buildRequestBody() {
        return null;
    }

    @Override
    protected Request buildRequest(RequestBody requestBody) {
        return builder.get().build();
    }

    public RxRequestCall<T> build() {
        return new RxRequestCall<T>(this);
    }
}
