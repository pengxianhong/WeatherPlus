package com.pengxh.app.weatherplus.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

import com.gyf.immersionbar.ImmersionBar;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.utils.DensityUtil;
import com.pengxh.app.multilib.utils.SaveKeyValues;
import com.pengxh.app.multilib.widget.swipemenu.SwipeMenuItem;
import com.pengxh.app.multilib.widget.swipemenu.SwipeMenuListView;
import com.pengxh.app.weatherplus.R;
import com.pengxh.app.weatherplus.adapter.CityListAdapter;
import com.pengxh.app.weatherplus.bean.CityWeatherBean;
import com.pengxh.app.weatherplus.utils.SQLiteUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint("SetTextI18n")
public class CityListActivity extends BaseNormalActivity implements View.OnClickListener {

    private static final String TAG = "CityListActivity";

    @BindView(R.id.mSwipeMenuListView)
    SwipeMenuListView mSwipeMenuListView;

    private String currentLocation = "";
    private CityListAdapter cityAdapter;
    private SQLiteUtil sqLiteUtil;
    private List<CityWeatherBean> weatherBeans;

    @Override
    public int initLayoutView() {
        return R.layout.activity_city_list;
    }

    @Override
    public void initData() {
        ImmersionBar.with(this).statusBarColor(R.color.statusBar_color).fitsSystemWindows(true).init();
        currentLocation = (String) SaveKeyValues.getValue("location", "");
        sqLiteUtil = SQLiteUtil.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        weatherBeans = sqLiteUtil.loadCityWeatherList();
        if (weatherBeans != null) {
            cityAdapter = new CityListAdapter(this, weatherBeans);
            mSwipeMenuListView.setAdapter(cityAdapter);
        }
    }

    @Override
    public void initEvent() {
        mSwipeMenuListView.setMenuCreator(menu -> {
            SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
            openItem.setBackground(new ColorDrawable(Color.rgb(255, 81, 81)));
            openItem.setWidth(DensityUtil.dp2px(getApplicationContext(), 70.0f));
            openItem.setTitle("删除");
            openItem.setTitleSize(18);
            openItem.setTitleColor(Color.WHITE);
            menu.addMenuItem(openItem);
        });
        mSwipeMenuListView.setOnMenuItemClickListener((position, menu, index) -> {
            if (index == 0) {//先删除数据库数据，再删除List，不然会出现角标越界
                //TODO 发送del广播
//                    sendUpdateBroadcast("del");
                sqLiteUtil.deleteCityByName(weatherBeans.get(position).getCity());
                weatherBeans.remove(position);
                cityAdapter.notifyDataSetChanged();
            }
            return true;
        });
        mSwipeMenuListView.setOnItemClickListener((parent, view, position, id) -> {
            //TODO 发送add广播
//                sendUpdateBroadcast("add");
            finish();
        });
    }


    @OnClick({R.id.mTitleBackView, R.id.mTitleAddView})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mTitleBackView:
                finish();
                break;
            case R.id.mTitleAddView:
                Intent intent = new Intent(this, SelectCityActivity.class);
                intent.putExtra("currentLocation", currentLocation);
                startActivity(intent);
                break;
        }
    }
}
