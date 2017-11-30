package com.example.ponycui_home.svgaplayer;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGADynamicEntity;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;

/**
 * Created by cuiminghui on 2017/3/30.
 * 将 svga 文件打包到 assets 文件夹中，然后使用 layout.xml 加载动画。
 */

public class SimpleActivity extends Activity {
    private static final String TAG = "SimpleActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        final SVGAImageView svgaImageView = findViewById(R.id.svga_image_view);
        SVGAParser parser = new SVGAParser(this);
        parser.parse("svga_effect_in_change_2.svga", new SVGAParser.ParseCompletion() {
            @Override
            public void onComplete(@NonNull SVGAVideoEntity videoItem) {
                setDrawable(svgaImageView, videoItem);
            }

            @Override
            public void onError(@NonNull Exception e) {
                Log.d(TAG, "onError() called with: e = [" + e + "]");
            }
        });
    }

    private void setDrawable(SVGAImageView svgaImageView, SVGAVideoEntity videoItem) {
        SVGADynamicEntity dynamicEntity = new SVGADynamicEntity();
        dynamicEntity.setDynamicImage(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher), "Bitmap1");
        SVGADrawable drawable = new SVGADrawable(videoItem, dynamicEntity);
        svgaImageView.setImageDrawable(drawable);
        svgaImageView.startAnimation();
    }

}
