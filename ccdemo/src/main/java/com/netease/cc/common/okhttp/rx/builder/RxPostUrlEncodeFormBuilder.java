package com.netease.cc.common.okhttp.rx.builder;

import com.netease.cc.common.okhttp.builders.OkHttpRequestBuilder;
import com.netease.cc.common.okhttp.rx.RxRequestCall;
import com.netease.cc.common.okhttp.rx.request.RxPostUrlEncodedFormRequest;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by n5320 on 2016/9/27.
 */
public class RxPostUrlEncodeFormBuilder extends OkHttpRequestBuilder<RxPostUrlEncodeFormBuilder> {
    private Map<String, String> formKeyValues;


    public RxPostUrlEncodeFormBuilder content(Map<String, String> keyValues) {
        formKeyValues = keyValues;
        return this;
    }

    @Override
    public RxRequestCall build() {
        byte[] bytes = getFormValueBytes();
        return new RxPostUrlEncodedFormRequest(url, tag, params, headers, id, bytes).build();
    }

    private byte[] getFormValueBytes() {
        if (formKeyValues == null) {
            return null;
        }
        List<NameValuePair> pairs = new ArrayList<>();

        for (Map.Entry<String, String> entry : formKeyValues.entrySet()) {
            pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        try {
            UrlEncodedFormEntity encodedFormEntity = new UrlEncodedFormEntity(pairs, "UTF-8");
            byte[] bytes = new byte[(int) encodedFormEntity.getContentLength()];
            encodedFormEntity.getContent().read(bytes);
            return bytes;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
