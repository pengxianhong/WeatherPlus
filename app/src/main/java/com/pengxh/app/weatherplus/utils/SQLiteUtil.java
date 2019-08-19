package com.pengxh.app.weatherplus.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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
        if (mCityName != null) {
            ContentValues values = new ContentValues();
            if (isCityExist(mCityName)) {
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
     */
    private boolean isCityExist(String cityName) {
        boolean result = false;
        Cursor cursor = null;
        try {
            cursor = db.query("HotCity", null, "cityName = ?", new String[]{cityName}, null, null, null);
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
}
