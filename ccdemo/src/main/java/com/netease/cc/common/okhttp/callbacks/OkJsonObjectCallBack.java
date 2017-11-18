package com.netease.cc.common.okhttp.callbacks;

import org.json.JSONObject;

import okhttp3.Response;

/**
 * Created by n5320 on 2016/8/23.
 */
public abstract class OkJsonObjectCallBack extends OkCallBack<JSONObject> {
    @Override
    public JSONObject parseNetworkResponse(Response response, int id) throws Throwable {
        return new JSONObject(response.body().string());
    }
}
