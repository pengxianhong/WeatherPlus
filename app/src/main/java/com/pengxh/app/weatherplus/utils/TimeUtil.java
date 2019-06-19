package com.pengxh.app.weatherplus.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2019/5/31.
 */

public class TimeUtil {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

    /**
     * 计算时间差，返回时间戳
     */
    public static long getDuration(String startTime, String endTtime) {
        return (getTimestamp(endTtime) - getTimestamp(startTime));
    }

    /**
     * 转换时间戳为时间
     */
    public static String rTimestamp(long currentTimeMillis) {
        return dateFormat.format(new Date(currentTimeMillis));
    }

    /**
     * 转换时间为时间戳
     */
    public static long getTimestamp(String time) {
        long timestamp = 0L;
        try {
            timestamp = dateFormat.parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timestamp;
    }
}
