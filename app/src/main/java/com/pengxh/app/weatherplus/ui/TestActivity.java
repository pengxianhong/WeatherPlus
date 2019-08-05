package com.pengxh.app.weatherplus.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.weatherplus.R;
import com.pengxh.app.weatherplus.ui.fragment.WeatherFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class TestActivity extends BaseNormalActivity {

    private static final String TAG = "TestActivity";

    @BindView(R.id.mTestViewPager)
    ViewPager mTestViewPager;

    private List<Fragment> fragmentList = new ArrayList<>();

    @Override
    public void initView() {
        setContentView(R.layout.activity_test);
    }

    @Override
    public void init() {
        fragmentList.add(new WeatherFragment());
    }

    @Override
    public void initEvent() {
        FragmentPagerAdapter adapter = new WeatherPageAdapter(getSupportFragmentManager(), fragmentList);
        mTestViewPager.setAdapter(adapter);
        mTestViewPager.setOnPageChangeListener(new WeatherPageChangeListener());
    }

    class WeatherPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {

        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    }

    class WeatherPageAdapter extends FragmentPagerAdapter {
        private List<Fragment> pageList;

        WeatherPageAdapter(FragmentManager fm, List<Fragment> pageList) {
            super(fm);
            this.pageList = pageList;
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
}
