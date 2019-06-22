package com.pengxh.app.weatherplus.utils;

import com.pengxh.app.weatherplus.BaseApplication;
import com.pengxh.app.weatherplus.bean.CityDaoBean;
import com.pengxh.app.weatherplus.greendao.CityDaoBeanDao;

import java.util.List;

public class GreenDaoUtil {

    private static final String TAG = "GreenDaoUtil";

    public static void saveCityToSQL(String cityid, String parentid, String citycode, String city) {
        CityDaoBean cityDaoBean = new CityDaoBean();
        cityDaoBean.setCityid(cityid);
        cityDaoBean.setParentid(parentid);
        cityDaoBean.setCitycode(citycode);
        cityDaoBean.setCity(city);
        BaseApplication.getDaoInstant().getCityDaoBeanDao().insert(cityDaoBean);
    }

    public static List<CityDaoBean> queryCity(String city) {
        CityDaoBeanDao cityDaoBeanDao = BaseApplication.getDaoInstant().getCityDaoBeanDao();
        return cityDaoBeanDao.queryBuilder().where(CityDaoBeanDao.Properties.City.eq(city)).list();
    }
}