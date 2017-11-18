package com.netease.cc.common.okhttp.callbacks;

import com.google.gson.Gson;

/**
 * Created by n5320 on 2016/8/24.
 */
public class JsonGenericSerializator implements IGenericsSerializator{
    Gson mGson = new Gson();

    @Override
    public <T> T transform(String response, Class<T> classOfT) {
         return mGson.fromJson(response, classOfT);
    }
}
