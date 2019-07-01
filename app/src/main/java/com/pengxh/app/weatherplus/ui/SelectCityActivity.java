package com.pengxh.app.weatherplus.ui;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.utils.ToastUtil;
import com.pengxh.app.weatherplus.R;
import com.pengxh.app.weatherplus.bean.CityDaoBean;
import com.pengxh.app.weatherplus.bean.CityNameBean;
import com.pengxh.app.weatherplus.utils.GreenDaoUtil;
import com.pengxh.app.weatherplus.utils.OtherUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SelectCityActivity extends BaseNormalActivity implements View.OnClickListener {

    private static final String TAG = "SelectCityActivity";

    @BindView(R.id.mTextView_current_location)
    TextView mTextView_current_location;
    @BindView(R.id.mAutoCompleteTextView)
    AutoCompleteTextView mAutoCompleteTextView;
    @BindView(R.id.mRecyclerView_hot_city)
    RecyclerView mRecyclerViewHotCity;

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
        List<CityNameBean> allCityName = GreenDaoUtil.loadAllCityName();
        List<String> cities = new ArrayList<>();
        for (int i = 0; i < allCityName.size(); i++) {
            String city = allCityName.get(i).getCity();
            cities.add(city);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
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
                final String city = s.toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<CityDaoBean> cityBeanList = GreenDaoUtil.queryCity(city);
                        Log.d(TAG, "从数据库中获取cityBeanList: " + cityBeanList.size());
                        if (cityBeanList.size() > 0) {
                            CityDaoBean cityBean = cityBeanList.get(0);
                            Log.d(TAG, "City: " + cityBean.getCity() +
                                    "\r\nCitycode: " + cityBean.getCitycode() +
                                    "\r\nCityid: " + cityBean.getCityid());
                        } else {
                            ToastUtil.showBeautifulToast("获取天气失败，请稍后再试", 5);
                        }
                    }
                }).start();
            }
        });
    }

    @OnClick(R.id.mImageView_title_back)
    @Override
    public void onClick(View v) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //在主线程销毁，直接出栈，返回键不会返回到前一个页面
                finish();
            }
        });
    }
}