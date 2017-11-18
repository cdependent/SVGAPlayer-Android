package com.netease.cc.common.okhttp.rx.convert;

import okhttp3.Response;

/**
 * Created by tantai on 2016/10/14.
 * 字符串的转换器
 * 参考https://github.com/jeasonlzy/okhttp-OkGo
 */
public class StringConvert implements Converter<String> {

    public static StringConvert create() {
        return ConvertHolder.convert;
    }

    private static class ConvertHolder {
        private static StringConvert convert = new StringConvert();
    }

    @Override
    public String convertSuccess(Response value) throws Exception {
        return value.body().string();
    }
}
