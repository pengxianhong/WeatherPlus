package com.pengxh.app.weatherplus.ui;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.aihook.alertview.library.AlertView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.gyf.immersionbar.ImmersionBar;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.utils.BroadcastManager;
import com.pengxh.app.multilib.utils.DensityUtil;
import com.pengxh.app.multilib.utils.SaveKeyValues;
import com.pengxh.app.weatherplus.R;
import com.pengxh.app.weatherplus.adapter.BottomSheetAdapter;
import com.pengxh.app.weatherplus.adapter.WeatherPageAdapter;
import com.pengxh.app.weatherplus.bean.CityWeatherBean;
import com.pengxh.app.weatherplus.listener.LocationCallbackListener;
import com.pengxh.app.weatherplus.ui.fragment.WeatherPageFragment;
import com.pengxh.app.weatherplus.utils.Constant;
import com.pengxh.app.weatherplus.utils.LocationClient;
import com.pengxh.app.weatherplus.utils.SQLiteUtil;
import com.pengxh.app.weatherplus.widgets.EasyPopupWindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            new AlertView("提醒", "GPS定位可能没打开", "取消", new String[]{"打开"}, null, this, AlertView.Style.AlertDialog, (o, position) -> {
                switch (position) {
                    case -1:
                        finish();
                        break;
                    case 0:
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, 10);
                        break;
                }
            }).show();
        } else {
            initPage();
        }
    }

    private void initPage() {
        Log.d(TAG, "initPage: ");
        locationClient.obtainLocation(new LocationCallbackListener() {
            @Override
            public void onGetLocation(String location) {
                Log.d(TAG, "onGetLocation: " + location);
                SaveKeyValues.putValue("location", location);
                pagerAdapter = new WeatherPageAdapter(getSupportFragmentManager());
                //首次启动默认显示一个点，后面启动从数据库读取数据
                List<CityWeatherBean> weatherBeans = SQLiteUtil.getInstance().loadCityWeatherList();
                if (weatherBeans == null || weatherBeans.size() == 0) {
                    Log.d(TAG, "onGetLocation: 初次加载数据");
                    pagerAdapter.addPage(WeatherPageFragment.newInstance(location));
                } else {
                    pagerAdapter.addPage(0, WeatherPageFragment.newInstance(location));
                    for (int i = 1; i < weatherBeans.size(); i++) {
                        String city = weatherBeans.get(i).getCity();
                        pagerAdapter.addPage(i, WeatherPageFragment.newInstance(city));
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == 0) {
            initPage();
        }
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
                            List<Fragment> fragmentList = pagerAdapter.getFragmentList();
                            List<CityWeatherBean> weatherBeans = SQLiteUtil.getInstance().loadCityWeatherList();

                            List<String> fragmentTitleList = new ArrayList<>();
                            for (Fragment f : fragmentList) {
                                String fragmentTitle = f.getArguments().getString("fragment_title");
                                fragmentTitleList.add(fragmentTitle);
                            }
                            List<String> weatherCityList = new ArrayList<>();
                            for (CityWeatherBean weatherBean : weatherBeans) {
                                weatherCityList.add(weatherBean.getCity());
                            }
                            //求差集
                            weatherCityList.removeAll(fragmentTitleList);
                            Log.d(TAG, "新增页面: " + weatherCityList);
                            for (String title : weatherCityList) {
                                pagerAdapter.addPage(WeatherPageFragment.newInstance(title));
                            }
                            break;
                        case "action_delCity":
                            String index = intent.getStringExtra("data");
                            assert index != null;
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
                BottomSheetDialog dialog = new BottomSheetDialog(this);
                View view = getLayoutInflater().inflate(R.layout.dialog_bottom, null);
                ListView bottomListView = view.findViewById(R.id.bottomListView);
                TextView bottomCancelView = view.findViewById(R.id.bottomCancelView);

                BottomSheetAdapter bottomAdapter = new BottomSheetAdapter(this, periodArray);
                bottomListView.setAdapter(bottomAdapter);
                bottomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String s = periodArray[position];
                        SaveKeyValues.putValue("hours", s);
                        String time;
                        if (s.contains("小时")) {
                            time = s.split("小时")[0];
                        } else {
                            //不更新
                            time = "9999999";
                        }
                        int hours = Integer.parseInt(time);
                        //TODO 启动定时服务

                        RadioButton radioButton = view.findViewById(R.id.radioButton);
                        //每次选择一个item时都要清除所有的状态，防止出现多个被选中
                        bottomAdapter.clearStates(position);
                        radioButton.setChecked(bottomAdapter.getStates(position));
                        bottomAdapter.notifyDataSetChanged();
                    }
                });
                bottomCancelView.setOnClickListener(v1 -> dialog.dismiss());

                dialog.setContentView(view);
                dialog.show();
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
                    imageView.setBackgroundResource(img_select);
                } else {
                    imageView.setBackgroundResource(img_unSelect);
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
                    mImgList.get(i).setBackgroundResource(img_select);
                } else {
                    mImgList.get(i).setBackgroundResource(img_unSelect);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int position) {

        }
    }
}