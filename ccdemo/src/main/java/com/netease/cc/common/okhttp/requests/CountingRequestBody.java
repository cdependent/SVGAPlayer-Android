package com.netease.cc.common.okhttp.requests;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by n5320 on 2016/8/29.
 *
 * 对RequestBody 的包装 主要用于大文件上传时  对multipart requests上传进度的的监控
 */
public class CountingRequestBody extends RequestBody {
    protected RequestBody delegate;
    protected OkRequestProgressListener mProgressListener;

    protected CountingSink countingSink;

    public CountingRequestBody(RequestBody delegate, OkRequestProgressListener listener) {
        this.delegate = delegate;
        this.mProgressListener = listener;
    }

    @Override
    public MediaType contentType() {
        return delegate.contentType();
    }

    @Override
    public long contentLength() {
        try {
            return delegate.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {

        countingSink = new CountingSink(sink);
        BufferedSink bufferedSink = Okio.buffer(countingSink);

        delegate.writeTo(bufferedSink);

        bufferedSink.flush();
    }

    protected final class CountingSink extends ForwardingSink {

        private long bytesWritten = 0;

        public CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);

            bytesWritten += byteCount;
            mProgressListener.onRequestProgress(bytesWritten, contentLength());
        }
    }

    /**
     * 上传进度的回调接口
     */
    public  interface OkRequestProgressListener {
         void onRequestProgress(long bytesWritten, long contentLength);
    }
}
