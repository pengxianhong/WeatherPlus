package com.pengxh.app.weatherplus.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.pengxh.app.weatherplus.utils.SaveKeyValues;

public class LocationService extends Service {

    private static final String TAG = "LocationService";
    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationOption = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mLocationClient = new AMapLocationClient(this);
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(5 * 1000);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(10000);
        if (null != mLocationClient) {
            mLocationClient.setLocationOption(mLocationOption);
            mLocationClient.setLocationListener(mLocationListener);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }
        /**
         * 1. START_STICKY_COMPATIBILITY：START_STICKY的兼容版本，但不保证服务被kill后一定能重启。 
         * 2. START_STICKY：系统就会重新创建这个服务并且调用onStartCommand()方法，但是它不会重新传递最后的Intent对象，这适用于不执行命令的媒体播放器（或类似的服务），它只是无限期的运行着并等待工作的到来。
         * 3. START_NOT_STICKY：直到接受到新的Intent对象，才会被重新创建。这是最安全的，用来避免在不需要的时候运行你的服务。
         * 4. START_REDELIVER_INTENT：系统就会重新创建了这个服务，并且用最后的Intent对象调。等待中的Intent对象会依次被发送。这适用于如下载文件。
         * */
        return START_STICKY;
    }

    private AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                //解析amapLocation获取相应内容。
                String district = aMapLocation.getDistrict();//城区信息
                Log.d(TAG, "定位点: " + district);
                SaveKeyValues.putValue("location", "district", district);
            } else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e(TAG, "location Error, ErrCode:" + aMapLocation.getErrorCode() +
                        ", errInfo:" + aMapLocation.getErrorInfo());
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
