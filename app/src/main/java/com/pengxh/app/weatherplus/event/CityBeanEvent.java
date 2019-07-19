package com.pengxh.app.weatherplus.event;

import com.pengxh.app.weatherplus.bean.AllCityBean;

public class CityBeanEvent {

    private AllCityBean allCityBean;

    public CityBeanEvent(AllCityBean allCityBean) {
        this.allCityBean = allCityBean;
    }

    public AllCityBean getAllCityBean() {
        return allCityBean;
    }
}
