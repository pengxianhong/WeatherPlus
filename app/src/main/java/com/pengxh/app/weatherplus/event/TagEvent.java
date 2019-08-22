package com.pengxh.app.weatherplus.event;

/**
 * Created by Administrator on 2019/8/21.
 */

public class TagEvent {

    private String className;
    private int msg;

    public TagEvent(String className, int msg) {
        this.className = className;
        this.msg = msg;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getMsg() {
        return msg;
    }

    public void setMsg(int msg) {
        this.msg = msg;
    }
}
