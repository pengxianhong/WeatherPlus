package com.pengxh.app.weatherplus.event;

public class PagePositionEvent {

    private int position;

    public PagePositionEvent(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
