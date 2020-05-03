package com.pengxh.app.weatherplus.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSONObject;
import com.pengxh.app.weatherplus.bean.CityInfoBean;
import com.pengxh.app.weatherplus.listener.HttpCallbackListener;
import com.pengxh.app.weatherplus.utils.Constant;
import com.pengxh.app.weatherplus.utils.HttpUtil;
import com.pengxh.app.weatherplus.utils.SQLiteUtil;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.Response;

public class CityService extends Service {

    private static final String TAG = "CityService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: 下载城市名单");
        SQLiteUtil sqLiteUtil = SQLiteUtil.getInstance();
        HttpUtil.sendHttpRequest(Constant.CITY_URL, new HttpCallbackListener() {
            @Override
            public void onFinish(Response response) throws IOException {
                //Gson解析效率太低，换fastJson
                String responseBody = Objects.requireNonNull(response.body()).string();
                CityInfoBean cityBean = JSONObject.parseObject(responseBody, CityInfoBean.class);
                List<CityInfoBean.ResultBeanX.ResultBean> result = cityBean.getResult().getResult();

                for (int i = 0; i < result.size(); i++) {
                    String cityid = String.valueOf(result.get(i).getCityid());
                    String parentid = String.valueOf(result.get(i).getParentid());
                    String citycode = result.get(i).getCitycode();
                    String city = result.get(i).getCity();
                    if (citycode != null && !citycode.equals("")) {
                        //保存城市详细信息
                        sqLiteUtil.saveCityInfoList(cityid, parentid, citycode, city);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}