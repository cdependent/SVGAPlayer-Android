package com.netease.cc.common.okhttp.callbacks;

import okhttp3.Response;

/**
 * Created by n5320 on 2016/8/23.
 */
public abstract class OkStringCallBack extends OkCallBack<String> {
    @Override
    public String parseNetworkResponse(Response response, int id) throws Throwable {
        return response.body().string();
    }
}
