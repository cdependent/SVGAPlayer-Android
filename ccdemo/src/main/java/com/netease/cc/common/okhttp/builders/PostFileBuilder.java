package com.netease.cc.common.okhttp.builders;

import com.netease.cc.common.okhttp.requests.PostFileRequest;
import com.netease.cc.common.okhttp.requests.RequestCall;

import java.io.File;

import okhttp3.MediaType;

/**
 * Created by n5320 on 2016/8/29.
 */
public class PostFileBuilder extends OkHttpRequestBuilder<PostFileBuilder> {
    private File file;
    private MediaType mediaType;


    public PostFileBuilder file(File file) {
        this.file = file;
        return this;
    }

    public PostFileBuilder mediaType(MediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    @Override
    public RequestCall build() {
        return new PostFileRequest(url, tag, params, headers, file, mediaType, id).build();
    }
}

