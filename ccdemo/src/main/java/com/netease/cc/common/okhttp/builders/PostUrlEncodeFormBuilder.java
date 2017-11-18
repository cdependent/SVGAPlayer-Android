package com.netease.cc.common.okhttp.builders;

import com.netease.cc.common.okhttp.requests.PostUrlEncodedFormRequest;
import com.netease.cc.common.okhttp.requests.RequestCall;

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
public class PostUrlEncodeFormBuilder extends OkHttpRequestBuilder<PostUrlEncodeFormBuilder> {
    private Map<String, String> formKeyValues;


    public PostUrlEncodeFormBuilder content(Map<String, String> keyValues) {
        formKeyValues = keyValues;
        return this;
    }

    @Override
    public RequestCall build() {
        byte[] bytes = getFormValueBytes();
        return new PostUrlEncodedFormRequest(url, tag, params, headers, id, bytes).build();
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
