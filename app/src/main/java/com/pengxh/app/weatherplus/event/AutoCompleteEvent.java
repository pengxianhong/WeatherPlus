package com.pengxh.app.weatherplus.event;

import java.util.List;

/**
 * Created by Administrator on 2019/7/2.
 */

public class AutoCompleteEvent {

    private List<String> cities;

    public AutoCompleteEvent(List<String> cities) {
        this.cities = cities;
    }

    public List<String> getCities() {
        return cities;
    }
}
