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
import com.pengxh.app.weatherplus.bean.NetWeatherBean;
import com.pengxh.app.weatherplus.mvp.presenter.WeatherPresenterImpl;
import com.pengxh.app.weatherplus.mvp.view.IWeatherView;
import com.pengxh.app.weatherplus.utils.SQLiteUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SelectCityActivity extends BaseNormalActivity implements IWeatherView, View.OnClickListener {

    private static final String TAG = "SelectCityActivity";
    @BindView(R.id.mTextView_title)
    TextView mTextView_title;
    @BindView(R.id.mImageView_title_add)
    ImageView mImageView_title_add;
    @BindView(R.id.mTextView_current_location)
    TextView mTextView_current_location;
    @BindView(R.id.mImageView_hot_city)
    ImageView mImageView_hot_city;
    @BindView(R.id.mAutoCompleteTextView)
    AutoCompleteTextView mAutoCompleteTextView;
    @BindView(R.id.mRecyclerView_hot_city)
    RecyclerView mRecyclerViewHotCity;

    private SQLiteUtil sqLiteUtil;
    private List<HotCityBean> hotCityList = new ArrayList<>();
    private HotCityAdapter hotCityAdapter = null;
    private WeatherPresenterImpl weatherPresenter;
    private ProgressDialog progressDialog;
    private AlertView alertView;

    @Override
    public int initLayoutView() {
        return R.layout.activity_selectcity;
    }

    @Override
    public void initData() {
        ImmersionBar.with(this).statusBarColor("#0094FF").fitsSystemWindows(true).init();
        mImageView_title_add.setVisibility(View.INVISIBLE);
        mTextView_title.setText("添加城市");

        String district = getIntent().getStringExtra("currentLocation");
        if (!TextUtils.isEmpty(district)) {
            mTextView_current_location.setText(district);
        } else {
            mTextView_current_location.setText("定位失败");
        }

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
        if (hotCityList != null) {
            mImageView_hot_city.setVisibility(View.VISIBLE);
        } else {
            mImageView_hot_city.setVisibility(View.INVISIBLE);
        }
        hotCityAdapter = new HotCityAdapter(SelectCityActivity.this, hotCityList);
        mRecyclerViewHotCity.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerViewHotCity.setAdapter(hotCityAdapter);
        hotCityAdapter.setOnItemClickListener(new HotCityAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                String city = hotCityList.get(position).getCity();
                CityInfoBean.ResultBeanX.ResultBean cityBean = sqLiteUtil.queryCityInfo(city);
                if (cityBean != null) {
                    getCityWeather(cityBean);
                }
            }
        });
    }

    private void getCityWeather(CityInfoBean.ResultBeanX.ResultBean cityBean) {
        Log.d(TAG, "getCityWeather: 查询天气");
        weatherPresenter.onReadyRetrofitRequest(cityBean.getCity(), cityBean.getCityid(), Integer.parseInt(cityBean.getCitycode()));
//        EventBus.getDefault().postSticky(new TagEvent(SelectCityActivity.class.getSimpleName(), 1));
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
                            mImageView_hot_city.setVisibility(View.VISIBLE);
                        }
                    });
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + msg.what);
            }
        }
    };

    @OnClick({R.id.mImageView_title_back, R.id.mImageView_hot_city})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mImageView_title_back:
                finish();
                break;
            case R.id.mImageView_hot_city:
                new AlertView("提示", "确认删除全部搜索历史?", "取消", new String[]{"确定"}, null, this, AlertView.Style.Alert, (o, position) -> {
                    if (position == 0) {
                        hotCityList.clear();
                        sqLiteUtil.deleteAll();
                        hotCityAdapter.notifyDataSetChanged();
                        mImageView_hot_city.setVisibility(View.INVISIBLE);
                    }
                }).setCancelable(false).show();
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
            //延时关闭页面，确保CityListActivity页面能基本收到消息
//            mHandler.sendEmptyMessageDelayed(10, 500);
        }
    }

    @Override
    public void showNetWorkData(NetWeatherBean weatherBean) {
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
        weatherPresenter.onUnsubscribe();
    }
}