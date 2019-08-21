package com.pengxh.app.weatherplus.event;

/**
 * Created by Administrator on 2019/8/21.
 */

public class TagEvent {
    private int tag;

    public TagEvent(int tag) {
        this.tag = tag;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }
}
