package com.pengxh.app.weatherplus;

import android.app.Application;

import androidx.multidex.MultiDex;

import com.pengxh.app.multilib.utils.SaveKeyValues;
import com.pengxh.app.multilib.widget.EasyToast;
import com.pengxh.app.weatherplus.utils.SQLiteUtil;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        EasyToast.init(this);
        MultiDex.install(this);
        SQLiteUtil.initDataBase(this);
        SaveKeyValues.initSharedPreferences(this);
    }
}
