package com.pengxh.app.weatherplus.utils;

/**
 * Created by Administrator on 2019/6/19.
 */

public interface HttpCallbackListener {

    void onFinish(String response);

    void onError(Exception e);
}
