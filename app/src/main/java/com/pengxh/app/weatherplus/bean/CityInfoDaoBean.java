package com.pengxh.app.weatherplus.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class CityInfoDaoBean {
    //不能用int
    @Id(autoincrement = true)
    private Long id;
    private String city;
    private String cityid;
    private String citycode;

    @Generated(hash = 1788724895)
    public CityInfoDaoBean(Long id, String city, String cityid, String citycode) {
        this.id = id;
        this.city = city;
        this.cityid = cityid;
        this.citycode = citycode;
    }

    @Generated(hash = 543648079)
    public CityInfoDaoBean() {
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

    public String getCityid() {
        return this.cityid;
    }

    public void setCityid(String cityid) {
        this.cityid = cityid;
    }

    public String getCitycode() {
        return this.citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }

}