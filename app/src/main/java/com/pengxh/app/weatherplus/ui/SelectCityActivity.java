package com.pengxh.app.weatherplus.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.aihook.alertview.library.AlertView;
import com.alibaba.fastjson.JSONObject;
import com.gyf.immersionbar.ImmersionBar;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.weatherplus.R;
import com.pengxh.app.weatherplus.adapter.HotCityAdapter;
import com.pengxh.app.weatherplus.bean.CityInfoBean;
import com.pengxh.app.weatherplus.bean.HotCityBean;
import com.pengxh.app.weatherplus.bean.WeatherBean;
import com.pengxh.app.weatherplus.listener.LocationCallbackListener;
import com.pengxh.app.weatherplus.mvp.presenter.WeatherPresenterImpl;
import com.pengxh.app.weatherplus.mvp.view.IWeatherView;
import com.pengxh.app.weatherplus.utils.LocationClient;
import com.pengxh.app.weatherplus.utils.SQLiteUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SelectCityActivity extends BaseNormalActivity implements IWeatherView, View.OnClickListener {

    private static final String TAG = "SelectCityActivity";
    @BindView(R.id.mTitleView)
    TextView mTitleView;
    @BindView(R.id.mTitleAddView)
    ImageView mTitleAddView;
    @BindView(R.id.mCurrentLocation)
    TextView mCurrentLocation;
    @BindView(R.id.mHotCityImageView)
    ImageView mHotCityImageView;
    @BindView(R.id.mAutoCompleteTextView)
    AutoCompleteTextView mAutoCompleteTextView;
    @BindView(R.id.mHotCityRecyclerView)
    RecyclerView mRecyclerViewHotCity;

    private SQLiteUtil sqLiteUtil;
    private List<HotCityBean> hotCityList = new ArrayList<>();
    private HotCityAdapter hotCityAdapter = null;
    private WeatherPresenterImpl weatherPresenter;
    private ProgressDialog progressDialog;
    private LocationClient locationClient;

    @Override
    public int initLayoutView() {
        return R.layout.activity_select_city;
    }

    @Override
    public void initData() {
        ImmersionBar.with(this).statusBarColor(R.color.statusBar_color).fitsSystemWindows(true).init();
        mTitleAddView.setVisibility(View.INVISIBLE);
        mTitleView.setText("添加城市");

        String district = getIntent().getStringExtra("currentLocation");
        if (!TextUtils.isEmpty(district)) {
            mCurrentLocation.setText(district);
        } else {
            mCurrentLocation.setText("定位失败");
        }

        locationClient = new LocationClient(this);
        weatherPresenter = new WeatherPresenterImpl(this);
        sqLiteUtil = SQLiteUtil.getInstance();
        //加载所有城市名并转为数组
        new Thread(() -> {
            String[] array = sqLiteUtil.loadAllCityName();
            //获取到数组之后再去初始化AutoCompleteTextView的Adapter
            Message message = mHandler.obtainMessage();
            message.what = 1000;
            message.obj = array;
            mHandler.sendMessage(message);
        }).start();
    }

    @Override
    public void initEvent() {
        hotCityList = sqLiteUtil.loadHotCity();
        if (hotCityList == null || hotCityList.size() == 0) {
            mHotCityImageView.setVisibility(View.INVISIBLE);
        } else {
            mHotCityImageView.setVisibility(View.VISIBLE);
        }
        hotCityAdapter = new HotCityAdapter(SelectCityActivity.this, hotCityList);
        mRecyclerViewHotCity.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerViewHotCity.setAdapter(hotCityAdapter);
        hotCityAdapter.setOnItemClickListener(position -> {
            String city = hotCityList.get(position).getCity();
            CityInfoBean.ResultBeanX.ResultBean cityBean = sqLiteUtil.queryCityInfo(city);
            if (cityBean != null) {
                getCityWeather(cityBean);
            }
        });
    }

    private void getCityWeather(CityInfoBean.ResultBeanX.ResultBean cityBean) {
        Log.d(TAG, "getCityWeather: 查询天气");
        weatherPresenter.onReadyRetrofitRequest(cityBean.getCity(), cityBean.getCityid(), Integer.parseInt(cityBean.getCitycode()));
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 10:
                    finish();
                    break;
                case 1000:
                    String[] cityNameArray = (String[]) msg.obj;
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(SelectCityActivity.this, android.R.layout.simple_dropdown_item_1line, cityNameArray);
                    mAutoCompleteTextView.setThreshold(1); //设置输入一个字符提示，默认为2
                    mAutoCompleteTextView.setAdapter(adapter);
                    mAutoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
                        String text = mAutoCompleteTextView.getText().toString();
                        CityInfoBean.ResultBeanX.ResultBean cityBean = sqLiteUtil.queryCityInfo(text);
                        if (cityBean != null) {
                            String city = cityBean.getCity();
                            //将查询历史保存到[热门]表
                            sqLiteUtil.saveHotCity(city);
                            //查询天气
                            getCityWeather(cityBean);

                            for (HotCityBean bean : hotCityList) {
                                if (city.equals(bean.getCity())) {
                                    Log.d(TAG, "热门城市已存在，不更新列表");
                                    return;
                                }
                            }
                            HotCityBean hotCityNameBean = new HotCityBean();
                            hotCityNameBean.setCity(city);
                            hotCityList.add(hotCityNameBean);
                            hotCityAdapter.notifyDataSetChanged();
                            mHotCityImageView.setVisibility(View.VISIBLE);
                        }
                    });
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + msg.what);
            }
        }
    };

    @OnClick({R.id.mTitleBackView, R.id.mHotCityImageView, R.id.mCurrentLocation})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mTitleBackView:
                finish();
                break;
            case R.id.mHotCityImageView:
                new AlertView("提示", "确认删除全部搜索历史?", "取消", new String[]{"确定"}, null, this, AlertView.Style.Alert, (o, position) -> {
                    if (position == 0) {
                        hotCityList.clear();
                        sqLiteUtil.deleteAll();
                        hotCityAdapter.notifyDataSetChanged();
                        mHotCityImageView.setVisibility(View.INVISIBLE);
                    }
                }).setCancelable(false).show();
                break;
            case R.id.mCurrentLocation:
                locationClient.obtainLocation(new LocationCallbackListener() {
                    @Override
                    public void onGetLocation(String location) {
                        if (!TextUtils.isEmpty(location)) {
                            mCurrentLocation.setText(location);
                            CityInfoBean.ResultBeanX.ResultBean cityBean = sqLiteUtil.queryCityInfo(location);
                            if (cityBean != null) {
                                getCityWeather(cityBean);
                            }
                        } else {
                            mCurrentLocation.setText("定位失败，请重试");
                        }
                    }
                });
                break;
        }
    }

    @Override
    public void showProgress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载天气数据...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
    }

    @Override
    public void hideProgress() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
            finish();
        }
    }

    @Override
    public void showNetWorkData(WeatherBean weatherBean) {
        if (weatherBean != null) {
            //存数据库，sp不合适
            String city = weatherBean.getResult().getResult().getCity();//用于判断城市是否存在于表中，如果存在就更新天气数据
            String jsonString = JSONObject.toJSONString(weatherBean);
            sqLiteUtil.saveCityListWeather(city, jsonString);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (weatherPresenter != null) {
            weatherPresenter.onUnsubscribe();
        }
        if (locationClient != null) {
            locationClient.destroyLocationClient();
        }
    }
}