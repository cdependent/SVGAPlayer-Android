package com.netease.cc.common.okhttp.rx;

import com.netease.cc.common.okhttp.rx.adapter.CallAdapter;

import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;
import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.Exceptions;
import rx.schedulers.Schedulers;

/**
 * Created by tantai on 2016/10/14.
 * Rx适配器
 * 参考https://github.com/jeasonlzy/okhttp-OkGo
 */
public class RxAdapter<T> implements CallAdapter<Observable<T>> {

    public static <T> RxAdapter<T> create() {
        return RxAdapter.ConvertHolder.convert;
    }

    private static class ConvertHolder {
        private static RxAdapter convert = new RxAdapter();
    }

    @Override
    public <R> Observable<T> adapt(RxRequestCall<R> call) {
        return Observable.create(new CallOnSubscribe<>((RxRequestCall<T>) call)) //强转,本质，T 与 R 是同一个泛型
                .subscribeOn(Schedulers.io())   //IO线程订阅网络请求
                //感觉用上面的map操作也可以完成,但是Retrofit是这么实现的,目前并不清楚具体好处在哪
                .lift(OperatorMapResponseToBodyOrError.<T>instance());
    }

    private static final class CallOnSubscribe<T> implements Observable.OnSubscribe<Response<T>> {
        private final RxRequestCall<T> originalCall;

        CallOnSubscribe(RxRequestCall<T> originalCall) {
            this.originalCall = originalCall;
        }

        @Override
        public void call(final Subscriber<? super Response<T>> subscriber) {
            // Since Call is a one-shot type, clone it for each new subscriber.
            RxRequestCall<T> call = originalCall.clone();

            // Wrap the call in a helper which handles both unsubscription and backpressure.
            RequestArbiter<T> requestArbiter = new RequestArbiter<>(call, subscriber);
            subscriber.add(requestArbiter);
            subscriber.setProducer(requestArbiter);
        }
    }

    private static final class RequestArbiter<T> extends AtomicBoolean implements Subscription, Producer {
        private final RxRequestCall<T> call;
        private final Subscriber<? super Response<T>> subscriber;

        RequestArbiter(RxRequestCall<T> call, Subscriber<? super Response<T>> subscriber) {
            this.call = call;
            this.subscriber = subscriber;
        }

        /**
         * 生产事件,将同步请求转化为Rx的事件
         */
        @Override
        public void request(long n) {
            if (n < 0) throw new IllegalArgumentException("n < 0: " + n);
            if (n == 0) return; // Nothing to do when requesting 0.
            if (!compareAndSet(false, true)) return; // Request was already triggered.

            try {
                Response<T> response = call.execute();
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(response);
                }
            } catch (Throwable t) {
                Exceptions.throwIfFatal(t);
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onError(t);
                }
                return;
            }

            if (!subscriber.isUnsubscribed()) {
                subscriber.onCompleted();
            }
        }

        @Override
        public void unsubscribe() {
            call.cancel();
        }

        @Override
        public boolean isUnsubscribed() {
            return call.isCanceled();
        }
    }
}