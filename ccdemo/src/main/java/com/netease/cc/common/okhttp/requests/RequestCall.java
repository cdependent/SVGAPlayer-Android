package com.netease.cc.common.okhttp.requests;

import android.os.Handler;
import android.os.Looper;

import com.netease.cc.common.okhttp.OkHttpUtils;
import com.netease.cc.common.okhttp.callbacks.OkCallBack;
import com.netease.cc.common.okhttp.callbacks.OkFileCallBack;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by n5320 on 2016/8/23.
 * 对okHttp 的 Request Call 进行封装
 */
public class RequestCall {
    public static final int STATUS_CREATE = 1;
    /**
     * 请求被加入请求队列(当前可以作为正在请求中状态)
     */
    public static final int STATUS_ADD_TO_EXECUTE_QUEUE = 2;
    public static final int STATUS_EXECUTE_FINISHED = 3;
    public static final int STATUS_CANCEL = 4;

    protected OkHttpRequest okHttpRequest;
    private Request request;
    private Call call;
    private int mStatus;//请求状态

    protected long readTimeOut;
    protected long writeTimeOut;
    protected long connTimeOut;
    private Handler mHandler = new Handler(Looper.myLooper());
    private OkHttpClient clone;

    public RequestCall(OkHttpRequest request) {
        this.okHttpRequest = request;
    }

    public RequestCall readTimeOut(long readTimeOut) {
        this.readTimeOut = readTimeOut;
        return this;
    }

    public RequestCall writeTimeOut(long writeTimeOut) {
        this.writeTimeOut = writeTimeOut;
        return this;
    }

    public RequestCall connTimeOut(long connTimeOut) {
        this.connTimeOut = connTimeOut;
        return this;
    }

    /**
     * 标记请求已经完成(Http请求Response)
     */
    public void finishRequestCall() {
        updateStatus(STATUS_EXECUTE_FINISHED);
    }

    /**
     * 请求是否结束(cancel finish 均表示结束)
     */
    public boolean isRequestEnded() {
        if (mStatus == STATUS_CANCEL || mStatus == STATUS_EXECUTE_FINISHED) {
            return true;
        }

        return false;
    }

    /**
     * 请求是否进行中
     * @return
     */
    public boolean isCallRequesting(){
        return mStatus == STATUS_ADD_TO_EXECUTE_QUEUE;
    }



    public Call buildCall(OkCallBack callback) {
        request = generateRequest(callback);

        if (readTimeOut > 0 || writeTimeOut > 0 || connTimeOut > 0) {
            readTimeOut = readTimeOut > 0 ? readTimeOut : OkHttpUtils.DEFAULT_MILLISECONDS;
            writeTimeOut = writeTimeOut > 0 ? writeTimeOut : OkHttpUtils.DEFAULT_MILLISECONDS;
            connTimeOut = connTimeOut > 0 ? connTimeOut : OkHttpUtils.DEFAULT_MILLISECONDS;

            clone = OkHttpUtils.getInstance().getOkHttpClient().newBuilder()
                    .readTimeout(readTimeOut, TimeUnit.MILLISECONDS)
                    .writeTimeout(writeTimeOut, TimeUnit.MILLISECONDS)
                    .connectTimeout(connTimeOut, TimeUnit.MILLISECONDS)
                    .build();

            call = clone.newCall(request);
        } else {
            call = OkHttpUtils.getInstance().getOkHttpClient().newCall(request);
        }

        updateStatus(STATUS_CREATE);

        return call;
    }

    private Request generateRequest(OkCallBack callback) {
        return okHttpRequest.generateRequest(callback);
    }

    public void execute(OkCallBack callback) {
        execute(false, callback);
    }

    /**
     * 同步执行请求  通过回调返回请求结果
     *
     * @param callback
     */
    public void executeSync(OkCallBack callback) {
        execute(true,callback);
    }

    private void execute(boolean isSync, final OkCallBack callback){

        if(okHttpRequest == null || callback == null){
            return;
        }

        if(!okHttpRequest.isUrlValidate()){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onError(new IllegalArgumentException(String.format("unexpected url: %s", okHttpRequest.orginUrl)), -1);
                }
            });

            return;
        }

        if(callback instanceof OkFileCallBack){
            readTimeOut = readTimeOut > 0 ? readTimeOut : OkHttpUtils.DEFAULT_DOWNLOAD_FILE_MILLISECONDS;
            writeTimeOut = writeTimeOut > 0 ? writeTimeOut : OkHttpUtils.DEFAULT_DOWNLOAD_FILE_MILLISECONDS;
            connTimeOut = connTimeOut > 0 ? connTimeOut : OkHttpUtils.DEFAULT_DOWNLOAD_FILE_MILLISECONDS;
        }

        buildCall(callback);

        callback.onBefore(request, getOkHttpRequest().getId());
        updateStatus(STATUS_ADD_TO_EXECUTE_QUEUE);

        if (isSync) {
            OkHttpUtils.getInstance().executeSync(this, callback);
        }else {
            OkHttpUtils.getInstance().executeAsync(this, callback);
        }
    }

    /**
     * (为了避免与OkHttp库的耦合 建议避免在okhttp包以外的地方调用此方法)
     *
     * @return 返回Call对象
     */
    public Call getCall() {
        return call;
    }


    public String getUrl() {
        if (call == null || call.request() == null || call.request().url() == null) {
            return "";
        }

        return call.request().url().toString();
    }

    public OkHttpRequest getOkHttpRequest() {
        return okHttpRequest;
    }

    public void cancel() {
        if (call != null) {
            call.cancel();
            updateStatus(STATUS_CANCEL);
        }
    }

    private void updateStatus(int status) {
        mStatus = status;
    }

    public boolean isCanceled(){
        if (call == null) {
           return true;
        }

        return  call.isCanceled();
    }
}
