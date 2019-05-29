package com.pengxh.app.weatherplus.mvp.presenter;

import com.pengxh.app.weatherplus.bean.WeatherBean;
import com.pengxh.app.weatherplus.mvp.BasePresenter;
import com.pengxh.app.weatherplus.mvp.model.WeatherModelImpl;
import com.pengxh.app.weatherplus.mvp.view.IWeatherView;

public class WeatherPresenterImpl extends BasePresenter implements IWeatherPresenter, WeatherModelImpl.OnWeatherListener {

    private IWeatherView iWeatherView;
    private WeatherModelImpl weatherModel;

    public WeatherPresenterImpl(IWeatherView view) {
        this.iWeatherView = view;
        weatherModel = new WeatherModelImpl(this);
    }

    /**
     * 唤醒订阅
     */
    @Override
    public void onReadyRetrofitRequest(String city, int cityid, int citycode) {
        iWeatherView.showProgress();
        addSubscription(weatherModel.sendRetrofitRequest(city, cityid, citycode));
    }

    @Override
    public void onSuccess(WeatherBean weatherBean) {
        iWeatherView.hideProgress();
        /**
         * 将返回的数据传递给View并显示在Activity/Fragment上面
         */
        iWeatherView.showNetWorkData(weatherBean);
    }

    @Override
    public void onFailure(Throwable throwable) {
        iWeatherView.hideProgress();
    }
}
