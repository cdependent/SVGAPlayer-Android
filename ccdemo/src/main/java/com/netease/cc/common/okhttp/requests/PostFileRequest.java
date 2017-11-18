package com.netease.cc.common.okhttp.requests;

import com.netease.cc.common.okhttp.callbacks.OkCallBack;
import com.netease.cc.common.okhttp.utils.Exceptions;
import com.netease.cc.common.utils.thread.UiThreadUtil;

import java.io.File;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by n5320 on 2016/8/29.
 */
public class PostFileRequest extends OkHttpRequest {
    private static MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream");

    private File file;
    private MediaType mediaType;

    public PostFileRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers, File file, MediaType mediaType, int id) {
        super(url, tag, params, headers, id);
        this.file = file;
        this.mediaType = mediaType;

        if (this.file == null) {
            Exceptions.illegalArgument("the file can not be null !");
        }
        if (this.mediaType == null) {
            this.mediaType = MEDIA_TYPE_STREAM;
        }
    }

    @Override
    protected RequestBody buildRequestBody() {
        return RequestBody.create(mediaType, file);
    }

    @Override
    protected RequestBody wrapRequestBody(RequestBody requestBody, final OkCallBack callback) {
        if (callback == null) {
            return requestBody;
        }

        //计算进度和速度
        CountingRequestBody countingRequestBody = new CountingRequestBody(requestBody, new CountingRequestBody.OkRequestProgressListener() {
            long lastRefreshTime;
            long lastBytesWrite;
            @Override
            public void onRequestProgress(final long bytesWritten, final long contentLength) {
                long curTime = System.currentTimeMillis();
                long intervalTime = curTime - lastRefreshTime;
                long intervalBytes = bytesWritten - lastBytesWrite;
                if (intervalTime > 100 || bytesWritten == contentLength) {
                    lastRefreshTime = System.currentTimeMillis();
                    lastBytesWrite = bytesWritten;
                    final float speed = intervalBytes /1024.0f * 1000 / intervalTime; //speed （KB/S）
                    final float percent = bytesWritten * 1.0f / contentLength * 100;
                    UiThreadUtil.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.inProgress(percent ,speed ,contentLength, id);
                        }
                    });
                }
            }
        });

        return countingRequestBody;
    }

    @Override
    protected Request buildRequest(RequestBody requestBody) {
        return builder.post(requestBody).build();
    }
}

