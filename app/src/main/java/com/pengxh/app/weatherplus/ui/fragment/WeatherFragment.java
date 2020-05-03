package com.pengxh.app.weatherplus.ui.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aihook.alertview.library.AlertView;
import com.aihook.alertview.library.OnItemClickListener;
import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.gyf.immersionbar.components.ImmersionFragment;
import com.pengxh.app.multilib.utils.SaveKeyValues;
import com.pengxh.app.multilib.widget.CustomGridView;
import com.pengxh.app.multilib.widget.EasyToast;
import com.pengxh.app.weatherplus.R;
import com.pengxh.app.weatherplus.adapter.GridViewAdapter;
import com.pengxh.app.weatherplus.adapter.HourlyRecyclerViewAdapter;
import com.pengxh.app.weatherplus.adapter.WeeklyRecyclerViewAdapter;
import com.pengxh.app.weatherplus.bean.WeatherBean;
import com.pengxh.app.weatherplus.mvp.presenter.WeatherPresenterImpl;
import com.pengxh.app.weatherplus.mvp.view.IWeatherView;
import com.pengxh.app.weatherplus.service.LocationService;
import com.pengxh.app.weatherplus.ui.CityListActivity;
import com.pengxh.app.weatherplus.utils.OtherUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class WeatherFragment extends ImmersionFragment implements IWeatherView, View.OnClickListener {

    private static final String TAG = "WeatherFragment";

    @BindView(R.id.mTextView_realtime_cityName)
    TextView mTextViewRealtimeCityName;
    @BindView(R.id.mTextView_realtime_date)
    TextView mTextViewRealtimeDate;
    @BindView(R.id.TextView_realtime_week)
    TextView mTextViewRealtimeWeek;
    @BindView(R.id.mImageView_realtime_add)
    ImageView mImageView_realtime_add;

    @BindView(R.id.mImageView_realtime_img)
    ImageView mImageViewRealtimeImg;
    @BindView(R.id.mTextView_realtime_temp)
    TextView mTextViewRealtimeTemp;
    @BindView(R.id.mTextView_realtime_weather)
    TextView mTextViewRealtimeWeather;
    @BindView(R.id.mTextView_realtime_templow)
    TextView mTextViewRealtimeTemplow;
    @BindView(R.id.mTextView_realtime_temphigh)
    TextView mTextViewRealtimeTemphigh;
    @BindView(R.id.mTextView_realtime_update)
    TextView mTextViewRealtimeUpdate;
    @BindView(R.id.mTextView_realtime_humidity)
    TextView mTextViewRealtimeHumidity;
    @BindView(R.id.mTextView_realtime_purple)
    TextView mTextViewRealtimePurple;
    @BindView(R.id.mTextView_realtime_sport)
    TextView mTextViewRealtimeSport;
    @BindView(R.id.mTextView_realtime_dress)
    TextView mTextViewRealtimeDress;
    @BindView(R.id.mLayout_realtime)
    LinearLayout mLayoutRealtime;
    @BindView(R.id.mTextView_realtime_aqi)
    TextView mTextViewRealtimeAqi;
    @BindView(R.id.mTextView_realtime_quality)
    TextView mTextViewRealtimeQuality;

    @BindView(R.id.mRecyclerView_hourly)
    RecyclerView mRecyclerViewHourly;

    @BindView(R.id.mRecyclerView_weekly)
    RecyclerView mRecyclerViewWeekly;

    @BindView(R.id.mTextView_air_aqi)
    TextView mTextViewAirAqi;
//    @BindView(R.id.mDialProgress_air_aqi)
//    DialProgress mDialProgressAirAqi;
    @BindView(R.id.mTextView_air_pm10)
    TextView mTextViewAirPM10;
    @BindView(R.id.mTextView_air_pm2_5)
    TextView mTextViewAirPM2_5;
    @BindView(R.id.mTextView_air_no2)
    TextView mTextViewAirNO2;
    @BindView(R.id.mTextView_air_so2)
    TextView mTextViewAirSO2;
    @BindView(R.id.mTextView_air_o3)
    TextView mTextViewAirO3;
    @BindView(R.id.mTextView_air_co)
    TextView mTextViewAirCO;
    @BindView(R.id.mCustomGridView_life)
    CustomGridView mCustomGridView_life;

    private WeatherPresenterImpl weatherPresenter;
    private ProgressDialog progressDialog;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_weather, null);
        unbinder = ButterKnife.bind(this, view);
        String simpleName = LocationService.class.getSimpleName();
        Log.d(TAG, simpleName + " isServiceRunning: " + OtherUtil.isServiceRunning(getContext(), simpleName));
        initEvent();
        return view;
    }

    //Fragment沉浸式状态栏
    @Override
    public void initImmersionBar() {
        ImmersionBar.with(this)
                .statusBarColor("#00BAFF")
                .fitsSystemWindows(true)
                .init();
    }

    private void initEvent() {
        //获取天气数据
        weatherPresenter = new WeatherPresenterImpl(this);
        //TODO 解决页面太长，ScrollView默认不能置顶的问题
        mTextViewRealtimeQuality.setFocusable(true);
        mTextViewRealtimeQuality.setFocusableInTouchMode(true);
        mTextViewRealtimeQuality.requestFocus();

        final String district = (String) SaveKeyValues.getValue("district", "");
        if (TextUtils.isEmpty(district)) {
            EasyToast.showToast("定位失败，请退出稍后重试", EasyToast.ERROR);
        } else {
            boolean isFirstGet = (boolean) SaveKeyValues.getValue("isFirstGet", true);
            if (isFirstGet) {
                SaveKeyValues.putValue("isFirstGet", false);
                /**
                 * 首次加载可能会加载不出数据，线程控制规避此问题
                 * */
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 5; i++) {
                            try {
                                Thread.sleep(1000);
                                getCityBean(district);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            } else {
                //如果不是第一次获取启动app并获取天气，就直接请求一次
                getCityBean(district);
            }
        }
    }

    private void getCityBean(String district) {
//        List<AllCityBean> beanList = GreenDaoUtil.queryCity(district);
//        if (beanList.size() > 0) {
//            AllCityBean allCityBean = beanList.get(0);
//            EventBus.getDefault().postSticky(new CityBeanEvent(allCityBean));
//        }
    }

//    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
//    public void onEventMainThread(CityBeanEvent event) {
//        AllCityBean allCityBean = event.getAllCityBean();
//        weatherPresenter.onReadyRetrofitRequest(allCityBean.getCity(),
//                Integer.parseInt(allCityBean.getCityid()),
//                Integer.parseInt(allCityBean.getCitycode()));
//
//        EventBus.getDefault().removeStickyEvent(event);
//    }

    @Override
    public void showNetWorkData(WeatherBean weatherBean) {
        if (weatherBean != null) {
            // TODO 显示当天的详细天气情况
            WeatherBean.ResultBeanX.ResultBean resultBean = weatherBean.getResult().getResult();
            bindResultData(resultBean);

            // TODO 显示当天24h的天气情况
            List<WeatherBean.ResultBeanX.ResultBean.HourlyBean> hourlyBeanList = weatherBean.getResult().getResult()
                    .getHourly();
            bindHourlyData(hourlyBeanList);

            // TODO 显示一周内的天气情况
            List<WeatherBean.ResultBeanX.ResultBean.DailyBean> dailyBeanList = weatherBean.getResult().getResult()
                    .getDaily();
            bindDailyData(dailyBeanList);

            // TODO 显示详细空气质量
            WeatherBean.ResultBeanX.ResultBean.AqiBean aqiBean = weatherBean.getResult().getResult().getAqi();
            bindAqiData(aqiBean);

            // TODO 绑定GridView
            List<WeatherBean.ResultBeanX.ResultBean.IndexBean> indexBeanList = weatherBean.getResult().getResult()
                    .getIndex();
            bindIndexData(indexBeanList);

            //TODO 保存简单的天气信息
            Map<String, String> weatherMap = new HashMap<>();
            weatherMap.put("city", resultBean.getCity());
            weatherMap.put("quality", aqiBean.getQuality());
            weatherMap.put("color", aqiBean.getAqiinfo().getColor());
            weatherMap.put("img", resultBean.getImg());
            weatherMap.put("weather", resultBean.getWeather());
            weatherMap.put("templow", resultBean.getTemplow());
            weatherMap.put("temphigh", resultBean.getTemphigh());
            SaveKeyValues.putValue("weatherMap", new Gson().toJson(weatherMap));
        } else {
            EasyToast.showToast("获取数据失败，请重试", EasyToast.ERROR);
        }
    }

    private void bindResultData(WeatherBean.ResultBeanX.ResultBean resultBean) {
        mTextViewRealtimeCityName.setText(resultBean.getCity());
        mTextViewRealtimeDate.setText("\r\r" + resultBean.getDate() + "\r\r");
        mTextViewRealtimeWeek.setText(resultBean.getWeek());

        mImageViewRealtimeImg.setImageResource(OtherUtil.getImageResource(resultBean.getImg()));
        mTextViewRealtimeTemp.setText(resultBean.getTemp() + "°");
        mTextViewRealtimeWeather.setText(resultBean.getWeather());
        mTextViewRealtimeTemplow.setText(resultBean.getTemplow() + "℃~");
        mTextViewRealtimeTemphigh.setText(resultBean.getTemphigh() + "℃");
        String updatetime = resultBean.getUpdatetime();
        mTextViewRealtimeUpdate.setText(updatetime.substring(5, 16) + "\r\r更新");
        mTextViewRealtimeUpdate.setCompoundDrawables(null, null, null, null);//定位点隐藏刷新按钮

        mTextViewRealtimeHumidity.setText(resultBean.getHumidity() + "%");
        mTextViewRealtimePurple.setText(resultBean.getIndex().get(2).getDetail());
        mTextViewRealtimeSport.setText(resultBean.getIndex().get(1).getDetail());
        mTextViewRealtimeDress.setText(resultBean.getIndex().get(6).getDetail());

        mLayoutRealtime.setBackgroundColor(Color.parseColor(resultBean.getAqi().getAqiinfo().getColor()));
        mTextViewRealtimeAqi.setText(resultBean.getAqi().getAqi());
        mTextViewRealtimeQuality.setText(resultBean.getAqi().getQuality());
    }

    private void bindHourlyData(List<WeatherBean.ResultBeanX.ResultBean.HourlyBean> hourlyBeanList) {
        HourlyRecyclerViewAdapter adapter = new HourlyRecyclerViewAdapter(getContext(), hourlyBeanList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);// 横向滚动
        mRecyclerViewHourly.setLayoutManager(layoutManager);
        mRecyclerViewHourly.setAdapter(adapter);
    }

    private void bindDailyData(List<WeatherBean.ResultBeanX.ResultBean.DailyBean> dailyBeanList) {
        WeeklyRecyclerViewAdapter adapter = new WeeklyRecyclerViewAdapter(getContext(), dailyBeanList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewWeekly.setLayoutManager(layoutManager);
        mRecyclerViewWeekly.setAdapter(adapter);
    }

    private void bindAqiData(WeatherBean.ResultBeanX.ResultBean.AqiBean aqiBean) {
        mTextViewAirAqi.setText("污染指数\r\r" + aqiBean.getQuality());
//        mDialProgressAirAqi.setValue(Float.parseFloat(aqiBean.getAqi()));
//        mDialProgressAirAqi.setValueLevel(aqiBean.getQuality());
        mTextViewAirPM10.setText(aqiBean.getPm10());
        mTextViewAirPM2_5.setText(aqiBean.getPm2_5());
        mTextViewAirNO2.setText(aqiBean.getNo2());
        mTextViewAirSO2.setText(aqiBean.getSo2());
        mTextViewAirO3.setText(aqiBean.getO3());
        mTextViewAirCO.setText(aqiBean.getCo());
    }

    private void bindIndexData(final List<WeatherBean.ResultBeanX.ResultBean.IndexBean> indexBeanList) {
        GridViewAdapter mGridViewAdapter = new GridViewAdapter(getContext(), indexBeanList);
        mCustomGridView_life.setAdapter(mGridViewAdapter);
        mCustomGridView_life.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String iname = indexBeanList.get(position).getIname();
                String detail = indexBeanList.get(position).getDetail();
                new AlertView(iname, detail, null, new String[]{"确定"}, null, getContext(),
                        AlertView.Style.Alert, new AlertViewItemClickListener()).show();
            }
        });
    }

    class AlertViewItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(Object o, int position) {

        }
    }

    @OnClick({R.id.mImageView_realtime_add})
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.mImageView_realtime_add) {
            startActivity(new Intent(getActivity(), CityListActivity.class));
        }
    }

    @Override
    public void showProgress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("正在加载天气数据...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
    }

    @Override
    public void hideProgress() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        EventBus.getDefault().register(this);
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        EventBus.getDefault().unregister(this);
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        weatherPresenter.onUnsubscribe();
        unbinder.unbind();
    }
}
