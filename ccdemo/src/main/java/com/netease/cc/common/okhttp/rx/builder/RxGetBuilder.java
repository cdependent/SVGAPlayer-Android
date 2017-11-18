package com.netease.cc.common.okhttp.rx.builder;

import android.net.Uri;

import com.netease.cc.common.okhttp.builders.HasParamsable;
import com.netease.cc.common.okhttp.builders.OkHttpRequestBuilder;
import com.netease.cc.common.okhttp.rx.RxRequestCall;
import com.netease.cc.common.okhttp.rx.request.RxGetRequest;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by tantai on 2016/10/14.
 */

public class RxGetBuilder extends OkHttpRequestBuilder<RxGetBuilder> implements HasParamsable {

    @Override
    public RxRequestCall build() {
        if (params != null) {
            url = appendParams(url, params);
        }

        return new RxGetRequest(url, tag, params, headers, id).build();
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
    public RxGetBuilder params(Map<String, String> params) {
        this.params = params;
        return this;
    }

    @Override
    public RxGetBuilder addParams(String key, String val) {
        if (this.params == null) {
            params = new LinkedHashMap<>();
        }
        params.put(key, val);
        return this;
    }

    public RxGetBuilder addParams(String key, Object val) {
        return addParams(key, String.valueOf(val));
    }
}
