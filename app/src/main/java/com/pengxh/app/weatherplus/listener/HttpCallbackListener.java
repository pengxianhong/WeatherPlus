package com.pengxh.app.weatherplus.listener;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by Administrator on 2019/6/19.
 */

public interface HttpCallbackListener {

    void onFinish(Response response) throws IOException;

    void onError(Throwable e);
}
