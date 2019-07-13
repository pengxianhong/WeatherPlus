package com.pengxh.app.weatherplus.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Administrator on 2019/7/13.
 */

@Entity
public class CityManagerBean {
    //不能用int
    @Id(autoincrement = true)
    private Long id;

    private String city;
    private String quality;
    private String color;
    private String img;
    private String weather;
    private String templow;
    private String temphigh;
    @Generated(hash = 899007673)
    public CityManagerBean(Long id, String city, String quality, String color,
            String img, String weather, String templow, String temphigh) {
        this.id = id;
        this.city = city;
        this.quality = quality;
        this.color = color;
        this.img = img;
        this.weather = weather;
        this.templow = templow;
        this.temphigh = temphigh;
    }
    @Generated(hash = 916439494)
    public CityManagerBean() {
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
    public String getQuality() {
        return this.quality;
    }
    public void setQuality(String quality) {
        this.quality = quality;
    }
    public String getColor() {
        return this.color;
    }
    public void setColor(String color) {
        this.color = color;
    }
    public String getImg() {
        return this.img;
    }
    public void setImg(String img) {
        this.img = img;
    }
    public String getWeather() {
        return this.weather;
    }
    public void setWeather(String weather) {
        this.weather = weather;
    }
    public String getTemplow() {
        return this.templow;
    }
    public void setTemplow(String templow) {
        this.templow = templow;
    }
    public String getTemphigh() {
        return this.temphigh;
    }
    public void setTemphigh(String temphigh) {
        this.temphigh = temphigh;
    }
}
