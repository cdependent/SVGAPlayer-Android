package com.netease.cc.common.okhttp;

import android.os.Handler;
import android.os.Looper;

import com.netease.cc.common.okhttp.builders.GetBuilder;
import com.netease.cc.common.okhttp.builders.PostFileBuilder;
import com.netease.cc.common.okhttp.builders.PostMultipartFileBuilder;
import com.netease.cc.common.okhttp.builders.PostStringBuilder;
import com.netease.cc.common.okhttp.builders.PostUrlEncodeFormBuilder;
import com.netease.cc.common.okhttp.callbacks.OkCallBack;
import com.netease.cc.common.okhttp.requests.RequestCall;
import com.netease.cc.common.okhttp.rx.builder.RxGetBuilder;
import com.netease.cc.common.okhttp.rx.builder.RxPostUrlEncodeFormBuilder;
import com.netease.cc.common.okhttp.utils.HttpException;
import com.netease.cc.common.okhttp.utils.IOkHttpErrorWatchDog;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;


/**
 * Created by n5320 on 2016/8/23.
 */
public class OkHttpUtils {
    private static final int INNER_ERROR_CODE_EXCEPTION = -1;
    private static final int INNER_ERROR_CODE_CANCEL = -2;

    public static final long DEFAULT_MILLISECONDS = 20_000L;
    public static final long DEFAULT_DOWNLOAD_FILE_MILLISECONDS = 60_000L;//默认下载文件超时时间
    private volatile static OkHttpUtils mInstance;
    private OkHttpClient mOkHttpClient;
    private Handler mHandler = new Handler(Looper.myLooper());

    private IOkHttpErrorWatchDog mHttpErrorWatchDog;

    public OkHttpUtils(OkHttpClient okHttpClient) {
        if (okHttpClient == null) {
            mOkHttpClient = new OkHttpClient();
        } else {
            mOkHttpClient = okHttpClient;
        }
    }

    public static OkHttpUtils initClient(OkHttpClient okHttpClient) {
        if (mInstance == null) {
            mInstance = new OkHttpUtils(okHttpClient);
        }

        return mInstance;
    }

    /**
     * 添加 网络错误监听
     */
    public void addHttpErrorWatchDog(IOkHttpErrorWatchDog watchDog) {
        this.mHttpErrorWatchDog = watchDog;
    }

    public static OkHttpUtils getInstance() {
        return initClient(null);
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    /**
     * 创建GET 请求方式
     */
    public static GetBuilder get() {
        return new GetBuilder();
    }

    public static RxGetBuilder getByRx() {
        return new RxGetBuilder();
    }

    public static RxPostUrlEncodeFormBuilder postByRx() {
        return new RxPostUrlEncodeFormBuilder();
    }

    public static PostFileBuilder postFile() {
        return new PostFileBuilder();
    }

    public static PostMultipartFileBuilder postMultipartFile() {
        return new PostMultipartFileBuilder();
    }

    public static PostStringBuilder postString() {
        return new PostStringBuilder();
    }

    public static PostUrlEncodeFormBuilder postUrlEncodeForm() {
        return new PostUrlEncodeFormBuilder();
    }

    /**
     * 异步请求 将请求加入请求队列 请求结果异步回调返回
     */
    public void executeAsync(final RequestCall requestCall, OkCallBack callback) {
        if (callback == null) {
            callback = OkCallBack.CALLBACK_DEFAULT;
        }

        final OkCallBack finalCallback = callback;
        final int id = requestCall.getOkHttpRequest().getId();

        requestCall.getCall().enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                String url = "";
                String host = "";
                if (call != null && call.request() != null && call.request().url() != null) {
                    url = call.request().url().toString();
                    host = call.request().header("Host");
                }

                int errorCode = requestCall.isCanceled() ? INNER_ERROR_CODE_CANCEL : INNER_ERROR_CODE_EXCEPTION;
                requestCall.finishRequestCall();
                sendFailResultCallback(id, errorCode, e, url, host, finalCallback, false);
            }

            @Override
            public void onResponse(Call call, final Response response) {
                String url = "";
                String host = "";
                try {
                    requestCall.finishRequestCall();
                    host = call.request().header("Host");

                    if (response != null && response.request() != null && response.request().url() != null) {
                        url = response.request().url().toString();
                    }

                    if (call.isCanceled()) {
                        sendFailResultCallback(id, INNER_ERROR_CODE_CANCEL, new HttpException("Canceled!").withResponse(response), url, host, finalCallback, false);
                        return;
                    }

                    if (!finalCallback.validateResponse(response)) {
                        if (response != null) {
                            HttpException e = new HttpException(String.format(Locale.getDefault(), "request failed, reponse's code is: %d", response.code())).withResponse(response);
                            sendFailResultCallback(id, response.code(), e, url, host, finalCallback, false);
                        }
                        return;
                    }

                    Object o = finalCallback.parseNetworkResponse(response, id);
                    sendSuccessResultCallback(call, response.code(), o, finalCallback, id, false);
                } catch (Throwable e) {
                    sendFailResultCallback(id, INNER_ERROR_CODE_EXCEPTION, new HttpException(e).withResponse(response), url, host, finalCallback, false);
                } finally {
                    try {
                        if (response != null && response.body() != null) {
                            response.body().close();
                        }
                    } catch (Exception ex) {

                    }
                }
            }
        });
    }

    /**
     * 同步执行 请求和回调都在当前线程执行
     */
    public void executeSync(final RequestCall requestCall, OkCallBack callback) {
        if (callback == null) {
            callback = OkCallBack.CALLBACK_DEFAULT;
        }

        final OkCallBack finalCallback = callback;
        final int id = requestCall.getOkHttpRequest().getId();
        Response response = null;
        String host = "";
        try {
            response = requestCall.getCall().execute();
            requestCall.finishRequestCall();

            host = requestCall.getCall().request().header("Host");

            if (requestCall.getCall().isCanceled()) {
                sendFailResultCallback(id, INNER_ERROR_CODE_CANCEL, new HttpException("Canceled!").withResponse(response), requestCall.getUrl(), host, finalCallback, true);
                return;
            }
            if (!finalCallback.validateResponse(response)) {
                sendFailResultCallback(id, response.code(), new HttpException(String.format(Locale.getDefault(), "request failed , response's code is %d", response.code())).withResponse(response), requestCall.getUrl(), host, finalCallback, true);
                return;
            }

            Object o = finalCallback.parseNetworkResponse(response, id);
            sendSuccessResultCallback(requestCall.getCall(), response.code(), o, finalCallback, id, true);
        } catch (Throwable ex) {
            requestCall.finishRequestCall();
            int errorCode = requestCall != null && requestCall.isCanceled() ? INNER_ERROR_CODE_CANCEL : INNER_ERROR_CODE_EXCEPTION;
            sendFailResultCallback(id, errorCode, new HttpException(ex).withResponse(response), requestCall.getUrl(), host, finalCallback, true);
        } finally {
            try {
                if (response != null && response.body() != null) {
                    response.body().close();
                }
            } catch (Exception ex) {

            }
        }
    }

    public void sendFailResultCallback(final int id, final int errorCode, final Exception errorException, final String url, String host, final OkCallBack callback, boolean isCurrentThread) {
        if (errorCode == INNER_ERROR_CODE_CANCEL) {//RequestCall cancel 不做错误上报 以及网络回调
            return;
        }

        if (mHttpErrorWatchDog != null) {
            String errorReason = "";
            if (errorCode < 0) {
                errorReason = errorException == null ? "" : String.format("%s : %s", errorException.getClass().getName(), errorException.getMessage());
            }

            mHttpErrorWatchDog.onError(errorCode, url, errorReason, host == null ? "" : host, errorException);
        }

        if (callback == null) {
            return;
        }

        if (isCurrentThread) {
            callback.onError(errorException, errorCode);
            callback.onAfter(id);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onError(errorException, errorCode);
                    callback.onAfter(id);
                }
            });
        }
    }

    private void sendSuccessResultCallback(final Call call, final int statusCode, final Object object, final OkCallBack callback, final int id, boolean isCurrentThread) {
        if (callback == null) {
            return;
        }

        if (isCurrentThread) {
            callback.onResponse(object, statusCode);
            callback.onAfter(id);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (!call.isCanceled()) {
                        callback.onResponse(object, statusCode);
                        callback.onAfter(id);
                    }
                }
            });
        }
    }
}
