package com.pengxh.app.weatherplus.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.gyf.immersionbar.ImmersionBar;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.utils.DensityUtil;
import com.pengxh.app.multilib.utils.ToastUtil;
import com.pengxh.app.multilib.widget.swipemenu.SwipeMenu;
import com.pengxh.app.multilib.widget.swipemenu.SwipeMenuCreator;
import com.pengxh.app.multilib.widget.swipemenu.SwipeMenuItem;
import com.pengxh.app.multilib.widget.swipemenu.SwipeMenuListView;
import com.pengxh.app.weatherplus.R;
import com.pengxh.app.weatherplus.adapter.CityListAdapter;
import com.pengxh.app.weatherplus.bean.CityListWeatherBean;
import com.pengxh.app.weatherplus.event.TagEvent;
import com.pengxh.app.weatherplus.utils.OtherUtil;
import com.pengxh.app.weatherplus.utils.SQLiteUtil;
import com.pengxh.app.weatherplus.utils.SaveKeyValues;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class CityListActivity extends BaseNormalActivity implements View.OnClickListener {
    @BindView(R.id.mImageView_title_back)
    ImageView mImageView_title_back;
    @BindView(R.id.mImageView_title_add)
    ImageView mImageView_title_add;

    @BindView(R.id.mTextView_citylist_city)
    TextView mTextViewCitylistCity;
    @BindView(R.id.mTextView_citylist_quality)
    TextView mTextViewCitylistQuality;
    @BindView(R.id.mImageView_citylist_img)
    ImageView mImageViewCitylistImg;
    @BindView(R.id.mTextView_citylist_weather)
    TextView mTextViewCitylistWeather;
    @BindView(R.id.mTextView_citylist_templow)
    TextView mTextViewCitylistTemplow;
    @BindView(R.id.mTextView_citylist_temphigh)
    TextView mTextViewCitylistTemphigh;

    @BindView(R.id.mSwipeMenuListView)
    SwipeMenuListView mSwipeMenuListView;


    private CityListAdapter cityAdapter;
    private List<CityListWeatherBean> listWeatherBeans;
    private SQLiteUtil sqLiteUtil;

    @Override
    public void initView() {
        setContentView(R.layout.activity_citylist);
        ImmersionBar.with(this)
                .statusBarColor("#00BAFF")
                .fitsSystemWindows(true)
                .init();
    }

    @Override
    public void init() {
        //设置第一个item的城市天气。后续改为实时更新的效果
        String weatherJson = (String) SaveKeyValues.getValue("location_weather", "weatherMap", "");
        if (!weatherJson.equals("")) {
            /**
             * {"templow":"19","img":"301","color":"#FFFF00","city":"西城区","weather":"雨","quality":"良","temphigh":"28"}
             * */
            try {
                JSONObject jsonObject = new JSONObject(weatherJson);
                mTextViewCitylistCity.setText(jsonObject.getString("city"));
                mTextViewCitylistQuality.setText(jsonObject.getString("quality"));
                mTextViewCitylistQuality.setBackgroundColor(Color.parseColor(jsonObject.getString("color")));
                mImageViewCitylistImg.setImageResource(OtherUtil.getImageResource(jsonObject.getString("img")));
                mTextViewCitylistWeather.setText(jsonObject.getString("weather"));
                mTextViewCitylistTemplow.setText(jsonObject.getString("templow") + "℃~");
                mTextViewCitylistTemphigh.setText(jsonObject.getString("temphigh") + "℃");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //设置其他城市的信息
        sqLiteUtil = SQLiteUtil.getInstance();
        listWeatherBeans = sqLiteUtil.loadCityList();
        if (listWeatherBeans.size() > 0) {
            cityAdapter = new CityListAdapter(this, listWeatherBeans);
            mSwipeMenuListView.setAdapter(cityAdapter);
        }
    }

    @Override
    public void initEvent() {
        mSwipeMenuListView.setMenuCreator(new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
                openItem.setBackground(new ColorDrawable(Color.rgb(255, 69, 0)));
                openItem.setWidth(DensityUtil.dp2px(getApplicationContext(), 70.0f));
                openItem.setTitle("删除");
                openItem.setTitleSize(18);
                openItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(openItem);
            }
        });
        mSwipeMenuListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        //先删除数据库数据，再删除List，不然会出现角标越界
                        sqLiteUtil.deleteCityByName(listWeatherBeans.get(position).getCityName());
                        listWeatherBeans.remove(position);
                        cityAdapter.notifyDataSetChanged();
                        ToastUtil.showBeautifulToast("删除成功", ToastUtil.SUCCESS);
                        break;
                }
                return true;
            }
        });
        mSwipeMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                finish();
            }
        });
    }


    @OnClick({R.id.mImageView_title_back, R.id.mRelativeLayout_citylist, R.id.mImageView_title_add})
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
            case R.id.mRelativeLayout_citylist:
                ToastUtil.showBeautifulToast("定位点城市不能删除", ToastUtil.WARNING);
                break;
            case R.id.mImageView_title_add:
                Intent intent = new Intent(this, SelectCityActivity.class);
                intent.putExtra("district", (String) SaveKeyValues.getValue("location", "district", ""));
                startActivity(intent);
                break;
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
    public void onEventMainThread(TagEvent event) {
        int tag = event.getTag();
        if (tag == 1) {
            //刷新UI
            sqLiteUtil = SQLiteUtil.getInstance();
            listWeatherBeans = sqLiteUtil.loadCityList();
            if (listWeatherBeans.size() > 0) {
                cityAdapter = new CityListAdapter(this, listWeatherBeans);
                mSwipeMenuListView.setAdapter(cityAdapter);
            }
        }
        EventBus.getDefault().removeStickyEvent(event);
    }
}
