package com.netease.cc.common.okhttp.callbacks;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;

import okhttp3.Response;

/**
 * Created by n5320 on 2016/8/24.
 */
public abstract class GenericsCallback <T> extends OkCallBack<T> {
    IGenericsSerializator mGenericsSerializator;

    public GenericsCallback(IGenericsSerializator serializator) {
        mGenericsSerializator = serializator;
    }

    @Override
    public T parseNetworkResponse(Response response, int id) throws IOException {
        String string = response.body().string();
        Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        if (entityClass == String.class) {
            return (T) string;
        }
        T bean = mGenericsSerializator.transform(string, entityClass);

        return bean;
    }

}
