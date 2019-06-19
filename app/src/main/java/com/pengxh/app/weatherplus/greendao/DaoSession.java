package com.pengxh.app.weatherplus.greendao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.pengxh.app.weatherplus.bean.CityDaoBean;

import com.pengxh.app.weatherplus.greendao.CityDaoBeanDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig cityDaoBeanDaoConfig;

    private final CityDaoBeanDao cityDaoBeanDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        cityDaoBeanDaoConfig = daoConfigMap.get(CityDaoBeanDao.class).clone();
        cityDaoBeanDaoConfig.initIdentityScope(type);

        cityDaoBeanDao = new CityDaoBeanDao(cityDaoBeanDaoConfig, this);

        registerDao(CityDaoBean.class, cityDaoBeanDao);
    }
    
    public void clear() {
        cityDaoBeanDaoConfig.clearIdentityScope();
    }

    public CityDaoBeanDao getCityDaoBeanDao() {
        return cityDaoBeanDao;
    }

}
