package com.pengxh.app.weatherplus.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.weatherplus.R;
import com.pengxh.app.weatherplus.callback.impl.FragmentListImpl;
import com.pengxh.app.weatherplus.ui.fragment.WeatherFragment;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseNormalActivity {

    private static final String TAG = "MainActivity";

    @BindView(R.id.mMainViewPager)
    ViewPager mMainViewPager;
    @BindView(R.id.mLlIndicator)
    LinearLayout mLlIndicator;
    private LinkedList<Fragment> fragmentLinkedList;


    @Override
    public void initView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void init() {
        FragmentListImpl fragmentManager = new FragmentListImpl();
        fragmentLinkedList = fragmentManager.getAllFragment();
        if (fragmentLinkedList.size() == 0) {
            fragmentManager.addFragment(new WeatherFragment());
            fragmentLinkedList = fragmentManager.getAllFragment();
        }
        Log.d(TAG, "init: fragmentLinkedList.size ===> " + fragmentLinkedList.size());
    }

    @Override
    public void initEvent() {
        FragmentPagerAdapter adapter = new WeatherPageAdapter(getSupportFragmentManager(), fragmentLinkedList);
        mMainViewPager.setAdapter(adapter);
        mMainViewPager.setOnPageChangeListener(new WeatherPageChangeListener(this, mLlIndicator, fragmentLinkedList.size()));
    }

    class WeatherPageChangeListener implements ViewPager.OnPageChangeListener {

        private int mPageCount;//页数
        private List<ImageView> mImgList;//保存img总个数
        private int img_select;
        private int img_unSelect;

        WeatherPageChangeListener(Context context, LinearLayout mLlIndicator, int pageCount) {
            this.mPageCount = pageCount;

            mImgList = new ArrayList<>();
            img_select = R.drawable.dot_enable;
            img_unSelect = R.drawable.dot_disable;

            final int imgSize = 15;

            for (int i = 0; i < mPageCount; i++) {
                ImageView imageView = new ImageView(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                //为小圆点左右添加间距
                params.leftMargin = 8;
                params.rightMargin = 8;
                //给小圆点一个默认大小
                params.height = imgSize;
                params.width = imgSize;
                if (i == 0) {
                    imageView.setBackgroundResource(img_select);
                } else {
                    imageView.setBackgroundResource(img_unSelect);
                }
                //为LinearLayout添加ImageView
                mLlIndicator.addView(imageView, params);
                mImgList.add(imageView);
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            for (int i = 0; i < mPageCount; i++) {
                //选中的页面改变小圆点为选中状态，反之为未选中
                if ((position % mPageCount) == i) {
                    (mImgList.get(i)).setBackgroundResource(img_select);
                } else {
                    (mImgList.get(i)).setBackgroundResource(img_unSelect);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int position) {

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
