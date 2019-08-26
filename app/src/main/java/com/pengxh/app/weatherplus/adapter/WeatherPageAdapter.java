package com.pengxh.app.weatherplus.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class WeatherPageAdapter extends FragmentPagerAdapter {
    private List<Fragment> pageList;

    public WeatherPageAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.pageList = list;
    }

    @Override
    public Fragment getItem(int position) {
        return pageList.get(position);
    }

    @Override
    public int getCount() {
        return pageList.size();
    }
}
