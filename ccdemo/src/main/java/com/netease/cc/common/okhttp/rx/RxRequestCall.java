package com.netease.cc.common.okhttp.rx;

import com.netease.cc.common.okhttp.requests.OkHttpRequest;
import com.netease.cc.common.okhttp.requests.RequestCall;
import com.netease.cc.common.okhttp.rx.adapter.CallAdapter;
import com.netease.cc.common.okhttp.rx.adapter.DefaultCallAdapter;
import com.netease.cc.common.okhttp.rx.convert.Converter;

import rx.Observable;

/**
 * Created by tantai on 2016/10/14.
 * 适配Rx
 */

public class RxRequestCall<R> extends RequestCall {
    private volatile boolean canceled;
    private boolean executed;
    private Converter mConverter;

    public RxRequestCall(OkHttpRequest request) {
        super(request);
    }

    @SuppressWarnings("unchecked")
    public <T> RxRequestCall<T> getCall(Converter<T> converter) {
        mConverter = converter;
        return DefaultCallAdapter.<T>create().adapt((RxRequestCall<T>) this);
    }

    /**
     * Rx支持,获取同步call对象
     */
    @SuppressWarnings("unchecked")
    public <T, E> Observable<E> getCall(Converter<T> converter, CallAdapter<E> adapter) {
        mConverter = converter;
        return (Observable<E>) adapter.adapt(getCall(converter));
    }

    public Response<R> execute() throws Exception {
        synchronized (this) {
            if (executed) throw new IllegalStateException("Already executed.");
            executed = true;
        }
        buildCall(null);
        okhttp3.Call call = getCall();
        if (canceled) {
            call.cancel();
        }
        return parseResponse(call.execute());
    }

    @SuppressWarnings("unchecked")
    private Response<R> parseResponse(okhttp3.Response rawResponse) throws Exception {
        R body = (R) mConverter.convertSuccess(rawResponse);
        return Response.success(body, rawResponse);
    }

    public void cancel() {
        canceled = true;
        if (getCall() != null) {
            getCall().cancel();
        }
    }

    public boolean isCanceled() {
        return canceled;
    }

    @Override
    public RxRequestCall clone() {
        RxRequestCall rxRequestCall = new RxRequestCall(okHttpRequest);
        rxRequestCall.readTimeOut = readTimeOut;
        rxRequestCall.writeTimeOut = writeTimeOut;
        rxRequestCall.connTimeOut = connTimeOut;
        rxRequestCall.mConverter = mConverter;
        return rxRequestCall;
    }
}
