package com.pengxh.app.weatherplus.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.viewpager.widget.ViewPager;

import com.gyf.immersionbar.ImmersionBar;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.utils.DensityUtil;
import com.pengxh.app.weatherplus.R;
import com.pengxh.app.weatherplus.service.LocationService;
import com.pengxh.app.weatherplus.utils.SQLiteUtil;
import com.pengxh.app.weatherplus.widgets.EasyPopupWindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseNormalActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    @BindView(R.id.layoutView)
    LinearLayout layoutView;
    @BindView(R.id.mMainViewPager)
    ViewPager mMainViewPager;
    @BindView(R.id.mLlIndicator)
    LinearLayout mLlIndicator;
    private List<String> items = Arrays.asList("管理城市", "更新间隔");
    private Intent locationIntent = null;

    private SQLiteUtil sqLiteUtil;
    private PageNumberUpdateBroadcast updateBroadcast = null;

    @Override
    public int initLayoutView() {
        return R.layout.activity_main;
    }

    @Override
    public void initData() {
        ImmersionBar.with(this).statusBarColor(R.color.statusBar_color).fitsSystemWindows(true).init();
        locationIntent = new Intent(this, LocationService.class);
        startService(locationIntent);
        sqLiteUtil = SQLiteUtil.getInstance();
    }

    @Override
    public void initEvent() {

    }

    @OnClick(R.id.manageCity)
    @Override
    public void onClick(View v) {
        EasyPopupWindow easyPopupWindow = new EasyPopupWindow(this, items);
        easyPopupWindow.setPopupWindowClickListener(position -> {
            if (position == 0) {
                startActivity(new Intent(this, CityListActivity.class));
            } else if (position == 1) {

            }
        });
        easyPopupWindow.setBackgroundDrawable(null);
        easyPopupWindow.showAsDropDown(layoutView,
                layoutView.getWidth() - easyPopupWindow.getWidth() - DensityUtil.dp2px(this, 15),
                DensityUtil.dp2px(this, 40));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (updateBroadcast != null) {
            unregisterReceiver(updateBroadcast);
        }
        if (locationIntent == null) {
            return;
        }
        stopService(locationIntent);
    }

    class PageNumberUpdateBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals("action.updatePageNumber")) {

            }
        }
    }

    private class WeatherPageChangeListener implements ViewPager.OnPageChangeListener {

        private int mPageCount;//页数
        private List<ImageView> mImgList;//保存img总个数
        private int img_select;
        private int img_unSelect;

        private WeatherPageChangeListener(Context context, LinearLayout mLlIndicator, int pageCount) {
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