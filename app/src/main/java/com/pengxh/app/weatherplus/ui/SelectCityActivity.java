package com.pengxh.app.weatherplus.ui;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.weatherplus.R;

import butterknife.BindView;
import butterknife.OnClick;

public class SelectCityActivity extends BaseNormalActivity implements View.OnClickListener {

    private static final String TAG = "SelectCityActivity";

    @BindView(R.id.mTextView_current_location)
    TextView mTextView_current_location;

    @Override
    public void initView() {
        setContentView(R.layout.activity_selectcity);
    }

    @Override
    public void init() {
        String district = getIntent().getStringExtra("district");
        Log.d(TAG, "定位点: " + district);
        if (district.isEmpty()) {
            mTextView_current_location.setText("定位失败");
        } else {
            mTextView_current_location.setText(district);
        }
    }

    @Override
    public void initEvent() {

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