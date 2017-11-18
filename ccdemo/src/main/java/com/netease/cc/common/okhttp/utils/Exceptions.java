package com.netease.cc.common.okhttp.utils;

/**
 * Created by n5320 on 2016/8/23.
 */
public class Exceptions {
    public static void illegalArgument(String msg, Object... params) {
        throw new IllegalArgumentException(String.format(msg, params));
    }
}
