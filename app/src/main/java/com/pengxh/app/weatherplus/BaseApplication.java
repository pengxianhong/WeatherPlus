package com.pengxh.app.weatherplus;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.pengxh.app.multilib.utils.ToastUtil;
import com.pengxh.app.weatherplus.greendao.DaoMaster;
import com.pengxh.app.weatherplus.greendao.DaoSession;
import com.pengxh.app.weatherplus.utils.SQLiteUtil;
import com.pengxh.app.weatherplus.utils.SaveKeyValues;

public class BaseApplication extends Application {

    private static DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        ToastUtil.init(this);
        SaveKeyValues.init(this);
        SQLiteUtil.initDataBase(this);
        setupDatabase();//配置greendao
    }

    /**
     * 配置数据库
     */
    private void setupDatabase() {
        DaoMaster.DevOpenHelper dbHelper = new DaoMaster.DevOpenHelper(this, "city.db", null);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //获取数据库对象
        DaoMaster daoMaster = new DaoMaster(db);
        //获取Dao对象管理者
        daoSession = daoMaster.newSession();
    }

    public static DaoSession getDaoInstant() {
        return daoSession;
    }
}
