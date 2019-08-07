package com.pengxh.app.weatherplus.callback.impl;

import android.util.Log;

import com.pengxh.app.weatherplus.bean.AllCityBean;
import com.pengxh.app.weatherplus.callback.HotCityListCallback;

import java.util.ArrayList;

public class HotCityListImpl implements HotCityListCallback {

    private static final String TAG = "HotCityListImpl";
    private static volatile ArrayList<AllCityBean> cityBeanList;

    public HotCityListImpl() {
        if (cityBeanList == null) {
            synchronized (HotCityListImpl.class) {
                if (cityBeanList == null) {
                    cityBeanList = new ArrayList<>();
                }
            }
        }
    }

    @Override
    public void addHotCity(AllCityBean cityBean) {
        if (cityBean != null) {
            cityBeanList.add(cityBean);
            //还需要存本地一份，存文件
        } else {
            Log.e(TAG, "addHotCity: cityBean is null", new Throwable());
        }
    }

    @Override
    public ArrayList<AllCityBean> getAllHotCity() {
        return cityBeanList;
    }
}
