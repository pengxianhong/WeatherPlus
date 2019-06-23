package com.pengxh.app.weatherplus.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.weatherplus.R;
import com.pengxh.app.weatherplus.service.CityService;
import com.pengxh.app.weatherplus.utils.OtherUtil;

import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Administrator on 2019/6/23.
 */

public class WelcomeActivity extends BaseNormalActivity implements EasyPermissions.PermissionCallbacks {

    private static final String TAG = "WelcomeActivity";
    private static final int permissionCode = 999;
    private static final String[] perms = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    @Override
    public void initView() {
        setContentView(R.layout.activity_welcome);
        requirePermissions();
    }

    @Override
    public void init() {
        //初始化定位
        mLocationClient = new AMapLocationClient(this);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(60 * 1000);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(30000);
        if (null != mLocationClient) {
            mLocationClient.setLocationOption(mLocationOption);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }
    }

    @Override
    public void initEvent() {
        SharedPreferences sp = this.getSharedPreferences("fisrtConfig", Context.MODE_PRIVATE);
        boolean isFirstRun = sp.getBoolean("isFirstRun", true);
        SharedPreferences.Editor editor = sp.edit();
        Log.d(TAG, "APP: isFirstRun =====> " + isFirstRun);
        if (isFirstRun) {
            editor.putBoolean("isFirstRun", false);
            editor.apply();

            //开启后台服务将本地数据存到数据库里面，提高查询效率。不能用网络请求，数据量太大，网络请求会卡死
            startService(new Intent(this, CityService.class));
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 5; i >= 0; i--) {
                    timePikerHandler.sendEmptyMessage(i);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //可在其中解析amapLocation获取相应内容。
                    double longitude = aMapLocation.getLongitude();//获取经度
                    double latitude = aMapLocation.getLatitude();//获取纬度
                    String address = aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                    String country = aMapLocation.getCountry();//国家信息
                    String province = aMapLocation.getProvince();//省信息
                    String city = aMapLocation.getCity();//城市信息
                    String district = aMapLocation.getDistrict();//城区信息
                    //由于定位和获取天气网络请求存在时间差，所以，此处需要先存到sp
                    OtherUtil.saveValue(WelcomeActivity.this, district);
//                    Log.d(TAG, "当前定位点的详细信息[\r\n" +
//                            "经度：" + longitude + "\r\n" +
//                            "纬度：" + latitude + "\r\n" +
//                            "地址：" + address + "\r\n" +
//                            "国家：" + country + "\r\n" +
//                            "省：" + province + "\r\n" +
//                            "城市：" + city + "\r\n" +
//                            "城区：" + district + "]");
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e(TAG,
                            "location Error, ErrCode:" + aMapLocation.getErrorCode() +
                                    ", errInfo:" + aMapLocation.getErrorInfo());
                }
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler timePikerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    };

    private void requirePermissions() {
        EasyPermissions.requestPermissions(this, "", permissionCode, perms);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsGranted: " + perms);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.e(TAG, "onPermissionsDenied: " + perms);
    }

    @Override
    public void onRequestPermissionsResult(int i, @NonNull String[] strings, @NonNull int[] ints) {
        Log.e(TAG, "onRequestPermissionsResult: " + Arrays.toString(strings));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
    }
}
