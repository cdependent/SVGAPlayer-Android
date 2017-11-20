package selfdriven.ku.ccdemo;

import android.content.Context;

import com.netease.cc.common.okhttp.OkHttpUtils;
import com.netease.cc.common.okhttp.callbacks.OkCallBack;
import com.opensource.svgaplayer.SVGAParser;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import okhttp3.Response;
import okhttp3.internal.Util;
import rx.Completable;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * SVGA 工具类
 * Created by zhengxinwei@N3072 on 2017/11/18.
 */
public class CCSVGAHelper {

    public static SVGAParser getParser(Context context) {
        SVGAParser parser = new SVGAParser(context);
        parser.setFileDownloader(sFileDownloader);
        return parser;
    }

    private static SVGAParser.FileDownloader sFileDownloader = new SVGAParser.FileDownloader() {
        @Override
        public void resume(@NotNull final URL url, @NotNull final Function1<? super InputStream, Unit> complete, @NotNull final Function1<? super Exception, Unit> failure) {
            if (!loadCache(url, complete, failure)) {
                Logger.INSTANCE.d("load network");
                loadFromNetwork(url, complete, failure);
            }
        }
    };

    private static boolean loadCache(final URL url, final Function1<? super InputStream, Unit> complete, final Function1<? super Exception, Unit> failure) {
        String cacheKey = cacheKey(url);
        final File cacheFile = new File(AppContext.getApplication().getCacheDir() + "/" + cacheKey);
        if (cacheFile.exists()) {
            Logger.INSTANCE.d("load cache:" + cacheKey);
            Completable.fromAction(new Action0() {
                @Override
                public void call() {
                    BufferedInputStream inputStream = null;
                    try {
                        inputStream = new BufferedInputStream(new FileInputStream(cacheFile));
                        complete.invoke(inputStream);
                        Logger.INSTANCE.d("load from cache:" + url);
                    } catch (FileNotFoundException e) {
                        failure.invoke(e);
                    } finally {
                        Util.closeQuietly(inputStream);
                    }
                }
            }).subscribeOn(Schedulers.io()).subscribe();
            return true;
        } else {
            return false;
        }
    }

    private static void loadFromNetwork(final URL url, final Function1<? super InputStream, Unit> complete, final Function1<? super Exception, Unit> failure) {
        OkHttpUtils.get().url(url.toString()).build().execute(new OkCallBack<Void>() {
            @Override
            public Void parseNetworkResponse(Response response, int id) throws Throwable {
                saveSVGACache(url, response.body().byteStream(), complete, failure);
                return null;
            }

            @Override
            public void onError(Exception e, int errorCode) {
                failure.invoke(e);
            }

            @Override
            public void onResponse(Void response, int statusCode) {
            }
        });
    }

    private static void saveSVGACache(URL url, InputStream response, Function1<? super InputStream, Unit> complete, Function1<? super Exception, Unit> failure) {
        String key = cacheKey(url);
        File cacheFile = new File(AppContext.getApplication().getCacheDir() + "/" + key);
        BufferedOutputStream bos = null;
        try {
            if (!cacheFile.exists() && !cacheFile.createNewFile()) {
                return;
            }
            bos = new BufferedOutputStream(new FileOutputStream(cacheFile));
            byte[] buffer = new byte[2048];
            int count;
            while ((count = response.read(buffer)) > 0) {
                bos.write(buffer, 0, count);
            }
            loadCache(url, complete, failure);
        } catch (Exception e) {
            failure.invoke(e);
        } finally {
            Util.closeQuietly(response);
            Util.closeQuietly(bos);
        }
    }

    private static String cacheKey(URL url) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(url.toString().getBytes("UTF-8"));
            byte[] result = digest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : result) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
