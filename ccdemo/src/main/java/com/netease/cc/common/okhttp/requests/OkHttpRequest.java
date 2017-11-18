package com.netease.cc.common.okhttp.requests;

import com.netease.cc.common.okhttp.callbacks.OkCallBack;

import java.util.Map;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by n5320 on 2016/8/23.
 */
public abstract class OkHttpRequest {

    private static final String INTERNAL_404_URL = "http://192.168.1.1:1/404";

    protected String url;
    protected Object tag;
    protected Map<String, String> params;
    protected Map<String, String> headers;
    protected int id;
    protected String orginUrl;

    private boolean mIsUrlValidate = true;

    protected Request.Builder builder = new Request.Builder();

    protected OkHttpRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers, int id) {
        this.url = url;
        this.tag = tag;
        this.params = params;
        this.headers = headers;
        this.id = id;
        this.orginUrl = url;

        if (!isUrlValidate(url)) {
            this.url = INTERNAL_404_URL;
            mIsUrlValidate = false;
           // Exceptions.illegalArgument("url can not be null.");
        }

        initBuilder();
    }

    /**
     * 初始化一些基本参数 url , tag , headers
     */
    private void initBuilder() {
        builder.url(url).tag(tag);
        appendHeaders();
    }

    protected abstract RequestBody buildRequestBody();

    protected RequestBody wrapRequestBody(RequestBody requestBody, final OkCallBack callback) {
        return requestBody;
    }

    protected abstract Request buildRequest(RequestBody requestBody);

    public RequestCall build() {
        return new RequestCall(this);
    }


    public Request generateRequest(OkCallBack callback) {
        RequestBody requestBody = buildRequestBody();
        RequestBody wrappedRequestBody = wrapRequestBody(requestBody, callback);
        Request request = buildRequest(wrappedRequestBody);
        return request;
    }


    protected void appendHeaders() {
        Headers.Builder headerBuilder = new Headers.Builder();


        if (headers == null || headers.isEmpty()) return;

        for (String key : headers.keySet()) {
            headerBuilder.add(key, headers.get(key));
        }
        builder.headers(headerBuilder.build());
    }

    public int getId() {
        return id;
    }

    public String getOrginUrl() {
        return orginUrl;
    }

    public boolean isUrlValidate(){
        return mIsUrlValidate;
    }

    public String getHeadersStr(){
        if(headers == null || headers.isEmpty()){
            return  "";
        }
        StringBuilder result = new StringBuilder();

        for (String key : headers.keySet()) {
            result.append(key).append(headers.get(key)).append("\n");
        }

        return result.toString();
    }

    private boolean isUrlValidate(String url){
        if (url == null){
            return false;
        }

        if (url.regionMatches(true, 0, "ws:", 0, 3)) {
            url = "http:" + url.substring(3);
        } else if (url.regionMatches(true, 0, "wss:", 0, 4)) {
            url = "https:" + url.substring(4);
        }

        HttpUrl parsed = HttpUrl.parse(url);

        if(parsed == null){
            return  false;
        }

        return true;
    }
}
