package com.pengxh.app.weatherplus.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by Administrator on 2019/6/30.
 */

@Entity
public class CityNameDaoBean {
    //不能用int
    @Id(autoincrement = true)
    private Long id;
    private String city;
    @Generated(hash = 158292730)
    public CityNameDaoBean(Long id, String city) {
        this.id = id;
        this.city = city;
    }
    @Generated(hash = 2046097888)
    public CityNameDaoBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCity() {
        return this.city;
    }
    public void setCity(String city) {
        this.city = city;
    }


}