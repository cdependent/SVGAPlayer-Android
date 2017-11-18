package com.netease.cc.common.okhttp.builders;

import java.util.Map;

/**
 * Created by n5320 on 2016/8/23.
 */
public interface HasParamsable {
    OkHttpRequestBuilder params(Map<String, String> params);
    OkHttpRequestBuilder addParams(String key, String val);
}
