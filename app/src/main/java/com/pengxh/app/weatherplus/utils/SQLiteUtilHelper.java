package com.pengxh.app.weatherplus.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteUtilHelper extends SQLiteOpenHelper {
    private static final String TAG = "SQLiteUtilHelper";

    SQLiteUtilHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table HotCity(id integer primary key autoincrement,cityName text)");
        Log.d(TAG, "数据库创建成功");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
