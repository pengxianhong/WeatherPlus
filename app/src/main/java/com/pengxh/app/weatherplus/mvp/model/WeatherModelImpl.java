package com.pengxh.app.weatherplus.mvp.model;

import android.util.Log;

import com.pengxh.app.weatherplus.bean.NetWeatherBean;
import com.pengxh.app.weatherplus.mvp.retrofit.RetrofitServiceManager;
import com.pengxh.app.weatherplus.utils.Constant;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class WeatherModelImpl implements IWeatherModel {

    private static final String TAG = "WeatherModelImpl";
    private OnWeatherListener weatherListener;

    public WeatherModelImpl(OnWeatherListener listener) {
        this.weatherListener = listener;
    }

    /**
     * 数据回调接口
     */
    public interface OnWeatherListener {
        void onSuccess(NetWeatherBean weatherBean);

        void onFailure(Throwable throwable);
    }

    @Override
    public Subscription sendRetrofitRequest(String city, int cityid, int citycode) {
        /**
         * 实体类写父类，一定不能详细到子类
         * */
        Observable<NetWeatherBean> observable = RetrofitServiceManager.getWeatherData(Constant.BASE_URL, city, cityid, citycode);
        Subscription subscribe = observable
                .subscribeOn(Schedulers.io())//在io线程获取数据
                .observeOn(AndroidSchedulers.mainThread())//回调给主线程，异步;
                .subscribe(new Observer<NetWeatherBean>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted ===============> 数据请求完毕");
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (weatherListener != null) {
                            weatherListener.onFailure(e);
                        }
                    }

                    @Override
                    public void onNext(NetWeatherBean weatherBean) {
                        if (weatherListener != null) {
                            weatherListener.onSuccess(weatherBean);
                            Log.d(TAG, "onNext ===============> " + weatherBean.getResult().getResult().getCity());
                        }
                    }
                });
        return subscribe;
    }
}
