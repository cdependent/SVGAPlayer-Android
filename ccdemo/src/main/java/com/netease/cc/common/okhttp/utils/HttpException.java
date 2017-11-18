package com.netease.cc.common.okhttp.utils;

/*******************************************************************************
 * Created on: 2017/7/11 10:40
 * Author: 莫宇
 * Mail: myn1213@corp.netease.com
 * Description: OKHTTP异常（可附带Response对象）
 *******************************************************************************/

import java.util.Locale;

import okhttp3.Response;


public final class HttpException extends Exception {

    private int code;
    private String message;
    private String body;

    public HttpException() {
        super();
    }

    public HttpException(String detailMessage) {
        super(detailMessage);
    }

    public HttpException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public HttpException(Throwable throwable) {
        super(throwable);
    }

    public HttpException withResponse(Response response) {
        if (response != null) {
            this.code = response.code();
            this.message = response.message();
            try {
                this.body = response.body().string();
            } catch (Exception e) {
                this.body = "";
            }
        }
        return this;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }

    public String body() {
        return body;
    }

    @Override
    public String toString() {
        String msg = getLocalizedMessage();
        return String.format(Locale.getDefault(), "%s: %s{code=%d, message='%s', body='%s'}", getClass().getName(), msg == null ? "" : msg + " ", code, message, body == null ? null : body.replaceAll("\n", ""));
    }
}
