package com.pengxh.app.weatherplus.mvp.view;


import com.pengxh.app.weatherplus.bean.WeatherBean;

public interface IWeatherView {

    void showProgress();

    void hideProgress();

    /**
     * 显示从后台服务器获取到的数据
     */
    void showNetWorkData(WeatherBean weatherBean);
}
