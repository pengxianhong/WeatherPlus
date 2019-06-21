package com.pengxh.app.weatherplus.utils;

import android.util.Log;

import com.pengxh.app.weatherplus.BaseApplication;
import com.pengxh.app.weatherplus.bean.CityDaoBean;
import com.pengxh.app.weatherplus.greendao.CityDaoBeanDao;

import java.util.List;

public class GreenDaoUtil {

    public static void saveCityToSQL(String cityid, String parentid, String citycode, String city) {
        CityDaoBean cityDaoBean = new CityDaoBean();
        cityDaoBean.setCityid(cityid);
        cityDaoBean.setParentid(parentid);
        cityDaoBean.setCitycode(citycode);
        cityDaoBean.setCity(city);
        BaseApplication.getDaoInstant().getCityDaoBeanDao().insert(cityDaoBean);
    }

    public static String queryCity(String city) {
        CityDaoBeanDao cityDaoBeanDao = BaseApplication.getDaoInstant().getCityDaoBeanDao();
        List<CityDaoBean> list = cityDaoBeanDao.queryBuilder().where(CityDaoBeanDao.Properties.City.eq(city)).list();
        Log.d("GreenDaoUtil", list.toString());
        return "";
    }
}