package com.pengxh.app.weatherplus.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aihook.alertview.library.AlertView;
import com.alibaba.fastjson.JSONObject;
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
import com.pengxh.app.weatherplus.utils.OtherUtil;
import com.pengxh.app.weatherplus.utils.SQLiteUtil;
import com.pengxh.app.weatherplus.widgets.DashboardView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/5/18 9:59
 */
@SuppressLint("SetTextI18n")
public class WeatherPageFragment extends BasePageFragment implements IWeatherView {

    private static final String TAG = "PageFragment";
    //刷新View
    @BindView(R.id.weatherRefreshLayout)
    SmartRefreshLayout weatherRefreshLayout;
    //实时天气View
    @BindView(R.id.realtimeWeatherImg)
    ImageView realtimeWeatherImg;
    @BindView(R.id.realtimeTemp)
    TextView realtimeTemp;
    @BindView(R.id.realtimeWeather)
    TextView realtimeWeather;
    @BindView(R.id.qualityView)
    CardView qualityView;
    @BindView(R.id.realtimeQuality)
    TextView realtimeQuality;
    @BindView(R.id.realtimeLowTemp)
    TextView realtimeLowTemp;
    @BindView(R.id.realtimeHighTemp)
    TextView realtimeHighTemp;
    @BindView(R.id.locationView)
    ImageView locationView;
    @BindView(R.id.currentLocationView)
    TextView currentLocationView;
    @BindView(R.id.realtimeUpdateTime)
    TextView realtimeUpdateTime;
    @BindView(R.id.realtimeWindDirect)
    TextView realtimeWindDirect;
    @BindView(R.id.realtimeWindSpeed)
    TextView realtimeWindSpeed;
    @BindView(R.id.realtimeHumidity)
    TextView realtimeHumidity;
    //当天天气View
    @BindView(R.id.mRecyclerView_hourly)
    RecyclerView mRecyclerViewHourly;
    //本周天气View
    @BindView(R.id.mRecyclerView_weekly)
    RecyclerView mRecyclerViewWeekly;
    //污染指数View
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
    //生活指数View
    @BindView(R.id.mCustomGridView)
    CustomGridView mCustomGridView;

    private Unbinder unbinder;
    private Context context;
    private WeatherPresenterImpl weatherPresenter;
    private SQLiteUtil sqLiteUtil;
    private boolean isRefresh = false;
    private String location;

    public static WeatherPageFragment newInstance(String title) {
        WeatherPageFragment fragment = new WeatherPageFragment();
        Bundle args = new Bundle();
        args.putString("fragment_title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(this.getContext()).inflate(R.layout.fragment_weather, null);
        unbinder = ButterKnife.bind(this, view);
        context = getContext();
        initEvent();
        return view;
    }

    private void initEvent() {
        String currentLocation = (String) SaveKeyValues.getValue("location", "");
        location = getArguments().getString("fragment_title");
        currentLocationView.setText(location);
        if (!currentLocation.equals(location)) {
            locationView.setVisibility(View.GONE);
        } else {
            locationView.setVisibility(View.VISIBLE);
        }
        sqLiteUtil = SQLiteUtil.getInstance();
        weatherPresenter = new WeatherPresenterImpl(this);
        //解决页面太长，ScrollView默认不能置顶的问题
        realtimeWeatherImg.setFocusable(true);
        realtimeWeatherImg.setFocusableInTouchMode(true);
        realtimeWeatherImg.requestFocus();
        //禁止上拉加载更多
        weatherRefreshLayout.setEnableLoadMore(false);
        weatherRefreshLayout.setOnRefreshListener(refreshLayout -> {
            CityInfoBean.ResultBeanX.ResultBean cityBean = sqLiteUtil.queryCityInfo(location);
            if (cityBean != null) {
                weatherPresenter.onReadyRetrofitRequest(cityBean.getCity(), cityBean.getCityid(), Integer.parseInt(cityBean.getCitycode()));
            }
            isRefresh = true;
        });
    }

    @Override
    public void fetchData() {
        /**
         * 在这里请求网络。
         * */
        CityInfoBean.ResultBeanX.ResultBean cityBean = sqLiteUtil.queryCityInfo(location);
        if (cityBean != null) {
            weatherPresenter.onReadyRetrofitRequest(cityBean.getCity(), cityBean.getCityid(), Integer.parseInt(cityBean.getCitycode()));
        }
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {
        if (isRefresh) {
            weatherRefreshLayout.finishRefresh();
        }
    }

    @Override
    public void showNetWorkData(WeatherBean weatherBean) {
        if (weatherBean != null) {
            String city = weatherBean.getResult().getResult().getCity();//用于判断城市是否存在于表中，如果存在就更新天气数据
            String jsonString = JSONObject.toJSONString(weatherBean);
            sqLiteUtil.saveCityListWeather(city, jsonString);

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
            List<WeatherBean.ResultBeanX.ResultBean.IndexBean> indexBeanList = weatherBean.getResult().getResult().getIndex();
            bindIndexData(indexBeanList);
        } else {
            EasyToast.showToast("获取数据失败，请重试", EasyToast.ERROR);
        }
    }

    private void bindResultData(WeatherBean.ResultBeanX.ResultBean resultBean) {
        realtimeWeatherImg.setImageResource(OtherUtil.getImageResource(OtherUtil.getCurrentHour(), resultBean.getImg()));
        realtimeTemp.setText(resultBean.getTemp() + "°");
        realtimeWeather.setText(resultBean.getWeather());
        realtimeLowTemp.setText(resultBean.getTemplow() + "°~");
        realtimeHighTemp.setText(resultBean.getTemphigh() + "°");
        currentLocationView.setText(resultBean.getCity());
        realtimeUpdateTime.setText(resultBean.getUpdatetime() + "\r\r更新");
        realtimeWindDirect.setText(resultBean.getWinddirect() + resultBean.getWindpower());
        realtimeWindSpeed.setText(resultBean.getWindspeed() + "米/秒");
        realtimeHumidity.setText(resultBean.getHumidity() + "%");
        realtimeQuality.setText(resultBean.getAqi().getQuality());
        qualityView.setCardBackgroundColor(Color.parseColor(resultBean.getAqi().getAqiinfo().getColor()));
    }

    private void bindHourlyData(List<WeatherBean.ResultBeanX.ResultBean.HourlyBean> hourlyBeanList) {
        HourlyRecyclerViewAdapter adapter = new HourlyRecyclerViewAdapter(context, hourlyBeanList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);// 横向滚动
        mRecyclerViewHourly.setLayoutManager(layoutManager);
        mRecyclerViewHourly.setAdapter(adapter);
    }

    private void bindDailyData(List<WeatherBean.ResultBeanX.ResultBean.DailyBean> dailyBeanList) {
        WeeklyRecyclerViewAdapter adapter = new WeeklyRecyclerViewAdapter(context, dailyBeanList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
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
        GridViewAdapter mGridViewAdapter = new GridViewAdapter(context, indexBeanList);
        mCustomGridView.setAdapter(mGridViewAdapter);
        mCustomGridView.setOnItemClickListener((parent, view, position, id) -> {
            String iname = indexBeanList.get(position).getIname();
            String detail = indexBeanList.get(position).getDetail();
            new AlertView(iname, detail, null, new String[]{"确定"}, null, context, AlertView.Style.Alert, null).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
