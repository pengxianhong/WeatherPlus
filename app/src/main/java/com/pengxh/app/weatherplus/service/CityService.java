package com.pengxh.app.weatherplus.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.pengxh.app.weatherplus.bean.CityBean;
import com.pengxh.app.weatherplus.utils.GreenDaoUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class CityService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String fromAssets = getStringFromAssets(getApplicationContext(), "city.json");
                CityBean cityBean = JSONObject.parseObject(fromAssets, CityBean.class);
                List<CityBean.ResultBeanX.ResultBean> result = cityBean.getResult().getResult();
                for (int i = 0; i < result.size(); i++) {
                    String cityid = String.valueOf(result.get(i).getCityid());
                    String parentid = String.valueOf(result.get(i).getParentid());
                    String citycode = result.get(i).getCitycode();
                    String city = result.get(i).getCity();

                    GreenDaoUtil.saveCityToSQL(cityid, parentid, citycode, city);
                }
            }
        }).start();
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

    private String getStringFromAssets(Context context, String filename) {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    assetManager.open(filename)));
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}