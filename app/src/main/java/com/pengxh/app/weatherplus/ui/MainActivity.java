package com.pengxh.app.weatherplus.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aihook.alertview.library.AlertView;
import com.aihook.alertview.library.OnItemClickListener;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.utils.ToastUtil;
import com.pengxh.app.weatherplus.R;
import com.pengxh.app.weatherplus.adapter.GridViewAdapter;
import com.pengxh.app.weatherplus.adapter.HourlyRecyclerViewAdapter;
import com.pengxh.app.weatherplus.adapter.WeeklyRecyclerViewAdapter;
import com.pengxh.app.weatherplus.bean.CityDaoBean;
import com.pengxh.app.weatherplus.bean.NetWeatherBean;
import com.pengxh.app.weatherplus.mvp.presenter.WeatherPresenterImpl;
import com.pengxh.app.weatherplus.mvp.view.IWeatherView;
import com.pengxh.app.weatherplus.utils.GreenDaoUtil;
import com.pengxh.app.weatherplus.utils.OtherUtil;
import com.pengxh.app.weatherplus.widgets.DialProgress;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseNormalActivity implements IWeatherView, OnClickListener {

    private static final String TAG = "MainActivity";

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
    @BindView(R.id.mDialProgress_air_aqi)
    DialProgress mDialProgressAirAqi;
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

    @BindView(R.id.mGridView_life)
    GridView mGridView_life;

    private WeatherPresenterImpl weatherPresenter;
    private ProgressDialog progressDialog;

    @Override
    public void initView() {
        setContentView(R.layout.activity_main);
        /**
         * 解决页面太长，ScrollView默认不能置顶的问题
         * */
        mTextViewRealtimeCityName.setFocusable(true);
        mTextViewRealtimeCityName.setFocusableInTouchMode(true);
        mTextViewRealtimeCityName.requestFocus();
    }

    @Override
    public void init() {
        //获取天气数据
        weatherPresenter = new WeatherPresenterImpl(this);
    }

    @Override
    public void initEvent() {
        String district = OtherUtil.getValue(this, "district");
        Log.d(TAG, "从sp中获取到定位点: " + district);
        if (TextUtils.isEmpty(district)) {
            ToastUtil.showBeautifulToast("获取天气失败，请稍后再试", ToastUtil.ERROR);
        } else {
            List<CityDaoBean> beanList = GreenDaoUtil.queryCity(district);
            Log.d(TAG, "从数据库中获取cityDaoBean: " + beanList);
            if (beanList.size() > 0) {
                CityDaoBean cityDaoBean = beanList.get(0);
                weatherPresenter.onReadyRetrofitRequest(
                        district,
                        Integer.parseInt(cityDaoBean.getCityid()),
                        Integer.parseInt(cityDaoBean.getCitycode())
                );
            } else {
                ToastUtil.showBeautifulToast("获取天气失败，请稍后再试", ToastUtil.ERROR);
            }
        }
    }

    @Override
    public void showProgress() {
        if (progressDialog == null) {
            show("正在加载天气数据...");
        }
    }

    @Override
    public void hideProgress() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void show(String msg) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(msg);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    @Override
    public void showNetWorkData(NetWeatherBean weatherBean) {
        if (weatherBean != null) {
            // TODO 显示当天的详细天气情况
            NetWeatherBean.ResultBeanX.ResultBean resultBean = weatherBean.getResult().getResult();
            bindResultData(resultBean);

            // TODO 显示当天24h的天气情况
            List<NetWeatherBean.ResultBeanX.ResultBean.HourlyBean> hourlyBeanList = weatherBean.getResult().getResult()
                    .getHourly();
            bindHourlyData(hourlyBeanList);

            // TODO 显示一周内的天气情况
            List<NetWeatherBean.ResultBeanX.ResultBean.DailyBean> dailyBeanList = weatherBean.getResult().getResult()
                    .getDaily();
            bindDailyData(dailyBeanList);

            // TODO 显示详细空气质量
            NetWeatherBean.ResultBeanX.ResultBean.AqiBean aqiBean = weatherBean.getResult().getResult().getAqi();
            bindAqiData(aqiBean);

            // TODO 绑定GridView
            List<NetWeatherBean.ResultBeanX.ResultBean.IndexBean> indexBeanList = weatherBean.getResult().getResult()
                    .getIndex();
            bindIndexData(indexBeanList);
        } else {
            ToastUtil.showBeautifulToast("获取数据失败，请重试", 5);
        }
    }

    private void bindResultData(NetWeatherBean.ResultBeanX.ResultBean resultBean) {
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

    private void bindHourlyData(List<NetWeatherBean.ResultBeanX.ResultBean.HourlyBean> hourlyBeanList) {
        HourlyRecyclerViewAdapter adapter = new HourlyRecyclerViewAdapter(this, hourlyBeanList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);// 横向滚动
        mRecyclerViewHourly.setLayoutManager(layoutManager);
        mRecyclerViewHourly.setAdapter(adapter);
    }

    private void bindDailyData(List<NetWeatherBean.ResultBeanX.ResultBean.DailyBean> dailyBeanList) {
        WeeklyRecyclerViewAdapter adapter = new WeeklyRecyclerViewAdapter(this, dailyBeanList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewWeekly.setLayoutManager(layoutManager);
        mRecyclerViewWeekly.setAdapter(adapter);
    }

    private void bindAqiData(NetWeatherBean.ResultBeanX.ResultBean.AqiBean aqiBean) {
        mTextViewAirAqi.setText("污染指数\r\r" + aqiBean.getQuality());
        mDialProgressAirAqi.setValue(Float.parseFloat(aqiBean.getAqi()));
        mDialProgressAirAqi.setValueLevel(aqiBean.getQuality());
        mTextViewAirPM10.setText(aqiBean.getPm10());
        mTextViewAirPM2_5.setText(aqiBean.getPm2_5());
        mTextViewAirNO2.setText(aqiBean.getNo2());
        mTextViewAirSO2.setText(aqiBean.getSo2());
        mTextViewAirO3.setText(aqiBean.getO3());
        mTextViewAirCO.setText(aqiBean.getCo());
    }

    private void bindIndexData(final List<NetWeatherBean.ResultBeanX.ResultBean.IndexBean> indexBeanList) {
        GridViewAdapter mGridViewAdapter = new GridViewAdapter(this, indexBeanList);
        mGridView_life.setAdapter(mGridViewAdapter);
        OtherUtil.measureViewHeight(this, mGridView_life);// 计算GridView的实际高度
        mGridView_life.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String iname = indexBeanList.get(position).getIname();
                String detail = indexBeanList.get(position).getDetail();
                new AlertView(iname, detail, null, new String[]{"确定"}, null, MainActivity.this,
                        AlertView.Style.Alert, new AlertViewItemClickListener()).show();
            }
        });
    }

    class AlertViewItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(Object o, int position) {

        }
    }

    @OnClick(R.id.mImageView_realtime_add)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mImageView_realtime_add:
                startActivity(new Intent(this, CityListActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        weatherPresenter.onUnsubscribe();
    }
}