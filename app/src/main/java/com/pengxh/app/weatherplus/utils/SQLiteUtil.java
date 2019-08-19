package com.pengxh.app.weatherplus.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.pengxh.app.weatherplus.bean.CityListWeatherBean;
import com.pengxh.app.weatherplus.bean.HotCityNameBean;

import java.util.ArrayList;
import java.util.List;

public class SQLiteUtil {
    private static final String TAG = "SQLiteUtil";
    @SuppressLint({"StaticFieldLeak"})
    private static Context context;
    /**
     * 数据库名
     */
    private static final String DB_NAME = "WeatherPlusDB";
    /**
     * 数据库版本
     */
    private static final int VERSION = 1;
    private SQLiteDatabase db;
    @SuppressLint("StaticFieldLeak")
    private static SQLiteUtil sqLiteUtil = null;

    public static void initDataBase(Context mContext) {
        context = mContext.getApplicationContext();
    }

    private SQLiteUtil() {
        SQLiteUtilHelper mSqLiteUtilHelper = new SQLiteUtilHelper(context, DB_NAME, null, VERSION);
        db = mSqLiteUtilHelper.getWritableDatabase();
    }

    public static SQLiteUtil getInstance() {
        if (null == sqLiteUtil) {
            synchronized (SQLiteUtil.class) {
                if (null == sqLiteUtil) {
                    sqLiteUtil = new SQLiteUtil();
                }
            }
        }
        return sqLiteUtil;
    }

    /**
     * 保存热门城市名
     */
    public void saveHotCity(String mCityName) {
        if (!TextUtils.isEmpty(mCityName)) {
            ContentValues values = new ContentValues();
            if (isCityExist("HotCity", mCityName)) {
                Log.d(TAG, mCityName + "已经存在了");
            } else {
                values.put("cityName", mCityName);
                db.insert("HotCity", null, values);
            }
        }
    }

    /**
     * 删除所有热门城市
     */
    public void deleteAll() {
        db.delete("HotCity", null, null);
    }

    /**
     * 加载所有热门城市
     */
    public List<HotCityNameBean> loadHotCity() {
        List<HotCityNameBean> list = new ArrayList<>();
        Cursor cursor = db
                .query("HotCity", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                HotCityNameBean resultBean = new HotCityNameBean();
                resultBean.setCityName(cursor.getString(cursor.getColumnIndex("cityName")));
                list.add(resultBean);
            } while (cursor.moveToNext());
        }
        return list;
    }

    /**
     * 查询
     *
     * @param table         表名
     * @param selectionArgs 需要查询的参数，所有涉及到城市的表尽量都用‘cityName’作为一列
     */
    private boolean isCityExist(String table, String selectionArgs) {
        boolean result = false;
        Cursor cursor = null;
        try {
            cursor = db.query(table, null, "cityName = ?", new String[]{selectionArgs}, null, null, null);
            result = null != cursor && cursor.moveToFirst();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return result;
    }

    /***********************************分割线**********************************************/

    /**
     * 保存城市列表天气
     */
    public void saveCityListWeather(String city, String weather) {
        if (!TextUtils.isEmpty(city)) {
            ContentValues values = new ContentValues();
            values.put("cityName", city);
            values.put("cityWeather", weather);
            if (isCityExist("CityWeather", city)) {
                db.update("CityWeather", values, "cityWeather = ?", new String[]{weather});
                Log.d(TAG, "更新数据");
            } else {
                db.insert("CityWeather", null, values);
            }
        }
    }

    /**
     * 加载所有城市天气
     */
    public List<CityListWeatherBean> loadCityList() {
        List<CityListWeatherBean> list = new ArrayList<>();
        Cursor cursor = db
                .query("CityWeather", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                CityListWeatherBean resultBean = new CityListWeatherBean();
                resultBean.setCityName(cursor.getString(cursor.getColumnIndex("cityName")));
                resultBean.setWeather(cursor.getString(cursor.getColumnIndex("cityWeather")));
                list.add(resultBean);
            } while (cursor.moveToNext());
        }
        return list;
    }

    /**
     * 删除城市
     */
    public void deleteCityByName(String city) {
        db.delete("CityWeather", "cityWeather = ?", new String[]{city});
    }
}
