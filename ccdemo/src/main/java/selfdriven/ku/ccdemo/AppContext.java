package selfdriven.ku.ccdemo;

import android.app.Application;

import com.netease.cc.common.okhttp.OkHttpUtils;
import com.netease.cc.common.okhttp.utils.CacheControlHeaderInterceptor;
import com.netease.cc.common.okhttp.utils.UserAgentHeaderInterceptor;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by zhengxinwei@N3072 on 2017/11/18.
 */

public class AppContext extends Application {

    public static AppContext sAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        initOkHttpClient();
        sAppContext = this;
    }

    private void initOkHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(20000L, TimeUnit.MILLISECONDS)
                .readTimeout(20000L, TimeUnit.MILLISECONDS)
                .writeTimeout(20000L, TimeUnit.MILLISECONDS)
                .addInterceptor(new UserAgentHeaderInterceptor(this))//UA拦截器
                .addInterceptor(new CacheControlHeaderInterceptor())//CacheControl拦截器
                .build();
        okHttpClient.dispatcher().setMaxRequests(5);//设置最大并发请求数量10 默认64
        okHttpClient.dispatcher().setMaxRequestsPerHost(3);//设置同一个域名的最大并发请求 默认为5个
        OkHttpUtils.initClient(okHttpClient);
    }

    public static AppContext getApplication() {
        return sAppContext;
    }
}
