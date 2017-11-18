package com.netease.cc.common.okhttp.builders;

import com.netease.cc.common.okhttp.requests.PostMultipartFileRequest;
import com.netease.cc.common.okhttp.requests.RequestCall;

import java.io.File;

import okhttp3.MediaType;

/**
 * 表单形式上传文件的builder
 * Created by g7603
 */
public class PostMultipartFileBuilder extends OkHttpRequestBuilder<PostMultipartFileBuilder> {

    File file;
    MediaType mediaType;

    public PostMultipartFileBuilder file(File file) {
        this.file = file;
        return this;
    }

    public PostMultipartFileBuilder mediaType(MediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    @Override
    public RequestCall build() {
        return new PostMultipartFileRequest(url, tag, params, headers, file, mediaType, id).build();
    }
}

