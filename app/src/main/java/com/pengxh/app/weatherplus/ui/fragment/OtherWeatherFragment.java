package com.pengxh.app.weatherplus.ui.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.alibaba.fastjson.JSONObject;
import com.gyf.immersionbar.ImmersionBar;
import com.gyf.immersionbar.components.ImmersionFragment;
import com.pengxh.app.multilib.widget.CustomGridView;
import com.pengxh.app.weatherplus.R;
import com.pengxh.app.weatherplus.adapter.GridViewAdapter;
import com.pengxh.app.weatherplus.adapter.HourlyRecyclerViewAdapter;
import com.pengxh.app.weatherplus.adapter.WeeklyRecyclerViewAdapter;
import com.pengxh.app.weatherplus.bean.CityWeatherBean;
import com.pengxh.app.weatherplus.bean.WeatherBean;
import com.pengxh.app.weatherplus.mvp.presenter.WeatherPresenterImpl;
import com.pengxh.app.weatherplus.mvp.view.IWeatherView;
import com.pengxh.app.weatherplus.ui.CityListActivity;
import com.pengxh.app.weatherplus.utils.OtherUtil;
import com.pengxh.app.weatherplus.utils.SQLiteUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class OtherWeatherFragment extends ImmersionFragment implements View.OnClickListener, IWeatherView {

    private static final String TAG = "OtherWeatherFragment";

    @BindView(R.id.mImageView_realtime_location)
    ImageView mImageView_realtime_location;
    @BindView(R.id.mTextView_realtime_cityName)
    TextView mTextViewRealtimeCityName;
    @BindView(R.id.mTextView_realtime_date)
    TextView mTextViewRealtimeDate;
    @BindView(R.id.TextView_realtime_week)
    TextView mTextViewRealtimeWeek;

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

    private Unbinder unbinder;
    private PagePositionBroadcast broadcast = null;
    private WeatherPresenterImpl weatherPresenter;
    private ProgressDialog progressDialog;
    private String cityName;
    private SQLiteUtil sqLiteUtil;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_weather, null);
        unbinder = ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        sqLiteUtil = SQLiteUtil.getInstance();
        weatherPresenter = new WeatherPresenterImpl(this);

        mImageView_realtime_location.setVisibility(View.GONE);
        // TODO 解决页面太长，ScrollView默认不能置顶的问题
        mTextViewRealtimeQuality.setFocusable(true);
        mTextViewRealtimeQuality.setFocusableInTouchMode(true);
        mTextViewRealtimeQuality.requestFocus();
    }

    // Fragment沉浸式状态栏
    @Override
    public void initImmersionBar() {
        ImmersionBar.with(this).statusBarColor("#00BAFF").fitsSystemWindows(true).init();
    }

    class PagePositionBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals("action.changePosition")) {
                int index = Integer.parseInt(intent.getStringExtra("position"));
                List<CityWeatherBean> weatherBeans = sqLiteUtil.loadCityWeatherList();
                cityName = weatherBeans.get(index).getCity();
                final String weather = weatherBeans.get(index).getWeather();
                if (weather.equals("")) {
                    Log.w(TAG, "onEventMainThread: ", new Throwable());
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            WeatherBean weatherBean = JSONObject.parseObject(weather, WeatherBean.class);
                            Message message = mHandler.obtainMessage();
                            message.obj = weatherBean;
                            message.what = 100;
                            mHandler.sendMessageDelayed(message, 1000);
                        }
                    }).start();
                }
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "handleMessage: " + msg.what);
            if (msg.what == 100 || msg.what == 101) {
                WeatherBean weatherBean = (WeatherBean) msg.obj;
                setWeather(weatherBean);
            } else {
                Log.w(TAG, "handleMessage: error msg", new Throwable());
            }
        }
    };

    private void setWeather(WeatherBean weatherBean) {
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
                new AlertView(iname, detail, null, new String[]{"确定"}, null, getContext(), AlertView.Style.Alert,
                        new AlertViewItemClickListener()).show();
            }
        });
    }

    class AlertViewItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(Object o, int position) {

        }
    }

    @OnClick({R.id.mImageView_realtime_add, R.id.mTextView_realtime_update})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mImageView_realtime_add:
                startActivity(new Intent(getActivity(), CityListActivity.class));
                break;
            case R.id.mTextView_realtime_update:
                // 手动更新天气
//            List<AllCityBean> beanList = GreenDaoUtil.queryCity(cityName);
//            if (beanList.size() > 0) {
//                AllCityBean cityBean = beanList.get(0);
//                weatherPresenter.onReadyRetrofitRequest(cityBean.getCity(), Integer.parseInt(cityBean.getCityid()),
//                        Integer.parseInt(cityBean.getCitycode()));
//            }
                break;
            default:
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (broadcast == null) {
            broadcast = new PagePositionBroadcast();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("action.changePosition");
            context.registerReceiver(broadcast, intentFilter);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (broadcast != null) {
            getActivity().unregisterReceiver(broadcast);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        weatherPresenter.onUnsubscribe();
        unbinder.unbind();
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

    @Override
    public void showNetWorkData(WeatherBean weatherBean) {
        if (weatherBean != null) {
            String city = weatherBean.getResult().getResult().getCity();// 用于判断城市是否存在于表中，如果存在就更新天气数据
            String jsonString = JSONObject.toJSONString(weatherBean);
            sqLiteUtil.saveCityListWeather(city, jsonString);

            // 手动获取天气
            Message message = mHandler.obtainMessage();
            message.obj = weatherBean;
            message.what = 101;
            mHandler.sendMessageDelayed(message, 1000);
            Log.d(TAG, "showNetWorkData: 手动获取天气");
        }
    }
}