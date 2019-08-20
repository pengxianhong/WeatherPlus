package com.pengxh.app.weatherplus.utils;

import com.pengxh.app.weatherplus.BaseApplication;
import com.pengxh.app.weatherplus.bean.AllCityBean;
import com.pengxh.app.weatherplus.bean.CityNameBean;
import com.pengxh.app.weatherplus.greendao.AllCityBeanDao;

import java.util.List;

public class GreenDaoUtil {
    /**
     * 初次启动app，将全国城市存入本地数据库
     */
    public static void saveCityToSQL(String cityid, String parentid, String citycode, String city) {
        AllCityBean allCityBean = new AllCityBean();
        allCityBean.setCityid(cityid);
        allCityBean.setParentid(parentid);
        allCityBean.setCitycode(citycode);
        allCityBean.setCity(city);
        BaseApplication.getDaoInstant().getAllCityBeanDao().insert(allCityBean);
    }

    /**
     * 查询某个城市的具体信息
     * 返回List
     */
    public static List<AllCityBean> queryCity(String city) {
        AllCityBeanDao allCityBeanDao = BaseApplication.getDaoInstant().getAllCityBeanDao();
        return allCityBeanDao.queryBuilder().where(AllCityBeanDao.Properties.City.eq(city)).list();
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