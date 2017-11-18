package com.netease.cc.common.okhttp.rx.adapter;

import com.netease.cc.common.okhttp.rx.RxRequestCall;

/**
 * Created by tantai on 2016/10/14.
 */

public class DefaultCallAdapter<T> implements CallAdapter<RxRequestCall<T>> {

    public static <T> DefaultCallAdapter<T> create() {
        return new DefaultCallAdapter<>();
    }

    @Override
    public <R> RxRequestCall<T> adapt(RxRequestCall<R> call) {
        return (RxRequestCall<T>) call;
    }
}
