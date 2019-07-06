package com.pengxh.app.weatherplus.ui;

import android.content.Intent;
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
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.utils.ToastUtil;
import com.pengxh.app.weatherplus.R;
import com.pengxh.app.weatherplus.adapter.HotCityAdapter;
import com.pengxh.app.weatherplus.bean.CityDaoBean;
import com.pengxh.app.weatherplus.bean.CityInfoDaoBean;
import com.pengxh.app.weatherplus.bean.CityNameDaoBean;
import com.pengxh.app.weatherplus.event.AutoCompleteEvent;
import com.pengxh.app.weatherplus.utils.GreenDaoUtil;
import com.pengxh.app.weatherplus.utils.OtherUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SelectCityActivity extends BaseNormalActivity implements View.OnClickListener, OnItemClickListener {

    private static final String TAG = "SelectCityActivity";

    @BindView(R.id.mTextView_current_location)
    TextView mTextView_current_location;
    @BindView(R.id.mImageView_hot_city)
    ImageView mImageView_hot_city;
    @BindView(R.id.mAutoCompleteTextView)
    AutoCompleteTextView mAutoCompleteTextView;
    @BindView(R.id.mRecyclerView_hot_city)
    RecyclerView mRecyclerViewHotCity;

    private HotCityAdapter hotCityAdapter;
    private AlertView alertView;
    private List<CityInfoDaoBean> hotCityList;

    @Override
    public void initView() {
        setContentView(R.layout.activity_selectcity);
    }

    @Override
    public void init() {
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<CityNameDaoBean> allCityName = GreenDaoUtil.loadAllCityName();
                List<String> cities = new ArrayList<>();
                for (int i = 0; i < allCityName.size(); i++) {
                    String city = allCityName.get(i).getCity();
                    cities.add(city);
                }
                EventBus.getDefault().postSticky(new AutoCompleteEvent(cities));
            }
        }).start();

        hotCityList = GreenDaoUtil.loadAllHotCity();
        if (hotCityList.size() > 0) {
            mImageView_hot_city.setVisibility(View.VISIBLE);
            hotCityAdapter = new HotCityAdapter(this, hotCityList);
            mRecyclerViewHotCity.setLayoutManager(
                    new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
            mRecyclerViewHotCity.setAdapter(hotCityAdapter);
            hotCityAdapter.setOnItemClickListener(new HotCityAdapter.OnItemClickListener() {
                @Override
                public void onClick(int position) {
                    ToastUtil.showBeautifulToast(hotCityList.get(position).getCity(), ToastUtil.SUCCESS);
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

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventMainThread(AutoCompleteEvent event) {
        List<String> cities = event.getCities();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line,
                OtherUtil.removeDuplicate(cities));
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
                List<CityDaoBean> beanList = GreenDaoUtil.queryCity(s.toString());
                Log.d(TAG, "从数据库中获取cityDaoBean: " + Arrays.toString(beanList.toArray()));
                if (beanList.size() > 0) {
                    CityDaoBean cityDaoBean = beanList.get(0);
                    String cityname = cityDaoBean.getCity();
                    String citycode = cityDaoBean.getCitycode();
                    String cityid = cityDaoBean.getCityid();
                    Log.d(TAG, "City: " + cityname + "\r\nCitycode: " + citycode + "\r\nCityid: " + cityid);
                    //将查询历史保存到数据库
                    GreenDaoUtil.saveToSQL(cityname, cityid, citycode);

                    Intent intent = new Intent(SelectCityActivity.this, OtherCityWeather.class);
                    intent.putExtra("cityname", cityname);
                    intent.putExtra("cityid", cityid);
                    intent.putExtra("citycode", citycode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        EventBus.getDefault().removeStickyEvent(event);
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
                GreenDaoUtil.deleteHotCity();
                hotCityList.clear();
                hotCityAdapter.notifyDataSetChanged();
                mImageView_hot_city.setVisibility(View.INVISIBLE);
                break;
        }
    }
}