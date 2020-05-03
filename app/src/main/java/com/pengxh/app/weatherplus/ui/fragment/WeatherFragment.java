package com.pengxh.app.weatherplus.ui.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aihook.alertview.library.AlertView;
import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.pengxh.app.multilib.base.BaseFragment;
import com.pengxh.app.multilib.utils.DensityUtil;
import com.pengxh.app.multilib.utils.SaveKeyValues;
import com.pengxh.app.multilib.widget.CustomGridView;
import com.pengxh.app.multilib.widget.EasyToast;
import com.pengxh.app.weatherplus.R;
import com.pengxh.app.weatherplus.adapter.GridViewAdapter;
import com.pengxh.app.weatherplus.adapter.HourlyRecyclerViewAdapter;
import com.pengxh.app.weatherplus.adapter.WeeklyRecyclerViewAdapter;
import com.pengxh.app.weatherplus.bean.CityInfoBean;
import com.pengxh.app.weatherplus.bean.WeatherBean;
import com.pengxh.app.weatherplus.mvp.presenter.WeatherPresenterImpl;
import com.pengxh.app.weatherplus.mvp.view.IWeatherView;
import com.pengxh.app.weatherplus.ui.CityListActivity;
import com.pengxh.app.weatherplus.utils.OtherUtil;
import com.pengxh.app.weatherplus.utils.SQLiteUtil;
import com.pengxh.app.weatherplus.widgets.DashboardView;
import com.pengxh.app.weatherplus.widgets.EasyPopupWindow;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint("SetTextI18n")
public class WeatherFragment extends BaseFragment implements IWeatherView, View.OnClickListener {

    private static final String TAG = "WeatherFragment";
    @BindView(R.id.layoutView)
    LinearLayout layoutView;
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

    @BindView(R.id.dashboardView)
    DashboardView dashboardView;
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

    private Context context;
    private List<String> items = Arrays.asList("管理城市", "更新间隔");
    private WeatherPresenterImpl weatherPresenter;
    private ProgressDialog progressDialog;

    @Override
    protected int setLayoutView() {
        return R.layout.fragment_weather;
    }

    @Override
    protected void initData() {
        context = getContext();
        ImmersionBar.with(this).statusBarColor(R.color.statusBar_color).fitsSystemWindows(true).init();
    }

    @Override
    protected void loadData() {
        SQLiteUtil sqLiteUtil = SQLiteUtil.getInstance();
        //获取天气数据
        weatherPresenter = new WeatherPresenterImpl(this);
        //解决页面太长，ScrollView默认不能置顶的问题
        mTextViewRealtimeQuality.setFocusable(true);
        mTextViewRealtimeQuality.setFocusableInTouchMode(true);
        mTextViewRealtimeQuality.requestFocus();

        final String currentLocation = (String) SaveKeyValues.getValue("location", "");
        if (TextUtils.isEmpty(currentLocation)) {
            EasyToast.showToast("定位失败，请重试", EasyToast.ERROR);
        } else {
            CityInfoBean.ResultBeanX.ResultBean cityBean = sqLiteUtil.queryCityInfo(currentLocation);
            if (cityBean != null) {
                getCityWeather(cityBean);
            }
        }
    }

    private void getCityWeather(CityInfoBean.ResultBeanX.ResultBean cityBean) {
        weatherPresenter.onReadyRetrofitRequest(cityBean.getCity(), cityBean.getCityid(), Integer.parseInt(cityBean.getCitycode()));
    }

    @Override
    public void showNetWorkData(WeatherBean weatherBean) {
        if (weatherBean != null) {
            //显示当天的详细天气情况
            WeatherBean.ResultBeanX.ResultBean resultBean = weatherBean.getResult().getResult();
            bindResultData(resultBean);

            //显示当天24h的天气情况
            List<WeatherBean.ResultBeanX.ResultBean.HourlyBean> hourlyBeanList = weatherBean.getResult().getResult().getHourly();
            bindHourlyData(hourlyBeanList);

            //显示一周内的天气情况
            List<WeatherBean.ResultBeanX.ResultBean.DailyBean> dailyBeanList = weatherBean.getResult().getResult().getDaily();
            bindDailyData(dailyBeanList);

            //显示详细空气质量
            WeatherBean.ResultBeanX.ResultBean.AqiBean aqiBean = weatherBean.getResult().getResult().getAqi();
            bindAqiData(aqiBean);

            //绑定GridView
            List<WeatherBean.ResultBeanX.ResultBean.IndexBean> indexBeanList = weatherBean.getResult().getResult()
                    .getIndex();
            bindIndexData(indexBeanList);

            //保存简单的天气信息
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
        mImageViewRealtimeImg.setImageResource(OtherUtil.getImageResource(resultBean.getImg()));
        mTextViewRealtimeTemp.setText(resultBean.getTemp() + "°");
        mTextViewRealtimeWeather.setText(resultBean.getWeather());
        mTextViewRealtimeTemplow.setText(resultBean.getTemplow() + "℃~");
        mTextViewRealtimeTemphigh.setText(resultBean.getTemphigh() + "℃");
        String updateTime = resultBean.getUpdatetime();
        mTextViewRealtimeUpdate.setText(updateTime.substring(5, 16) + "\r\r更新");
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
        //设置仪表盘属性
        dashboardView.setDateStrPattern("更新时间：{date}");
        dashboardView.setValueLevelPattern("{level}");
        dashboardView.setValue(Integer.parseInt(aqiBean.getAqi()), true, false);

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
        mCustomGridView_life.setOnItemClickListener((parent, view, position, id) -> {
            String iname = indexBeanList.get(position).getIname();
            String detail = indexBeanList.get(position).getDetail();
            new AlertView(iname, detail, null, new String[]{"确定"}, null, context, AlertView.Style.Alert, null).show();
        });
    }

    @OnClick(R.id.manageCity)
    @Override
    public void onClick(View v) {
        EasyPopupWindow easyPopupWindow = new EasyPopupWindow(context, items);
        easyPopupWindow.setPopupWindowClickListener(position -> {
            if (position == 0) {
                startActivity(new Intent(context, CityListActivity.class));
            } else if (position == 1) {

            }
        });
        easyPopupWindow.setBackgroundDrawable(null);
        easyPopupWindow.showAsDropDown(layoutView,
                layoutView.getWidth() - easyPopupWindow.getWidth() - DensityUtil.dp2px(context, 15),
                DensityUtil.dp2px(context, 40));
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
    public void onDestroyView() {
        super.onDestroyView();
        weatherPresenter.onUnsubscribe();
    }
}
