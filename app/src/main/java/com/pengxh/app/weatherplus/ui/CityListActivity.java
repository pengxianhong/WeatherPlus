package com.pengxh.app.weatherplus.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.utils.DensityUtil;
import com.pengxh.app.multilib.utils.ToastUtil;
import com.pengxh.app.multilib.widget.swipemenu.SwipeMenu;
import com.pengxh.app.multilib.widget.swipemenu.SwipeMenuCreator;
import com.pengxh.app.multilib.widget.swipemenu.SwipeMenuItem;
import com.pengxh.app.multilib.widget.swipemenu.SwipeMenuListView;
import com.pengxh.app.weatherplus.R;
import com.pengxh.app.weatherplus.adapter.CityListAdapter;
import com.pengxh.app.weatherplus.bean.CityManagerBean;
import com.pengxh.app.weatherplus.callback.impl.FragmentListImpl;
import com.pengxh.app.weatherplus.ui.fragment.OtherWeatherFragment;
import com.pengxh.app.weatherplus.utils.GreenDaoUtil;
import com.pengxh.app.weatherplus.utils.OtherUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class CityListActivity extends BaseNormalActivity implements View.OnClickListener {

    private static final String TAG = "CityListActivity";

    @BindView(R.id.mImageView_title_back)
    ImageView mImageView_title_back;
    @BindView(R.id.mImageView_title_add)
    ImageView mImageView_title_add;
    @BindView(R.id.mSwipeMenuListView)
    SwipeMenuListView mSwipeMenuListView;

    private List<CityManagerBean> otherCityWeather;
    private CityListAdapter cityAdapter;

    @Override
    public void initView() {
        setContentView(R.layout.activity_citylist);
    }

    @Override
    public void init() {
        otherCityWeather = GreenDaoUtil.loadOtherCityWeather();
        if (otherCityWeather.size() > 0) {
            cityAdapter = new CityListAdapter(this, otherCityWeather);
            mSwipeMenuListView.setAdapter(cityAdapter);
        }
    }

    @Override
    public void initEvent() {
        mSwipeMenuListView.setMenuCreator(new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
                openItem.setBackground(new ColorDrawable(Color.rgb(255, 0, 0)));
                openItem.setWidth(DensityUtil.dp2px(getApplicationContext(), 90.0f));
                openItem.setTitle("Delete");
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
                        GreenDaoUtil.deleteCity(otherCityWeather.get(position));
                        otherCityWeather.remove(position);
                        Log.d(TAG, "otherCityWeather.size(): " + otherCityWeather.size()
                                + "\r\nposition: " + position);
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
                Log.d(TAG, "onItemClick: item位置" + position);
                if (position != 0) {
                    //第一个位置不新建fragment
                    new FragmentListImpl().addFragment(new OtherWeatherFragment());
                }
                finish();
            }
        });
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
