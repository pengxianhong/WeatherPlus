package com.pengxh.app.weatherplus.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aihook.alertview.library.AlertView;
import com.aihook.alertview.library.OnItemClickListener;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.weatherplus.R;
import com.pengxh.app.weatherplus.event.PagePositionEvent;
import com.pengxh.app.weatherplus.service.LocationService;
import com.pengxh.app.weatherplus.ui.fragment.OtherWeatherFragment;
import com.pengxh.app.weatherplus.ui.fragment.WeatherFragment;
import com.pengxh.app.weatherplus.utils.SQLiteUtil;

import org.greenrobot.eventbus.EventBus;

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
    private List<Fragment> fragmentLinkedList = new LinkedList<>();


    @Override
    public void initView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void init() {
        fragmentLinkedList.add(new WeatherFragment());
        int pageNumber = SQLiteUtil.getInstance().loadCityList().size();
        for (int i = 0; i < pageNumber; i++) {
            fragmentLinkedList.add(new OtherWeatherFragment());
        }
    }

    @Override
    public void initEvent() {
        FragmentPagerAdapter adapter = new WeatherPageAdapter(getSupportFragmentManager(), fragmentLinkedList);
        mMainViewPager.setOffscreenPageLimit(3);
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

            final int imgSize = 30;

            for (int i = 0; i < mPageCount; i++) {
                ImageView imageView = new ImageView(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                //为小圆点左右添加间距
                params.leftMargin = 10;
                params.rightMargin = 10;
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
//            Log.d(TAG, "onPageSelected: => " + position);
            /**
             * position=0不能传过去
             * */
            if (position == 0) {
                Log.w(TAG, "onPageSelected: 定位点位置不传", new Throwable());
            } else {
                EventBus.getDefault().postSticky(new PagePositionEvent(position - 1));
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

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        //防止定位不准确
        int p1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int p2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int p3 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (PackageManager.PERMISSION_GRANTED != p1 || PackageManager.PERMISSION_GRANTED != p2 || PackageManager.PERMISSION_GRANTED != p3) {
            new AlertView("友情提示", "缺少定位权限，请在设置里面打开相关权限", null, new String[]{"确定"}, null, MainActivity.this,
                    AlertView.Style.Alert, new OnItemClickListener() {
                @Override
                public void onItemClick(Object o, int position) {
                    MainActivity.this.finish();
                }
            }).show();
        } else {
            startService(new Intent(this, LocationService.class));
        }
    }
}
