package com.pengxh.app.weatherplus.utils;

import com.pengxh.app.weatherplus.R;

public class Constant {
    //retrofit请求baseurl只能是根url，不能带任何参数
    public static final String BASE_URL = "https://way.jd.com/";

    //获取城市列表
    public static final String CITY_URL = BASE_URL + "jisuapi/weather1?appkey=e957ed7ad90436a57e604127d9d8fa32";

    public static final int[] ICONS = {R.drawable.air, R.drawable.sports, R.drawable.sunny
            , R.drawable.headache, R.drawable.car, R.drawable.airpollution
            , R.drawable.yifu};
}
