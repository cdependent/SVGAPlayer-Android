package com.netease.cc.common.okhttp.callbacks;

/**
 * Created by n5320 on 2016/8/24.
 */
public interface IGenericsSerializator {
    <T> T transform(String response, Class<T> classOfT);
}
