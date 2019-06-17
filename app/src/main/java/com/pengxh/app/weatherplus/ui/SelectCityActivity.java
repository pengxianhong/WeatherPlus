package com.pengxh.app.weatherplus.ui;

import android.view.View;
import android.widget.TextView;

import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.weatherplus.R;

import butterknife.BindView;
import butterknife.OnClick;

public class SelectCityActivity extends BaseNormalActivity implements View.OnClickListener {

    @BindView(R.id.mTextView_current_location)
    TextView mTextView_current_location;

    @Override
    public void initView() {
        setContentView(R.layout.activity_selectcity);
    }

    @Override
    public void init() {
        String district = getIntent().getStringExtra("district");
        mTextView_current_location.setText(district);
    }

    @Override
    public void initEvent() {

    }

    @OnClick()
    @Override
    public void onClick(View v) {

    }
}