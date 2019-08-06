package com.pengxh.app.weatherplus.event;

import com.pengxh.app.weatherplus.bean.NetWeatherBean;

public class NetWeatherBeanEvent {

    private NetWeatherBean weatherBean;


    public NetWeatherBeanEvent(NetWeatherBean weatherBean) {
        this.weatherBean = weatherBean;
    }

    public NetWeatherBean getWeatherBean() {
        return weatherBean;
    }

    public void setWeatherBean(NetWeatherBean weatherBean) {
        this.weatherBean = weatherBean;
    }
}
