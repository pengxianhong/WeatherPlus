package com.pengxh.app.weatherplus.utils;

import android.util.Log;

import com.pengxh.app.weatherplus.BaseApplication;
import com.pengxh.app.weatherplus.bean.CityDaoBean;
import com.pengxh.app.weatherplus.bean.CityInfoDaoBean;
import com.pengxh.app.weatherplus.bean.CityNameDaoBean;
import com.pengxh.app.weatherplus.greendao.CityDaoBeanDao;
import com.pengxh.app.weatherplus.greendao.CityInfoDaoBeanDao;

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
     * 也可以返回List
     * <p>
     * unique()返回一个或者零个结果
     */
    public static CityDaoBean queryCity(String city) {
        CityDaoBeanDao cityDaoBeanDao = BaseApplication.getDaoInstant().getCityDaoBeanDao();
        return cityDaoBeanDao.queryBuilder().where(CityDaoBeanDao.Properties.City.eq(city)).unique();
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
        CityNameDaoBean nameBean = new CityNameDaoBean();
        nameBean.setCity(city);
        BaseApplication.getDaoInstant().getCityNameDaoBeanDao().insert(nameBean);
    }

    /**
     * 从数据库加载所有城市信息
     */
    public static List<CityNameDaoBean> loadAllCityName() {
        return BaseApplication.getDaoInstant().getCityNameDaoBeanDao().loadAll();
    }

    /**
     * 保存热门城市信息
     */
    public static void saveToSQL(String cityname, String cityid, String citycode) {
        CityInfoDaoBeanDao cityInfoDaoBeanDao = BaseApplication.getDaoInstant().getCityInfoDaoBeanDao();
        CityInfoDaoBean infoDaoBean = new CityInfoDaoBean();
        infoDaoBean.setCity(cityname);
        infoDaoBean.setCityid(cityid);
        infoDaoBean.setCitycode(citycode);
        if (isExist(cityname)) {
            Log.d(TAG, "saveToSQL: 重复保存" + cityname);
        } else {
            cityInfoDaoBeanDao.insert(infoDaoBean);
        }
    }

    /**
     * 加载热门城市信息
     */
    public static List<CityInfoDaoBean> loadAllHotCity() {
        return BaseApplication.getDaoInstant().getCityInfoDaoBeanDao().loadAll();
    }

    /**
     * 判断字段是否存在
     */
    private static boolean isExist(String city) {
        CityInfoDaoBeanDao dao = BaseApplication.getDaoInstant().getCityInfoDaoBeanDao();
        CityInfoDaoBean cityInfoDaoBean = dao.queryBuilder().where(CityInfoDaoBeanDao.Properties.City.eq(city)).unique();
        if (cityInfoDaoBean == null) {
            return false;
        }
        return true;
    }

    /**
     * 删除热门城市
     */
    public static void deleteHotCity() {
        CityInfoDaoBeanDao dao = BaseApplication.getDaoInstant().getCityInfoDaoBeanDao();
        dao.deleteAll();
    }
}