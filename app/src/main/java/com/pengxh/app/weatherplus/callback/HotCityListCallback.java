package com.pengxh.app.weatherplus.callback;

import com.pengxh.app.weatherplus.bean.AllCityBean;

import java.util.ArrayList;

public interface HotCityListCallback {
    /**
     * 将所有HotCity添加进list
     */
    void addHotCity(AllCityBean cityBean);

    /**
     * 将所有HotCity取出
     */
    ArrayList<AllCityBean> getAllHotCity();
}
