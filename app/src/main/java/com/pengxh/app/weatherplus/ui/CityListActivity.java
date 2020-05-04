package com.pengxh.app.weatherplus.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.gyf.immersionbar.ImmersionBar;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.utils.DensityUtil;
import com.pengxh.app.multilib.utils.SaveKeyValues;
import com.pengxh.app.multilib.widget.EasyToast;
import com.pengxh.app.multilib.widget.swipemenu.SwipeMenuItem;
import com.pengxh.app.multilib.widget.swipemenu.SwipeMenuListView;
import com.pengxh.app.weatherplus.R;
import com.pengxh.app.weatherplus.adapter.CityListAdapter;
import com.pengxh.app.weatherplus.bean.CityWeatherBean;
import com.pengxh.app.weatherplus.bean.WeatherBean;
import com.pengxh.app.weatherplus.utils.OtherUtil;
import com.pengxh.app.weatherplus.utils.SQLiteUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint("SetTextI18n")
public class CityListActivity extends BaseNormalActivity implements View.OnClickListener {
    @BindView(R.id.mCityName)
    TextView mCityName;
    @BindView(R.id.mCityQuality)
    TextView mCityQuality;
    @BindView(R.id.mCityImage)
    ImageView mCityImage;
    @BindView(R.id.mCityWeather)
    TextView mCityWeather;
    @BindView(R.id.mCityLowTemp)
    TextView mCityLowTemp;
    @BindView(R.id.mCityHighTemp)
    TextView mCityHighTemp;
    @BindView(R.id.mSwipeMenuListView)
    SwipeMenuListView mSwipeMenuListView;

    private static final String TAG = "CityListActivity";
    private String currentLocation = "";
    private CityListAdapter cityAdapter;
    private SQLiteUtil sqLiteUtil;
    private List<CityWeatherBean> weatherBeans = new ArrayList<>();

    @Override
    public int initLayoutView() {
        return R.layout.activity_city_list;
    }

    @Override
    public void initData() {
        ImmersionBar.with(this).statusBarColor(R.color.statusBar_color).fitsSystemWindows(true).init();
        currentLocation = (String) SaveKeyValues.getValue("location", "");
        //设置第一个item的城市天气。后续改为实时更新的效果
        sqLiteUtil = SQLiteUtil.getInstance();
        String weatherJson = sqLiteUtil.loadCityWeather(currentLocation).getWeather();
        if (weatherJson == null || weatherJson.equals("")) {
            Log.d(TAG, "initData: 加载当前位置天气失败");
            return;
        }
        //解析weather
        WeatherBean weatherBean = JSONObject.parseObject(weatherJson, WeatherBean.class);
        WeatherBean.ResultBeanX.ResultBean resultBean = weatherBean.getResult().getResult();
        mCityName.setText(resultBean.getCity());
        mCityQuality.setText(resultBean.getAqi().getQuality());
        mCityQuality.setTextColor(Color.parseColor(resultBean.getAqi().getAqiinfo().getColor()));
        mCityImage.setImageResource(OtherUtil.getImageResource(resultBean.getImg()));
        mCityWeather.setText(resultBean.getWeather());
        mCityLowTemp.setText(resultBean.getTemplow() + "℃~");
        mCityHighTemp.setText(resultBean.getTemphigh() + "℃");
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<CityWeatherBean> beanList = sqLiteUtil.loadCityWeatherList();
        //原本可以直接用sqLiteUtil.loadCityWeatherList()，但是由于数据库里面存在定位点的天气数据，如果直接用，那么列表会出现重复，所以，需要将数据筛选下
        weatherBeans.clear();
        for (int i = 1; i < beanList.size(); i++) {
            CityWeatherBean weatherBean = beanList.get(i);
            weatherBeans.add(weatherBean);
        }
        if (weatherBeans != null) {
            cityAdapter = new CityListAdapter(this, weatherBeans);
            mSwipeMenuListView.setAdapter(cityAdapter);
        }
    }

    @Override
    public void initEvent() {
        mSwipeMenuListView.setMenuCreator(menu -> {
            SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
            openItem.setBackground(new ColorDrawable(Color.rgb(255, 69, 0)));
            openItem.setWidth(DensityUtil.dp2px(getApplicationContext(), 70.0f));
            openItem.setTitle("删除");
            openItem.setTitleSize(18);
            openItem.setTitleColor(Color.WHITE);
            menu.addMenuItem(openItem);
        });
        mSwipeMenuListView.setOnMenuItemClickListener((position, menu, index) -> {
            if (index == 0) {//先删除数据库数据，再删除List，不然会出现角标越界
                sqLiteUtil.deleteCityByName(weatherBeans.get(position).getCity());
                weatherBeans.remove(position);
                cityAdapter.notifyDataSetChanged();
                //TODO 发送del广播
//                    sendUpdateBroadcast("del");
            }
            return true;
        });
        mSwipeMenuListView.setOnItemClickListener((parent, view, position, id) -> {
            //TODO 发送add广播
//                sendUpdateBroadcast("add");
//                finish();
        });
    }


    @OnClick({R.id.mTitleBackView, R.id.mRelativeLayout_citylist, R.id.mTitleAddView})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mTitleBackView:
                finish();
                break;
            case R.id.mRelativeLayout_citylist:
                EasyToast.showToast("定位点城市不能删除", EasyToast.WARING);
                break;
            case R.id.mTitleAddView:
                Intent intent = new Intent(this, SelectCityActivity.class);
                intent.putExtra("currentLocation", currentLocation);
                startActivity(intent);
                break;
        }
    }
}
