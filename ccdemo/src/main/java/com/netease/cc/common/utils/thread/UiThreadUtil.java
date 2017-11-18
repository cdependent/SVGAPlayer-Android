package com.netease.cc.common.utils.thread;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * @Created by n5378 on 2016/12/2.
 * @desc： Utility for interacting with the UI thread.
 */

public class UiThreadUtil {

    private static Handler sMainHandler = new Handler(Looper.getMainLooper());

    /**
     * @return {@code true} if the current thread is the UI thread.
     */
    public static boolean isOnUiThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    /**
     * Runs the given {@code Runnable} on the UI thread.
     */
    public static void runOnUiThread(Runnable runnable) {
        runOnUiThread(runnable, null);
    }

    public static void runOnUiThread(Runnable runnable, Object token){
        runOnUiThreadDelay(runnable, 0, token);
    }


    /**
     * Runs the given {@code Runnable} on the UI thread delay.
     */
    public static void runOnUiThread(Runnable runnable, long delayMillis) {
        runOnUiThreadDelay(runnable, delayMillis, null);
    }

    public static void runOnUiThreadDelay(Runnable runnable, long delayMillis, Object token) {
        if (sMainHandler == null) {
            sMainHandler = new Handler(Looper.getMainLooper());
        }
        Message message = Message.obtain(sMainHandler, runnable);
        if (token != null) {
            message.what = token.hashCode();
        }

        sMainHandler.sendMessageDelayed(message, delayMillis);
    }

    /**
     * Remove any pending posts of Runnable r that are in the message queue.
     */
    public static void removeCallbacks(Runnable runnable) {
        if (runnable == null || sMainHandler == null) {
            return;
        }

        sMainHandler.removeCallbacks(runnable);
    }

    public static void removeCallbacksWithToken(Object token){
        if(token == null || sMainHandler == null){
            return;
        }

        sMainHandler.removeMessages(token.hashCode());
    }
}
