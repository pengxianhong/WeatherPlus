package com.pengxh.app.weatherplus.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by Administrator on 2019/6/30.
 */

@Entity
public class CityNameBean {
    //不能用int
    @Id(autoincrement = true)
    private Long id;
    private String city;

    @Generated(hash = 323073614)
    public CityNameBean(Long id, String city) {
        this.id = id;
        this.city = city;
    }

    @Generated(hash = 1665663809)
    public CityNameBean() {
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