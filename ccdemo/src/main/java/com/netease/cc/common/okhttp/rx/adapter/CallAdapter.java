package com.netease.cc.common.okhttp.rx.adapter;

import com.netease.cc.common.okhttp.rx.RxRequestCall;

/**
 * Created by tantai on 2016/10/14.
 */

public interface CallAdapter<T> {

    /** call执行的代理方法 */
    <R> T adapt(RxRequestCall<R> call);
}
