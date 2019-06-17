package com.pengxh.app.weatherplus.ui;

import android.util.Log;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.weatherplus.R;
import com.pengxh.app.weatherplus.utils.OtherUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;

public class SelectCityActivity extends BaseNormalActivity {

    @BindView(R.id.mTextView_current_location)
    TextView mTextView_current_location;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
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
                    String street = aMapLocation.getStreet();//街道信息
                    //获取定位时间
                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                    Date date = new Date(aMapLocation.getTime());
                    String time = df.format(date);

                    Log.d("SelectCityActivity", "当前定位点的详细信息[\r\n" +
                            "经度：" + longitude + "\r\n" +
                            "纬度：" + latitude + "\r\n" +
                            "地址：" + address + "\r\n" +
                            "国家：" + country + "\r\n" +
                            "省：" + province + "\r\n" +
                            "城市：" + city + "\r\n" +
                            "城区：" + district + "\r\n" +
                            "街道：" + street + "]");
                    OtherUtil.saveValues(SelectCityActivity.this, time, district);
                    mTextView_current_location.setText(district);
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("SelectCityActivity",
                            "location Error, ErrCode:" + aMapLocation.getErrorCode() +
                                    ", errInfo:" + aMapLocation.getErrorInfo());
                }
            }
        }
    };

    @Override
    public void initView() {
        setContentView(R.layout.activity_selectcity);
    }

    @Override
    public void init() {
        //初始化定位
        mLocationClient = new AMapLocationClient(this);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
    }

    @Override
    public void initEvent() {
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
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
    }
}