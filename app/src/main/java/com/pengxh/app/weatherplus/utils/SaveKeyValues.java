package com.pengxh.app.weatherplus.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class SaveKeyValues {
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    @SuppressLint({"StaticFieldLeak"})
    private static Context context;

    public static void init(Context mContext) {
        context = mContext.getApplicationContext();
    }

    /**
     * 存储
     */
    public static void putValue(String file, String key, Object object) {
        sharedPreferences = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        editor.apply();
    }

    /**
     * 获取保存的数据
     */
    public static Object getValue(String file, String key, Object defaultObject) {
        sharedPreferences = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        if (defaultObject instanceof String) {
            return sharedPreferences.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sharedPreferences.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sharedPreferences.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sharedPreferences.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sharedPreferences.getLong(key, (Long) defaultObject);
        } else {
            return sharedPreferences.getString(key, null);
        }
    }

    /**
     * 移除某个key值已经对应的值
     */
    public static void removeKey(String key) {
        editor.remove(key);
        editor.commit();
    }

    /**
     * 清除所有数据
     */
    public static void clearAll() {
        editor.clear();
        editor.commit();
    }

    /**
     * 查询某个key是否存在
     */
    public static Boolean containsKey(String file, String key) {
        sharedPreferences = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        return sharedPreferences.contains(key);
    }
}