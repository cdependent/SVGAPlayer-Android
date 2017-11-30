package selfdriven.ku.ccdemo;

import android.app.Application;

import com.netease.cc.common.okhttp.OkHttpUtils;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

/**
 * Created by zhengxinwei@N3072 on 2017/11/18.
 */

public class AppContext extends Application {

    public static AppContext sAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            initOkHttpClient();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        sAppContext = this;
    }

    private void initOkHttpClient() throws NoSuchAlgorithmException, KeyManagementException {
        // Create a trust manager that does not validate certificate chains
        final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }
        }};
        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new SecureRandom());
        OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(20000L, TimeUnit.MILLISECONDS).readTimeout(20000L, TimeUnit.MILLISECONDS).writeTimeout(20000L, TimeUnit.MILLISECONDS).sslSocketFactory(sslContext.getSocketFactory(), new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }
        })
                //                .addInterceptor(new UserAgentHeaderInterceptor(this))//UA拦截器
                //                .addInterceptor(new CacheControlHeaderInterceptor())//CacheControl拦截器
                .build();
        okHttpClient.dispatcher().setMaxRequests(5);//设置最大并发请求数量10 默认64
        okHttpClient.dispatcher().setMaxRequestsPerHost(3);//设置同一个域名的最大并发请求 默认为5个
        OkHttpUtils.initClient(okHttpClient);
    }

    public static AppContext getApplication() {
        return sAppContext;
    }
}
