package com.pengxh.app.weatherplus.ui;

import android.app.ProgressDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.aihook.alertview.library.AlertView;
import com.aihook.alertview.library.OnItemClickListener;
import com.alibaba.fastjson.JSONObject;
import com.gyf.immersionbar.ImmersionBar;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.weatherplus.R;
import com.pengxh.app.weatherplus.adapter.HotCityAdapter;
import com.pengxh.app.weatherplus.bean.AllCityBean;
import com.pengxh.app.weatherplus.bean.CityNameBean;
import com.pengxh.app.weatherplus.bean.HotCityNameBean;
import com.pengxh.app.weatherplus.bean.NetWeatherBean;
import com.pengxh.app.weatherplus.event.AutoCompleteEvent;
import com.pengxh.app.weatherplus.mvp.presenter.WeatherPresenterImpl;
import com.pengxh.app.weatherplus.mvp.view.IWeatherView;
import com.pengxh.app.weatherplus.utils.GreenDaoUtil;
import com.pengxh.app.weatherplus.utils.OtherUtil;
import com.pengxh.app.weatherplus.utils.SQLiteUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SelectCityActivity extends BaseNormalActivity implements IWeatherView, View.OnClickListener, OnItemClickListener {

    private static final String TAG = "SelectCityActivity";

    @BindView(R.id.mTextView_current_location)
    TextView mTextView_current_location;
    @BindView(R.id.mImageView_hot_city)
    ImageView mImageView_hot_city;
    @BindView(R.id.mAutoCompleteTextView)
    AutoCompleteTextView mAutoCompleteTextView;
    @BindView(R.id.mRecyclerView_hot_city)
    RecyclerView mRecyclerViewHotCity;
    @BindView(R.id.mImageView_title_add)
    ImageView mImageView_title_add;
    @BindView(R.id.mTextView_title)
    TextView mTextView_title;

    private WeatherPresenterImpl weatherPresenter;
    private ProgressDialog progressDialog;
    private HotCityAdapter hotCityAdapter;
    private AlertView alertView;
    private List<HotCityNameBean> allHotCity;
    private SQLiteUtil sqLiteUtil;

    @Override
    public void initView() {
        setContentView(R.layout.activity_selectcity);
        ImmersionBar.with(this)
                .statusBarColor("#0094FF")
                .fitsSystemWindows(true)
                .init();
    }

    @Override
    public void init() {
        mImageView_title_add.setVisibility(View.INVISIBLE);
        mTextView_title.setText("添加城市");
        String district = getIntent().getStringExtra("district");
        Log.d(TAG, "定位点: " + district);
        if (!TextUtils.isEmpty(district)) {
            mTextView_current_location.setText(district);
        } else {
            mTextView_current_location.setText("定位失败");
        }
    }

    @Override
    public void initEvent() {
        sqLiteUtil = SQLiteUtil.getInstance();
        weatherPresenter = new WeatherPresenterImpl(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<CityNameBean> allCityName = GreenDaoUtil.loadAllCityName();
                List<String> cities = new ArrayList<>();
                for (int i = 0; i < allCityName.size(); i++) {
                    String city = allCityName.get(i).getCity();
                    cities.add(city);
                }
                EventBus.getDefault().postSticky(new AutoCompleteEvent(cities));
            }
        }).start();

        allHotCity = sqLiteUtil.loadHotCity();
        if (allHotCity.size() > 0) {
            mImageView_hot_city.setVisibility(View.VISIBLE);
            hotCityAdapter = new HotCityAdapter(this, allHotCity);
            mRecyclerViewHotCity.setLayoutManager(
                    new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
            mRecyclerViewHotCity.setAdapter(hotCityAdapter);
            hotCityAdapter.setOnItemClickListener(new HotCityAdapter.OnItemClickListener() {
                @Override
                public void onClick(int position) {
                    String city = allHotCity.get(position).getCityName();

                    List<AllCityBean> beanList = GreenDaoUtil.queryCity(city);
                    if (beanList.size() > 0) {
                        getCityWeather(beanList.get(0));
                    }
                }
            });
        } else {
            mImageView_hot_city.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        weatherPresenter.onUnsubscribe();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventMainThread(AutoCompleteEvent event) {
        List<String> cities = event.getCities();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this
                , android.R.layout.simple_dropdown_item_1line
                , OtherUtil.removeDuplicate(cities));
        mAutoCompleteTextView.setThreshold(1); //设置输入一个字符提示，默认为2
        mAutoCompleteTextView.setAdapter(adapter);
        mAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                List<AllCityBean> beanList = GreenDaoUtil.queryCity(s.toString());
                if (beanList.size() > 0) {
                    //将查询历史保存到[热门]表
                    AllCityBean cityBean = beanList.get(0);

                    sqLiteUtil.saveHotCity(cityBean.getCity());
                    getCityWeather(cityBean);
                }
            }
        });
        EventBus.getDefault().removeStickyEvent(event);
    }

    private void getCityWeather(AllCityBean cityBean) {
        weatherPresenter.onReadyRetrofitRequest(cityBean.getCity(),
                Integer.parseInt(cityBean.getCityid()),
                Integer.parseInt(cityBean.getCitycode()));
    }

    @OnClick({R.id.mImageView_title_back, R.id.mImageView_hot_city})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mImageView_title_back:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //在主线程销毁，直接出栈，返回键不会返回到前一个页面
                        finish();
                    }
                });
                break;
            case R.id.mImageView_hot_city:
                alertView = new AlertView(
                        "提示",
                        "确认删除全部搜索历史?",
                        "取消",
                        new String[]{"确定"},
                        null,
                        this,
                        AlertView.Style.Alert,
                        this).setCancelable(false);
                alertView.show();
                break;
        }
    }

    @Override
    public void onItemClick(Object o, int position) {
        /**
         * -1取消，0确定
         * */
        switch (position) {
            case -1:
                alertView.dismiss();
                break;
            case 0:
                allHotCity.clear();
                sqLiteUtil.deleteAll();
                hotCityAdapter.notifyDataSetChanged();
                mImageView_hot_city.setVisibility(View.INVISIBLE);
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
    public void showNetWorkData(NetWeatherBean weatherBean) {
        if (weatherBean != null) {
            //也存数据库，sp不太合适
            String city = weatherBean.getResult().getResult().getCity();//用于判断城市是否存在于表中，如果存在就更新天气数据
            String jsonString = JSONObject.toJSONString(weatherBean);
            Log.d(TAG, "showNetWorkData: " + jsonString);
            sqLiteUtil.saveCityListWeather(city, jsonString);
        }
    }
}