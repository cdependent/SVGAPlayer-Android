package com.netease.cc.common.okhttp.requests;

import com.netease.cc.common.okhttp.callbacks.OkCallBack;
import com.netease.cc.common.okhttp.utils.Exceptions;

import java.io.File;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 表单形式的post request
 * Created by g7603
 */
public class PostMultipartFileRequest extends OkHttpRequest {
    private static MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream");
    File file;
    MediaType mediaType;

    public PostMultipartFileRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers, File file, MediaType mediaType, int id) {
        super(url, tag, params, headers, id);
        this.file = file;
        this.mediaType=mediaType;

        if (this.file == null) {
            Exceptions.illegalArgument("the file can not be null !");
        }
        if (this.mediaType == null) {
            this.mediaType = MEDIA_TYPE_STREAM;
        }
    }

    @Override
    protected RequestBody buildRequestBody() {

        return RequestBody.create(mediaType,file);
    }

    @Override
    protected Request buildRequest(RequestBody requestBody) {
        return builder.post(requestBody).build();
    }

    @Override
    protected RequestBody wrapRequestBody(RequestBody requestBody, OkCallBack callback) {
        if (callback == null) {
            return requestBody;
        }
        // form 表单形式上传
        MultipartBody.Builder multipartRequestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (file!=null){
            multipartRequestBody.addFormDataPart("file", file.getName(),requestBody);
        }
        if (params!=null){
            for (Map.Entry entry : params.entrySet()) {
                multipartRequestBody.addFormDataPart(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
            }
        }
        return multipartRequestBody.build();
    }
}
