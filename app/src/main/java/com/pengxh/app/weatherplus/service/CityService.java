package com.pengxh.app.weatherplus.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class CityService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

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

//    List<CityBean.ResultBeanX.ResultBean> result = cityBean.getResult().getResult();
//        for (int i = 0; i < result.size(); i++) {
//        String cityid = String.valueOf(result.get(i).getCityid());
//        String parentid = String.valueOf(result.get(i).getParentid());
//        String citycode = result.get(i).getCitycode();
//        String city = result.get(i).getCity();
//
//        GreenDaoUtil.saveCityToSQL(cityid, parentid, citycode, city);
//    }
}
