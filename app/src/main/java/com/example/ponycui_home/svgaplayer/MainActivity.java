package com.example.ponycui_home.svgaplayer;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGADynamicEntity;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by cuiminghui on 2017/3/30.
 * 这是最复杂的一个 Sample， 演示了从网络加载动画，并播放动画。
 * 更多的 Sample 可以在这里找到 https://github.com/yyued/SVGA-Samples
 */

public class MainActivity extends AppCompatActivity {

    SVGAImageView testView = null;
    SVGADynamicEntity dynamicItem = new SVGADynamicEntity();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        testView = new SVGAImageView(this);
        testView.setBackgroundColor(Color.GRAY);
        loadAnimation();
        setContentView(testView);
    }

    private void loadAnimation() {
        SVGAParser parser = new SVGAParser(this);
        resetDownloader(parser);
//        String url = "http://c.cotton.netease.com/buckets/4NhQWd/files/QbyhJxp";
//        String url = "https://github.com/yyued/SVGA-Samples/blob/master/angel.svga?raw=true";
        String url = "http://c.cotton.netease.com/buckets/4NhQWd/files/QcAfUcK";

        try {
            parser.parse(new URL(url), new SVGAParser.ParseCompletion() {
                @Override
                public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                    SVGADynamicEntity entity = new SVGADynamicEntity();
                    SVGADrawable drawable = new SVGADrawable(videoItem, entity);
                    testView.setImageDrawable(drawable);
                    testView.startAnimation();
                }
                @Override
                public void onError(Exception e) {
                    Log.e("TAG", "hehe", e);
                }
            });
        } catch (Exception e) {
            Log.e("TAG", "hehe", e);
        }
    }

    /**
     * 设置下载器，这是一个可选的配置项。
     * @param parser
     */
    private void resetDownloader(SVGAParser parser) {
        parser.setFileDownloader(new SVGAParser.FileDownloader() {
            @Override
            public void resume(final URL url, final Function1<? super InputStream, Unit> complete, final Function1<? super Exception, Unit> failure) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder().url(url).get().build();
                        try {
                            Response response = client.newCall(request).execute();
                            complete.invoke(response.body().byteStream());
                        } catch (IOException e) {
                            e.printStackTrace();
                            failure.invoke(e);
                        }
                    }
                }).start();
            }
        });
    }

}
