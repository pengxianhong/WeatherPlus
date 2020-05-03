package com.pengxh.app.weatherplus.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteUtilHelper extends SQLiteOpenHelper {
    private static final String TAG = "SQLiteUtilHelper";
    private static final String SQL_CITY_INFO = "create table CityInfo(id integer primary key autoincrement,cityid text,parentid text,citycode text,city text)";
    private static final String SQL_HOT_CITY = "create table HotCity(id integer primary key autoincrement,city text)";
    private static final String SQL_CITY_WEATHER = "create table CityWeather(id integer primary key autoincrement,city text,weather text)";

    SQLiteUtilHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CITY_INFO);
        db.execSQL(SQL_HOT_CITY);
        db.execSQL(SQL_CITY_WEATHER);
        Log.d(TAG, "数据库创建成功");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
