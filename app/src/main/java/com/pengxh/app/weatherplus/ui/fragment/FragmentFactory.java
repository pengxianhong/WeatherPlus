package com.pengxh.app.weatherplus.ui.fragment;

import androidx.fragment.app.Fragment;

import com.pengxh.app.multilib.base.BaseFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: TODO
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/5/3 21:32
 */
public class FragmentFactory {
    public static final int TAB_WEATHER = 0;

    private static Map<Integer, BaseFragment> mFragmentCache = new HashMap<>();

    public static Fragment createFragment(int position) {
        BaseFragment fragment = mFragmentCache.get(position);
        if (fragment != null) {
            return fragment;
        }
        if (position == TAB_WEATHER) {
            fragment = new WeatherFragment();
        }
        mFragmentCache.put(position, fragment);
        return fragment;
    }
}
