package com.netease.cc.common.okhttp.builders;

import com.netease.cc.common.okhttp.requests.PostStringRequest;
import com.netease.cc.common.okhttp.requests.RequestCall;

import okhttp3.MediaType;

/**
 * Created by n5320 on 2016/8/31.
 */
public class PostStringBuilder extends OkHttpRequestBuilder<PostStringBuilder> {
    private String content;
    private MediaType mediaType;


    public PostStringBuilder content(String content) {
        this.content = content;
        return this;
    }

    public PostStringBuilder mediaType(MediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    @Override
    public RequestCall build() {
        return new PostStringRequest(url, tag, params, headers, content, mediaType, id).build();
    }
}
