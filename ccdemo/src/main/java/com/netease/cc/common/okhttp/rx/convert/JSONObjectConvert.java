package com.netease.cc.common.okhttp.rx.convert;

import org.json.JSONObject;

import okhttp3.Response;

/**
 * Created by tantai on 2016/10/14.
 * JSONObject转换器
 * 参考https://github.com/jeasonlzy/okhttp-OkGo
 */
public class JSONObjectConvert implements Converter<JSONObject> {

    public static JSONObjectConvert create() {
        return ConvertHolder.convert;
    }

    private static class ConvertHolder {
        private static JSONObjectConvert convert = new JSONObjectConvert();
    }

    @Override
    public JSONObject convertSuccess(Response value) throws Exception {
        return new JSONObject(value.body().string());
    }
}
