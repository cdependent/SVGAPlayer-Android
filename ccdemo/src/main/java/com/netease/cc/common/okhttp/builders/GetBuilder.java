package com.netease.cc.common.okhttp.builders;

import android.net.Uri;

import com.netease.cc.common.okhttp.requests.GetRequest;
import com.netease.cc.common.okhttp.requests.RequestCall;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by n5320 on 2016/8/23.
 */
public class GetBuilder extends OkHttpRequestBuilder<GetBuilder> implements HasParamsable {
    @Override
    public RequestCall build() {
        if (params != null) {
            url = appendParams(url, params);
        }

        return new GetRequest(url, tag, params, headers, id).build();
    }

    protected String appendParams(String url, Map<String, String> params) {
        if (url == null || params == null || params.isEmpty()) {
            return url;
        }
        Uri.Builder builder = Uri.parse(url).buildUpon();
        Set<String> keys = params.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            builder.appendQueryParameter(key, params.get(key));
        }
        return builder.build().toString();
    }


    @Override
    public GetBuilder params(Map<String, String> params) {
        this.params = params;
        return this;
    }

    @Override
    public GetBuilder addParams(String key, String val) {
        if (this.params == null) {
            params = new LinkedHashMap<>();
        }
        params.put(key, val);
        return this;
    }

    public GetBuilder addParams(String key, Object object) {
        return addParams(key, String.valueOf(object));
    }
}
