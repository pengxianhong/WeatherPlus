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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aihook.alertview.library.AlertView;
import com.aihook.alertview.library.OnItemClickListener;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.utils.ToastUtil;
import com.pengxh.app.weatherplus.R;
import com.pengxh.app.weatherplus.adapter.GridViewAdapter;
import com.pengxh.app.weatherplus.adapter.HourlyRecyclerViewAdapter;
import com.pengxh.app.weatherplus.adapter.WeeklyRecyclerViewAdapter;
import com.pengxh.app.weatherplus.bean.AllCityBean;
import com.pengxh.app.weatherplus.bean.NetWeatherBean;
import com.pengxh.app.weatherplus.mvp.presenter.WeatherPresenterImpl;
import com.pengxh.app.weatherplus.mvp.view.IWeatherView;
import com.pengxh.app.weatherplus.utils.GreenDaoUtil;
import com.pengxh.app.weatherplus.utils.OtherUtil;
import com.pengxh.app.weatherplus.widgets.CustomGridView;
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

    @BindView(R.id.mCustomGridView_life)
    CustomGridView mCustomGridView_life;

    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationOption = null;
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
        getLocaltion();
        //获取天气数据
        weatherPresenter = new WeatherPresenterImpl(this);
    }

    @Override
    public void initEvent() {
        getWeather();
    }

    private void getWeather() {
        String district = OtherUtil.getValue(this, "district");
        Log.d(TAG, "getLocaltion: " + district);
        if (TextUtils.isEmpty(district)) {
            ToastUtil.showBeautifulToast("定位失败，请刷新重试下", ToastUtil.ERROR);
        } else {
            List<AllCityBean> beanList = GreenDaoUtil.queryCity(district);
            Log.d(TAG, "beanList.size(): " + beanList.size());
            if (beanList.size() > 0) {
                AllCityBean allCityBean = beanList.get(0);
                weatherPresenter.onReadyRetrofitRequest(
                        district,
                        Integer.parseInt(allCityBean.getCityid()),
                        Integer.parseInt(allCityBean.getCitycode()));
            } else {
                ToastUtil.showBeautifulToast("获取天气失败，请稍后再试", ToastUtil.ERROR);
            }
        }
    }

    @Override
    public void showProgress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
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

            //TODO 保存简单的天气信息
            String city = resultBean.getCity();
            String quality = aqiBean.getQuality();
            String color = aqiBean.getAqiinfo().getColor();
            String img = resultBean.getImg();
            String weather = resultBean.getWeather();
            String templow = resultBean.getTemplow();
            String temphigh = resultBean.getTemphigh();

            GreenDaoUtil.saveSimpleWeather(city, quality, color, img, weather, templow, temphigh);
        } else {
            ToastUtil.showBeautifulToast("获取数据失败，请重试", ToastUtil.ERROR);
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
        mCustomGridView_life.setAdapter(mGridViewAdapter);
        OtherUtil.measureViewHeight(this, mCustomGridView_life);// 计算GridView的实际高度
        mCustomGridView_life.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

    @OnClick({R.id.mImageView_realtime_add, R.id.mTextView_realtime_update})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mImageView_realtime_add:
                startActivity(new Intent(this, CityListActivity.class));
                break;
            case R.id.mTextView_realtime_update:
                getWeather();
                break;
            default:
                break;
        }
    }

    private void getLocaltion() {
        mLocationClient = new AMapLocationClient(this);
        mLocationOption = new AMapLocationClientOption();
        mLocationClient.setLocationListener(mLocationListener);
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(60 * 1000);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(2000);
        if (null != mLocationClient) {
            mLocationClient.setLocationOption(mLocationOption);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }
    }

    private AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                //解析amapLocation获取相应内容。
                String district = aMapLocation.getDistrict();//城区信息
                OtherUtil.saveValue(MainActivity.this, district);
            } else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e(TAG, "location Error, ErrCode:" + aMapLocation.getErrorCode() +
                        ", errInfo:" + aMapLocation.getErrorInfo());
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        weatherPresenter.onUnsubscribe();
        mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
    }
}