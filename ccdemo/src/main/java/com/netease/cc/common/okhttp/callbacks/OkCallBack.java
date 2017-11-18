package com.netease.cc.common.okhttp.callbacks;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by n5320 on 2016/8/23.
 */
public abstract class OkCallBack<T> {

    /**
     * 请求发送前回调
     * @param request
     * @param id
     */
    public void onBefore(Request request, int id) {
    }

    public void onAfter(int id) {
    }

    public void inProgress(float progress,float speed ,long total, int id) {

    }

    /**
     * if you parse reponse code in parseNetworkResponse, you should make this method return true.
     *
     * @param response
     * @return
     */
    public boolean validateResponse(Response response) {
        return response.isSuccessful();
    }

    /**
     * Thread Pool Thread
     *
     * @param response
     */
    public abstract T parseNetworkResponse(Response response, int id) throws Throwable;

    public abstract void onError(Exception e, int errorCode);

    public abstract void onResponse(T response, int statusCode);


    public static OkCallBack CALLBACK_DEFAULT = new OkCallBack() {

        @Override
        public Object parseNetworkResponse(Response response, int id) throws Throwable {
            return null;
        }

        @Override
        public void onError(Exception e, int errorCode) {

        }

        @Override
        public void onResponse(Object response, int statusCode) {

        }
    };
}
