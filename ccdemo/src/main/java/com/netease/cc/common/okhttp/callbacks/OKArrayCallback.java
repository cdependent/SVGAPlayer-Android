package com.netease.cc.common.okhttp.callbacks;

import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Response;

/**
 * 处理返回 JsonArray 的情况
 * Created by zhengxinwei@N3072 on 2016/12/12.
 */
public abstract class OKArrayCallback<T> extends OkCallBack<List<T>> {

    Gson mGson = new Gson();

    @Override public List<T> parseNetworkResponse(Response response, int id) throws Exception {
        String string = response.body().string();
        @SuppressWarnings("unchecked")
        final Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        List<T> result = mGson.fromJson(string, new ListParameterizedType(entityClass));
        if (result == null) {
            return Collections.emptyList();
        }
        return result;
    }

    private static class ListParameterizedType implements ParameterizedType {
        private Type type;

        private ListParameterizedType(Type type) {
            this.type = type;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{type};
        }

        @Override
        public Type getRawType() {
            return ArrayList.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }
}
