package com.pengxh.app.weatherplus.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aihook.alertview.library.AlertView;
import com.aihook.alertview.library.OnItemClickListener;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.weatherplus.R;
import com.pengxh.app.weatherplus.adapter.WeatherPageAdapter;
import com.pengxh.app.weatherplus.service.LocationService;
import com.pengxh.app.weatherplus.ui.fragment.OtherWeatherFragment;
import com.pengxh.app.weatherplus.ui.fragment.WeatherFragment;
import com.pengxh.app.weatherplus.utils.SQLiteUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseNormalActivity {

    private static final String TAG = "MainActivity";

    @BindView(R.id.mMainViewPager)
    ViewPager mMainViewPager;
    @BindView(R.id.mLlIndicator)
    LinearLayout mLlIndicator;
    private SQLiteUtil sqLiteUtil;
    private PageNumberUpdateBroadcast updateBroadcast = null;

    @Override
    public void initView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void initData() {
        sqLiteUtil = SQLiteUtil.getInstance();
    }

    @Override
    public void initEvent() {
        mHandler.sendEmptyMessage(20);
    }

    class PageNumberUpdateBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals("action.updatePageNumber")) {
                String tag = intent.getStringExtra("TAG");
                Log.d(TAG, "onReceive: => " + tag);
                switch (tag) {
                    case "add":
                        sengMsg(sqLiteUtil.loadCityList().size());
                        break;
                    case "del":
                        sengMsg(sqLiteUtil.loadCityList().size());
                        break;
                    default:
                        Log.w(TAG, "onReceive: error tag", new Throwable());
                        break;
                }
            }
        }
    }

    private void sengMsg(int number) {
        Message message = mHandler.obtainMessage();
        message.what = 30;
        message.obj = number;
        mHandler.sendMessage(message);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 30:
                    //刷新UI
                    int number = (int) msg.obj;
                    updatePage(number, true);
                    break;
                default:
                    //默认加载
                    updatePage(sqLiteUtil.loadCityList().size(), false);
                    break;
            }
        }
    };

    private void updatePage(int pageNum, boolean isUpdateNumer) {
        Log.d(TAG, "isUpdate => " + isUpdateNumer);
        List<Fragment> fragments = new LinkedList<>();
        if (!isUpdateNumer) {
            fragments.add(new WeatherFragment());
            for (int i = 1; i <= pageNum; i++) {
                fragments.add(new OtherWeatherFragment());
            }
            WeatherPageAdapter pageAdapter = new WeatherPageAdapter(getSupportFragmentManager(), fragments);
            mMainViewPager.setOffscreenPageLimit(2 * pageNum + 1);
            mMainViewPager.setAdapter(pageAdapter);
            mMainViewPager.setOnPageChangeListener(
                    //page+1是因为需要将定位点页面计算在内
                    new WeatherPageChangeListener(this, mLlIndicator, (pageNum + 1)));
        } else {
            Log.d(TAG, "pageNumber => " + (pageNum + 1));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //防止定位不准确
        int p1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int p2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int p3 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (PackageManager.PERMISSION_GRANTED != p1 || PackageManager.PERMISSION_GRANTED != p2 || PackageManager.PERMISSION_GRANTED != p3) {
            new AlertView("友情提示", "缺少定位权限，请在设置里面打开相关权限", null, new String[]{"确定"}, null, MainActivity.this,
                    AlertView.Style.Alert, new OnItemClickListener() {
                @Override
                public void onItemClick(Object o, int position) {
                    MainActivity.this.finish();
                }
            }).show();
        } else {
            startService(new Intent(this, LocationService.class));
        }
        if (updateBroadcast == null) {
            updateBroadcast = new PageNumberUpdateBroadcast();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("action.updatePageNumber");
            this.registerReceiver(updateBroadcast, intentFilter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (updateBroadcast != null) {
            this.unregisterReceiver(updateBroadcast);
        }
    }

    class WeatherPageChangeListener implements ViewPager.OnPageChangeListener {

        private int mPageCount;//页数
        private List<ImageView> mImgList;//保存img总个数
        private int img_select;
        private int img_unSelect;

        WeatherPageChangeListener(Context context, LinearLayout mLlIndicator, int pageCount) {
            this.mPageCount = pageCount;

            mImgList = new ArrayList<>();
            img_select = R.drawable.dot_enable;
            img_unSelect = R.drawable.dot_disable;

            final int imgSize = 30;

            for (int i = 0; i < mPageCount; i++) {
                ImageView imageView = new ImageView(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                //为小圆点左右添加间距
                params.leftMargin = 10;
                params.rightMargin = 10;
                //给小圆点一个默认大小
                params.height = imgSize;
                params.width = imgSize;
                if (i == 0) {
                    imageView.setBackgroundResource(img_select);
                } else {
                    imageView.setBackgroundResource(img_unSelect);
                }
                //为LinearLayout添加ImageView
                mLlIndicator.addView(imageView, params);
                mImgList.add(imageView);
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            for (int i = 0; i < mPageCount; i++) {
                //选中的页面改变小圆点为选中状态，反之为未选中
                if ((position % mPageCount) == i) {
                    (mImgList.get(i)).setBackgroundResource(img_select);
                } else {
                    (mImgList.get(i)).setBackgroundResource(img_unSelect);
                }
            }
            /**
             * position=0不能传过去
             * */
            if (position == 0) {
                Log.w(TAG, "onPageSelected: 定位点位置不传", new Throwable());
            } else {
                Intent intent = new Intent();
                intent.setAction("action.changePosition");
                intent.putExtra("position", String.valueOf((position - 1)));//不能发int型
                sendBroadcast(intent);
            }
        }

        @Override
        public void onPageScrollStateChanged(int position) {

        }
    }
}