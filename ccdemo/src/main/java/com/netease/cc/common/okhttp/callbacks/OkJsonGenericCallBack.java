package com.netease.cc.common.okhttp.callbacks;

/**
 * Created by n5320 on 2016/8/24.
 */
public abstract class OkJsonGenericCallBack<T> extends GenericsCallback<T>{
    public OkJsonGenericCallBack() {
        super(new JsonGenericSerializator());
    }
}
