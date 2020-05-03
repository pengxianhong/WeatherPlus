package com.pengxh.app.weatherplus.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pengxh.app.weatherplus.bean.CityInfoBean;
import com.pengxh.app.weatherplus.bean.CityListWeatherBean;
import com.pengxh.app.weatherplus.bean.HotCityBean;

import java.util.ArrayList;
import java.util.List;

@SuppressLint({"StaticFieldLeak"})
public class SQLiteUtil {
    private static final String TAG = "SQLiteUtil";
    private static Context context;
    /**
     * 数据库名
     */
    private static final String DB_NAME = "WeatherPlus.db";
    /**
     * 数据库表名
     */
    private static final String CITY_INFO_TABLE = "CityInfo";
    private static final String HOT_CITY_TABLE = "HotCity";
    private static final String WEATHER_TABLE = "CityWeather";
    /**
     * 数据库版本
     */
    private static final int VERSION = 1;
    private SQLiteDatabase db;
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
     * 初次启动app，将全国城市详细信息存入本地数据库
     */
    public void saveCityInfoList(String cityid, String parentid, String citycode, String city) {
        ContentValues values = new ContentValues();
        values.put("cityid", cityid);
        values.put("parentid", parentid);
        values.put("citycode", citycode);
        values.put("city", city);
        db.insert(CITY_INFO_TABLE, null, values);
    }

    /**
     * 加载所有城市信息
     */
    private List<CityInfoBean.ResultBeanX.ResultBean> loadAllCityInfo() {
        List<CityInfoBean.ResultBeanX.ResultBean> list = new ArrayList<>();
        Cursor cursor = db.query(CITY_INFO_TABLE, null, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CityInfoBean.ResultBeanX.ResultBean resultBean = new CityInfoBean.ResultBeanX.ResultBean();
            resultBean.setCityid(cursor.getInt(cursor.getColumnIndex("cityid")));
            resultBean.setParentid(cursor.getInt(cursor.getColumnIndex("parentid")));
            resultBean.setCitycode(cursor.getString(cursor.getColumnIndex("citycode")));
            resultBean.setCity(cursor.getString(cursor.getColumnIndex("city")));
            list.add(resultBean);
            //下一次循环开始
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    /**
     * 查询某个城市的详细信息
     */
    public CityInfoBean.ResultBeanX.ResultBean queryCityInfo(String city) {
        CityInfoBean.ResultBeanX.ResultBean resultBean = null;
        Cursor cursor = db.query(CITY_INFO_TABLE, null, "city = ?", new String[]{city}, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            resultBean = new CityInfoBean.ResultBeanX.ResultBean();
            resultBean.setCityid(cursor.getInt(cursor.getColumnIndex("cityid")));
            resultBean.setParentid(cursor.getInt(cursor.getColumnIndex("parentid")));
            resultBean.setCitycode(cursor.getString(cursor.getColumnIndex("citycode")));
            resultBean.setCity(cursor.getString(cursor.getColumnIndex("city")));

            cursor.moveToNext();
        }
        cursor.close();
        return resultBean;
    }

    /**
     * 从数据库加载所有城市名字数组
     */
    public String[] loadAllCityName() {
        String[] cityNameArray;
        List<CityInfoBean.ResultBeanX.ResultBean> list = loadAllCityInfo();
        int size = list.size();
        cityNameArray = new String[size];
        for (int i = 0; i < size; i++) {
            cityNameArray[i] = list.get(i).getCity();
        }
        return cityNameArray;
    }

    /**
     * 保存热门城市名
     */
    public void saveHotCity(String city) {
        if (isCityExist(HOT_CITY_TABLE, city)) {
            Log.d(TAG, city + "已经存在了");
            return;
        }
        ContentValues values = new ContentValues();
        values.put("city", city);
        db.insert(HOT_CITY_TABLE, null, values);
        Log.d(TAG, "热门城市[" + city + "]保存成功");
    }

    /**
     * 加载所有热门城市
     */
    public List<HotCityBean> loadHotCity() {
        List<HotCityBean> list = new ArrayList<>();
        Cursor cursor = db.query(HOT_CITY_TABLE, null, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            HotCityBean resultBean = new HotCityBean();
            resultBean.setCity(cursor.getString(cursor.getColumnIndex("city")));
            list.add(resultBean);

            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    /**
     * 删除所有热门城市
     */
    public void deleteAll() {
        db.delete(HOT_CITY_TABLE, null, null);
    }

    /**
     * 查询
     *
     * @param table         表名
     * @param selectionArgs 需要查询的参数，所有涉及到城市的表尽量都用‘city’作为一列
     */
    private boolean isCityExist(String table, String selectionArgs) {
        boolean result = false;
        Cursor cursor = null;
        try {
            cursor = db.query(table, null, "city = ?", new String[]{selectionArgs}, null, null, null);
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

    /**
     * 保存城市列表天气
     */
    public void saveCityListWeather(String city, String weather) {
        ContentValues values = new ContentValues();
        if (isCityExist(WEATHER_TABLE, city)) {
            values.put("weather", weather);//value为将要替换更改的值
            //将CityWeather里面cityName = city的cityWeather更新
            db.update(WEATHER_TABLE, values, "city = ?", new String[]{city});
            Log.d(TAG, city + "天气数据更新成功");
        } else {
            values.put("city", city);
            values.put("weather", weather);
            db.insert(WEATHER_TABLE, null, values);
        }
    }

    /**
     * 加载所有城市天气
     */
    public List<CityListWeatherBean> loadCityList() {
        List<CityListWeatherBean> list = new ArrayList<>();
        Cursor cursor = db.query(WEATHER_TABLE, null, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CityListWeatherBean resultBean = new CityListWeatherBean();
            resultBean.setCity(cursor.getString(cursor.getColumnIndex("city")));
            resultBean.setWeather(cursor.getString(cursor.getColumnIndex("weather")));
            list.add(resultBean);

            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    /**
     * 删除城市
     */
    public void deleteCityByName(String city) {
        db.delete(WEATHER_TABLE, "city = ?", new String[]{city});
    }
}
