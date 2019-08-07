package com.pengxh.app.weatherplus.utils;

import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.LinkedList;

public class FragmentListImpl implements FragmentListCallback {

    private static final String TAG = "FragmentListImpl";
    private static volatile LinkedList<Fragment> fragmentList;

    public FragmentListImpl() {
        if (fragmentList == null) {
            synchronized (FragmentListImpl.class) {
                if (fragmentList == null) {
                    //Fragment可能会增删频繁，需要排序，因此使用LinkedList
                    fragmentList = new LinkedList<>();
                }
            }
        }
    }

    @Override
    public void addFragment(Fragment fragment) {
        if (fragment != null) {
            fragmentList.add(fragment);
        } else {
            Log.e(TAG, "addFragment: fragment is null", new Throwable());
        }
    }

    @Override
    public LinkedList<Fragment> getAllFragment() {
        return fragmentList;
    }
}
