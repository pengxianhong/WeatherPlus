package com.pengxh.app.weatherplus.utils;

import android.util.Log;

import com.pengxh.app.weatherplus.listener.HttpCallbackListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @description: TODO
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/5/1 21:46
 */
public class HttpUtil {
    private static final String TAG = "HttpUtil";

    public static void sendHttpRequest(final String requestUrl, final HttpCallbackListener listener) {
        Observable.create((Observable.OnSubscribe<Response>) subscriber -> new OkHttpClient().newCall(new Request.Builder().url(requestUrl).get().build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, IOException e) {
                subscriber.onError(e);
            }

            @Override
            public void onResponse(@NotNull Call call, Response response) {
                subscriber.onNext(response);
            }
        })).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Response>() {
            @Override
            public void onError(Throwable e) {
                listener.onError(e);
            }

            @Override
            public void onNext(Response response) {
                if (response == null) {
                    Log.e(TAG, "请求出错: ", new NullPointerException());
                    return;
                }
                try {
                    listener.onFinish(response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCompleted() {
                Log.d(TAG, "onCompleted: 请求完成");
            }
        });
    }
}
