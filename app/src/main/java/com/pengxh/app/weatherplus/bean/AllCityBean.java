package com.pengxh.app.weatherplus.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class AllCityBean {
    //不能用int
    @Id(autoincrement = true)
    private Long id;

    private String cityid;
    private String parentid;
    private String citycode;
    private String city;
    @Generated(hash = 648897534)
    public AllCityBean(Long id, String cityid, String parentid, String citycode,
            String city) {
        this.id = id;
        this.cityid = cityid;
        this.parentid = parentid;
        this.citycode = citycode;
        this.city = city;
    }
    @Generated(hash = 871883475)
    public AllCityBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCityid() {
        return this.cityid;
    }
    public void setCityid(String cityid) {
        this.cityid = cityid;
    }
    public String getParentid() {
        return this.parentid;
    }
    public void setParentid(String parentid) {
        this.parentid = parentid;
    }
    public String getCitycode() {
        return this.citycode;
    }
    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }
    public String getCity() {
        return this.city;
    }
    public void setCity(String city) {
        this.city = city;
    }
}