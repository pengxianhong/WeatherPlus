package com.pengxh.app.weatherplus.utils;

import com.pengxh.app.weatherplus.BaseApplication;
import com.pengxh.app.weatherplus.bean.CityDaoBean;
import com.pengxh.app.weatherplus.bean.CityNameBean;
import com.pengxh.app.weatherplus.greendao.CityDaoBeanDao;

import java.util.List;

public class GreenDaoUtil {

    private static final String TAG = "GreenDaoUtil";

    /**
     * 初次启动app，将全国城市存入本地数据库
     */
    public static void saveCityToSQL(String cityid, String parentid, String citycode, String city) {
        CityDaoBean cityDaoBean = new CityDaoBean();
        cityDaoBean.setCityid(cityid);
        cityDaoBean.setParentid(parentid);
        cityDaoBean.setCitycode(citycode);
        cityDaoBean.setCity(city);
        BaseApplication.getDaoInstant().getCityDaoBeanDao().insert(cityDaoBean);
    }

    /**
     * 查询某个城市的具体信息
     */
    public static List<CityDaoBean> queryCity(String city) {
        CityDaoBeanDao cityDaoBeanDao = BaseApplication.getDaoInstant().getCityDaoBeanDao();
        return cityDaoBeanDao.queryBuilder().where(CityDaoBeanDao.Properties.City.eq(city)).list();
    }

    /**
     * 从数据库加载所有城市信息
     */
    public static List<CityDaoBean> loadAllCity() {
        return BaseApplication.getDaoInstant().getCityDaoBeanDao().loadAll();
    }

    /**
     * 从数据库分离出城市名称
     */
    public static void saveCityNameToSQL(String city) {
        CityNameBean nameBean = new CityNameBean();
        nameBean.setCity(city);
        BaseApplication.getDaoInstant().getCityNameBeanDao().insert(nameBean);
    }

    /**
     * 从数据库加载所有城市信息
     */
    public static List<CityNameBean> loadAllCityName() {
        return BaseApplication.getDaoInstant().getCityNameBeanDao().loadAll();
    }
}