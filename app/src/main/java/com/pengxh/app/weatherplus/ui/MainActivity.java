package com.pengxh.app.weatherplus.ui;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aihook.alertview.library.AlertView;
import com.aihook.alertview.library.OnItemClickListener;
import com.gyf.immersionbar.ImmersionBar;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.utils.BroadcastManager;
import com.pengxh.app.multilib.utils.DensityUtil;
import com.pengxh.app.multilib.utils.SaveKeyValues;
import com.pengxh.app.multilib.widget.EasyToast;
import com.pengxh.app.weatherplus.R;
import com.pengxh.app.weatherplus.adapter.WeatherPageAdapter;
import com.pengxh.app.weatherplus.bean.CityWeatherBean;
import com.pengxh.app.weatherplus.listener.LocationCallbackListener;
import com.pengxh.app.weatherplus.ui.fragment.PageFragment;
import com.pengxh.app.weatherplus.utils.Constant;
import com.pengxh.app.weatherplus.utils.LocationClient;
import com.pengxh.app.weatherplus.utils.SQLiteUtil;
import com.pengxh.app.weatherplus.widgets.EasyPopupWindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseNormalActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    //头布局
    @BindView(R.id.layoutView)
    LinearLayout layoutView;

    private String[] periodArray = {"1小时", "2小时", "6小时", "12小时", "24小时", "不更新"};
    private List<String> items = Arrays.asList("管理城市", "更新间隔");
    private WeatherPageChangeListener pageChangeListener;
    private WeatherPageAdapter pagerAdapter;
    private ViewPager mMainViewPager;
    private LinearLayout indicatorLayout;
    private BroadcastManager broadcastManager;
    private LocationClient locationClient;

    @Override
    public int initLayoutView() {
        return R.layout.activity_main;
    }

    @Override
    public void initData() {
        ImmersionBar.with(this).statusBarColor(R.color.statusBar_color).fitsSystemWindows(true).init();
        mMainViewPager = findViewById(R.id.mMainViewPager);
        indicatorLayout = findViewById(R.id.mLlIndicator);

        broadcastManager = BroadcastManager.getInstance(this);
        locationClient = new LocationClient(this);
        locationClient.obtainLocation(new LocationCallbackListener() {
            @Override
            public void onGetLocation(String location) {
                SaveKeyValues.putValue("location", location);
                pagerAdapter = new WeatherPageAdapter(getSupportFragmentManager());
                //首次启动默认显示一个点，后面启动从数据库读取数据
                List<CityWeatherBean> weatherBeans = SQLiteUtil.getInstance().loadCityWeatherList();
                if (weatherBeans == null || weatherBeans.size() == 0) {
                    pagerAdapter.addPage(PageFragment.newInstance(location));
                } else {
                    for (int i = 0; i < weatherBeans.size(); i++) {
                        String city = weatherBeans.get(i).getCity();
                        pagerAdapter.addPage(PageFragment.newInstance(city));
                    }
                }
                Message message = indicatorHandler.obtainMessage();
                message.what = 1000;
                if (weatherBeans == null || weatherBeans.size() == 0) {
                    message.obj = 1;
                } else {
                    message.obj = weatherBeans.size();
                }
                indicatorHandler.sendMessage(message);
                mMainViewPager.setAdapter(pagerAdapter);
            }
        });
    }

    @Override
    public void initEvent() {
        BroadcastManager.getInstance(this).addAction(Constant.PAGE_ACTIONS, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action != null) {
                    switch (action) {
                        case "action_addCity":
                            //TODO 需要考虑怎么批量添加
//                            String city = intent.getStringExtra("data");
                            CopyOnWriteArrayList<Fragment> fragmentList = pagerAdapter.getFragmentList();
                            List<CityWeatherBean> weatherBeans = SQLiteUtil.getInstance().loadCityWeatherList();
                            for (Fragment f : fragmentList) {
                                String fragmentTitle = f.getArguments().getString("fragment_title");
                                if (weatherBeans.contains(fragmentTitle)) {
                                    Log.d(TAG, "onReceive: 包含" + fragmentTitle);
                                }
//                                pagerAdapter.addPage(PageFragment.newInstance(city));
                            }
                            break;
                        case "action_delCity":
                            int size = pagerAdapter.getFragmentList().size();
                            if (size == 1) {
                                EasyToast.showToast("默认天气页无法删除", EasyToast.WARING);
                                return;
                            }
                            String index = intent.getStringExtra("data");
                            pagerAdapter.delPage(Integer.parseInt(index));
                            break;
                    }
                    Message message = indicatorHandler.obtainMessage();
                    message.what = 1000;
                    message.obj = pagerAdapter.getFragmentList().size();
                    indicatorHandler.sendMessage(message);
                }
            }
        });
        pageChangeListener = new WeatherPageChangeListener(this, indicatorLayout);
        mMainViewPager.addOnPageChangeListener(pageChangeListener);
    }

    @SuppressLint("HandlerLeak")
    private Handler indicatorHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1000) {
                int pageSize = (int) msg.obj;
                pageChangeListener.setIndicator(pageSize);
            }
        }
    };

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        broadcastManager.destroy(Constant.PAGE_ACTIONS);
        if (locationClient != null) {
            locationClient.destroyLocationClient();
        }
    }

    private static class WeatherPageChangeListener implements ViewPager.OnPageChangeListener {

        private static final int imgSize = 30;
        private static final int img_select = R.drawable.dot_enable;
        private static final int img_unSelect = R.drawable.dot_disable;
        private Context context;
        private LinearLayout mIndicator;
        private int mPageCount;
        private List<ImageView> mImgList;//保存img总个数

        private WeatherPageChangeListener(Context ctx, LinearLayout indicator) {
            this.context = ctx;
            this.mIndicator = indicator;
            mImgList = new ArrayList<>();
        }

        private void setIndicator(int size) {
            this.mPageCount = size;
            //***小点数量变化之后，一定要先清除之前加进去的View，不然会出现重复*************************//
            mImgList.clear();
            mIndicator.removeAllViews();
            //****************************//
            for (int i = 0; i < size; i++) {
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
                    imageView.setBackgroundResource(img_unSelect);
                } else {
                    imageView.setBackgroundResource(img_select);
                }
                //为LinearLayout添加ImageView
                mIndicator.addView(imageView, params);
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
                    mImgList.get(i).setBackgroundResource(img_unSelect);
                } else {
                    mImgList.get(i).setBackgroundResource(img_select);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int position) {

        }
    }
}