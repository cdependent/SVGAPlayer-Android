package com.netease.cc.common.okhttp.rx.request;

import com.netease.cc.common.okhttp.requests.OkHttpRequest;
import com.netease.cc.common.okhttp.rx.RxRequestCall;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by n5320 on 2016/9/27.
 */
public class RxPostUrlEncodedFormRequest<T> extends OkHttpRequest {
    private static MediaType MEDIA_TYPE_PLAIN = MediaType.parse("application/x-www-form-urlencoded");
    byte[] content;

    public RxPostUrlEncodedFormRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers, int id, byte[] content) {
        super(url, tag, params, headers, id);
        this.content = content;
    }

    @Override
    protected RequestBody buildRequestBody() {
        return RequestBody.create(MEDIA_TYPE_PLAIN,content);
    }

    @Override
    protected Request buildRequest(RequestBody requestBody) {
        return builder.post(requestBody).build();
    }

    public RxRequestCall<T> build() {
        return new RxRequestCall<T>(this);
    }
}
