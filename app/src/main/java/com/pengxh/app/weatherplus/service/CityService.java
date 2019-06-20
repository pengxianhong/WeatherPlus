package com.pengxh.app.weatherplus.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.pengxh.app.weatherplus.bean.CityBean;
import com.pengxh.app.weatherplus.utils.Constant;
import com.pengxh.app.weatherplus.utils.GreenDaoUtil;
import com.pengxh.app.weatherplus.utils.HttpCallbackListener;
import com.pengxh.app.weatherplus.utils.OtherUtil;

import java.util.List;

public class CityService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        OtherUtil.sendHttpRequest(Constant.CITY_URL, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                //Gson解析效率太低，换fastJson
                CityBean cityBean = JSONObject.parseObject(response, CityBean.class);
                List<CityBean.ResultBeanX.ResultBean> result = cityBean.getResult().getResult();
                for (int i = 0; i < result.size(); i++) {
                    String cityid = String.valueOf(result.get(i).getCityid());
                    String parentid = String.valueOf(result.get(i).getParentid());
                    String citycode = result.get(i).getCitycode();
                    String city = result.get(i).getCity();

                    GreenDaoUtil.saveCityToSQL(cityid, parentid, citycode, city);
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
        return super.onStartCommand(intent, flags, START_REDELIVER_INTENT);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}