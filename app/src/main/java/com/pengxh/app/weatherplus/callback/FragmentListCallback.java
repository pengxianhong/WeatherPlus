package com.pengxh.app.weatherplus.callback;

import android.support.v4.app.Fragment;

import java.util.LinkedList;

public interface FragmentListCallback {

    /**
     * 将所有fragment添加进list
     */
    void addFragment(Fragment fragment);

    /**
     * 将所有fragment取出
     */
    LinkedList<Fragment> getAllFragment();
}
