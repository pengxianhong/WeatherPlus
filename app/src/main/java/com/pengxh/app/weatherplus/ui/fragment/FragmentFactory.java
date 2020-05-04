package com.pengxh.app.weatherplus.ui.fragment;

import android.util.Log;

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
    private static final String TAG = "FragmentFactory";
    private static Map<Integer, BaseFragment> mFragmentCache = new HashMap<>();

    public static Fragment createFragment(int position) {
        Log.d(TAG, "createFragment: " + position);
        BaseFragment fragment = mFragmentCache.get(position);
        if (fragment != null) {
            return fragment;
        }
        fragment = new WeatherFragment();
        mFragmentCache.put(position, fragment);
        return fragment;
    }
}
