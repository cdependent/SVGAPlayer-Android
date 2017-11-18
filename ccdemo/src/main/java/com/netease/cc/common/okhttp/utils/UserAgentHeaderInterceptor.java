package com.netease.cc.common.okhttp.utils;

import android.content.Context;
import android.text.TextUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.Version;

/**
 * Created by n5320 on 2016/8/24.
 * <p/>
 * 添加默认的请求头UserAgent 拦截器 (未设置UA 则设置默认的UserAgent)
 */
public class UserAgentHeaderInterceptor implements Interceptor {
    private static final String USER_AGENT = "User-Agent";
    private Context mContext;

    private String mSnStr;
    private String mVersionName;

    public UserAgentHeaderInterceptor(Context context) {
        mContext = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originRequest = chain.request();
        if (TextUtils.isEmpty(originRequest.header(USER_AGENT))) { //当未设置UA时 添加默认UA
            Request newRequest = originRequest.newBuilder().header(USER_AGENT, getDefaultUserAgent()).build();
            return chain.proceed(newRequest);
        } else {
            return chain.proceed(originRequest);
        }
    }

    private String getDefaultUserAgent() {
        if(TextUtils.isEmpty(mSnStr)){
            mSnStr = "TEST_SN";
            mVersionName = "2.3.10(123456)";
        }

        return " Platform/Android" + " SN/" + mSnStr + " APP_VERSION/" + mVersionName + " USER_UID/" + "123456" + " OKHTTP/" + Version.userAgent();
    }
}
