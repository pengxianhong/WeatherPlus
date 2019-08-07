package com.pengxh.app.weatherplus.utils;

import com.pengxh.app.weatherplus.BaseApplication;
import com.pengxh.app.weatherplus.bean.AllCityBean;
import com.pengxh.app.weatherplus.bean.CityManagerBean;
import com.pengxh.app.weatherplus.bean.CityNameBean;
import com.pengxh.app.weatherplus.greendao.AllCityBeanDao;
import com.pengxh.app.weatherplus.greendao.CityManagerBeanDao;

import java.util.List;

public class GreenDaoUtil {

    private static final String TAG = "GreenDaoUtil";

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

    /**
     * 保存简单的天气信息
     */
    public static void saveSimpleWeather(String city, String quality, String color, String img, String weather, String templow, String temphigh) {
        CityManagerBeanDao dao = BaseApplication.getDaoInstant().getCityManagerBeanDao();
        CityManagerBean managerDaoBean = new CityManagerBean();
        managerDaoBean.setCity(city);
        managerDaoBean.setQuality(quality);
        managerDaoBean.setColor(color);
        managerDaoBean.setImg(img);
        managerDaoBean.setWeather(weather);
        managerDaoBean.setTemplow(templow);
        managerDaoBean.setTemphigh(temphigh);
        //插入数据，传入的对象主键如果存在于数据库中，有则更新，否则插入
        if (isWeatherExist(city)) {
            //dao.update(managerDaoBean);
        } else {
            dao.insert(managerDaoBean);
        }
    }

    private static boolean isWeatherExist(String city) {
        CityManagerBeanDao dao = BaseApplication.getDaoInstant().getCityManagerBeanDao();
        List<CityManagerBean> list = dao.queryBuilder().where(CityManagerBeanDao.Properties.City.eq(city)).list();
        if (list.size() == 0) {
            return false;
        }
        return true;
    }

    /**
     * 加载其他城市的天气信息
     */
    public static List<CityManagerBean> loadOtherCityWeather() {
        return BaseApplication.getDaoInstant().getCityManagerBeanDao().loadAll();
    }

    /**
     * 删除某一个城市天气
     */
    public static void deleteCity(CityManagerBean cityManagerBean) {
        BaseApplication.getDaoInstant().getCityManagerBeanDao().delete(cityManagerBean);
    }
}