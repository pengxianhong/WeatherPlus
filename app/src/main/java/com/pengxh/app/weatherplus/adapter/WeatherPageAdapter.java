package com.pengxh.app.weatherplus.adapter;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/5/18 16:50
 */
public class WeatherPageAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragmentList = new ArrayList<>();

    public WeatherPageAdapter(FragmentManager fm) {
        super(fm);
    }

    public List<Fragment> getFragmentList() {
        return mFragmentList;
    }

    public void addPage(int index, Fragment fragment) {
        mFragmentList.add(index, fragment);
        notifyDataSetChanged();
    }

    public void addPage(Fragment fragment) {
        mFragmentList.add(fragment);
        notifyDataSetChanged();
    }

    public void delPage(int index) {
        mFragmentList.remove(index);
        notifyDataSetChanged();
    }

    public void delPage(Fragment fragment) {
        mFragmentList.remove(fragment);
        notifyDataSetChanged();
    }

    public void updatePage(List<Fragment> fragmentList) {
        mFragmentList.clear();
        mFragmentList.addAll(fragmentList);
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    /**
     * 返回值有三种，
     * POSITION_UNCHANGED  默认值，位置没有改变
     * POSITION_NONE       item已经不存在
     * position            item新的位置
     * 当position发生改变时这个方法应该返回改变后的位置，以便页面刷新。
     */
    @Override
    public int getItemPosition(Object object) {
        if (object instanceof Fragment) {
            int index = mFragmentList.indexOf(object);
            if (index != -1) {
                return index;
            } else {
                return POSITION_NONE;
            }

        }
        return super.getItemPosition(object);
    }

    @Override
    public long getItemId(int position) {
        return mFragmentList.get(position).hashCode();
    }
}
