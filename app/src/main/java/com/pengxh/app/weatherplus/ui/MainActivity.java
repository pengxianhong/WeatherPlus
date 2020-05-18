package com.pengxh.app.weatherplus.ui;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.aihook.alertview.library.AlertView;
import com.aihook.alertview.library.OnItemClickListener;
import com.gyf.immersionbar.ImmersionBar;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.utils.DensityUtil;
import com.pengxh.app.weatherplus.R;
import com.pengxh.app.weatherplus.ui.fragment.WeatherFragment;
import com.pengxh.app.weatherplus.widgets.EasyPopupWindow;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseNormalActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    //头布局
    @BindView(R.id.layoutView)
    LinearLayout layoutView;

    private String[] periodArray = {"1小时", "2小时", "6小时", "12小时", "24小时", "不更新"};
    private List<String> items = Arrays.asList("管理城市", "更新间隔");
    private ViewPager mMainViewPager;
    private LinearLayout mLlIndicator;
    private List<Fragment> fragmentList = new ArrayList<>();

    @Override
    public int initLayoutView() {
        return R.layout.activity_main;
    }

    @Override
    public void initData() {
        Log.d(TAG, "initData: ");
        ImmersionBar.with(this).statusBarColor(R.color.statusBar_color).fitsSystemWindows(true).init();

        mMainViewPager = findViewById(R.id.mMainViewPager);
        mLlIndicator = findViewById(R.id.mLlIndicator);
    }

    @Override
    public void initEvent() {
        fragmentList.add(new WeatherFragment());
        //默认添加当前位置天气页面
        int size = fragmentList.size();
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragmentList);
        mMainViewPager.setAdapter(pagerAdapter);
        mMainViewPager.addOnPageChangeListener(new WeatherPageChangeListener(this, mLlIndicator, size));
    }

    @OnClick(R.id.manageCity)
    @Override
    public void onClick(View v) {
        EasyPopupWindow easyPopupWindow = new EasyPopupWindow(this, items);
        easyPopupWindow.setPopupWindowClickListener(position -> {
            if (position == 0) {
                startActivity(new Intent(this, CityListActivity.class));
            } else if (position == 1) {
                new AlertView("更新间隔", null, "取消", null, periodArray, this, AlertView.Style.ActionSheet, new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int position) {
                        //TODO 获取到更新时间间隔之后设置按设定刷新时间
                    }
                }).show();
            }
        });
        easyPopupWindow.setBackgroundDrawable(null);
        easyPopupWindow.showAsDropDown(layoutView
                , layoutView.getWidth() - easyPopupWindow.getWidth() - DensityUtil.dp2px(this, 20)
                , DensityUtil.dp2px(this, 40));
    }


    /**
     * home键然后回到App：onPause->onStop->onRestart->onStart->onResume
     * 返回键然后回到App：onPause->onStop->onDestroy->initData(onCreate)->onStart->onResume
     */

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

    private static class ViewPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragments;

        ViewPagerAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            this.mFragments = list;
        }

        @NotNull
        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            if (mFragments == null) {
                return 0;
            }
            return mFragments.size();
        }
    }

    private static class WeatherPageChangeListener implements ViewPager.OnPageChangeListener {

        private int mPageCount;//页数
        private List<ImageView> mImgList;//保存img总个数
        private int img_select;
        private int img_unSelect;

        private WeatherPageChangeListener(Context context, LinearLayout mLlIndicator, int pageCount) {
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
        }

        @Override
        public void onPageScrollStateChanged(int position) {

        }
    }
}