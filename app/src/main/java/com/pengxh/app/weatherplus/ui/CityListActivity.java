package com.pengxh.app.weatherplus.ui;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.weatherplus.R;
import com.pengxh.app.weatherplus.utils.OtherUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class CityListActivity extends BaseNormalActivity implements View.OnClickListener {

    private static final String TAG = "CityListActivity";

    @BindView(R.id.mImageView_title_back)
    ImageView mImageView_title_back;
    @BindView(R.id.mImageView_title_add)
    ImageView mImageView_title_add;

    @Override
    public void initView() {
        setContentView(R.layout.activity_citylist);
    }

    @Override
    public void init() {

    }

    @Override
    public void initEvent() {

    }

    @OnClick({R.id.mImageView_title_back, R.id.mImageView_title_add})
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
            case R.id.mImageView_title_add:
                Intent intent = new Intent(this, SelectCityActivity.class);
                intent.putExtra("district", OtherUtil.getValue(this, "district"));
                startActivity(intent);
                break;
        }
    }
}
