package com.pengxh.app.weatherplus.test;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.utils.BroadcastManager;
import com.pengxh.app.multilib.widget.EasyToast;
import com.pengxh.app.weatherplus.R;
import com.pengxh.app.weatherplus.adapter.WeatherPageAdapter;
import com.pengxh.app.weatherplus.ui.fragment.WeatherPageFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * @description: TODO
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/5/4 19:29
 */
public class TestActivity extends BaseNormalActivity implements View.OnClickListener {

    @BindView(R.id.addCity)
    Button addCity;
    @BindView(R.id.delCity)
    Button delCity;

    private WeatherPageChangeListener pageChangeListener;
    private WeatherPageAdapter pagerAdapter;
    private ViewPager mTestViewPager;
    private LinearLayout indicatorLayout;
    private BroadcastManager broadcastManager;

    @Override
    public int initLayoutView() {
        return R.layout.activity_test;
    }

    @Override
    public void initData() {
        mTestViewPager = findViewById(R.id.mTestViewPager);
        indicatorLayout = findViewById(R.id.indicatorLayout);
        broadcastManager = BroadcastManager.getInstance(this);
        pagerAdapter = new WeatherPageAdapter(getSupportFragmentManager());
        pagerAdapter.addPage(WeatherPageFragment.newInstance("海淀区"));
        mTestViewPager.setAdapter(pagerAdapter);

        //首次启动默认显示一个点，后面启动从数据库读取数据
        Message message = indicatorHandler.obtainMessage();
        message.what = 1000;
        message.obj = 1;
        indicatorHandler.sendMessage(message);
    }

    @Override
    public void initEvent() {
        BroadcastManager.getInstance(this).addAction(new String[]{"action_addCity", "action_delCity"}, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action != null) {
                    switch (action) {
                        case "action_addCity":
                            pagerAdapter.addPage(WeatherPageFragment.newInstance(intent.getStringExtra("data")));
                            break;
                        case "action_delCity":
                            int size = pagerAdapter.getFragmentList().size();
                            if (size == 1) {
                                EasyToast.showToast("删除失败", EasyToast.ERROR);
                                return;
                            }
                            pagerAdapter.delPage(0);
                            break;
                    }
                    Message message = indicatorHandler.obtainMessage();
                    message.what = 1000;
                    message.obj = pagerAdapter.getFragmentList().size();
                    indicatorHandler.sendMessage(message);
                }
            }
        });
        pageChangeListener = new WeatherPageChangeListener(this, indicatorLayout);
        mTestViewPager.addOnPageChangeListener(pageChangeListener);
    }

    @SuppressLint("HandlerLeak")
    private Handler indicatorHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1000) {
                int pageSize = (int) msg.obj;
                pageChangeListener.setIndicator(pageSize);
            }
        }
    };

    @OnClick({R.id.addCity, R.id.delCity})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addCity:
                broadcastManager.sendBroadcast("action_addCity", "北京" + new Random().nextInt(100));
                break;
            case R.id.delCity:
                broadcastManager.sendBroadcast("action_delCity", "海淀区");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        broadcastManager.destroy("action_addCity", "action_delCity");
    }

    private static class WeatherPageChangeListener implements ViewPager.OnPageChangeListener {

        private static final int imgSize = 30;
        private static final int img_select = R.drawable.dot_enable;
        private static final int img_unSelect = R.drawable.dot_disable;
        private Context context;
        private LinearLayout mIndicator;
        private int mPageCount;
        private List<ImageView> mImgList;//保存img总个数

        private WeatherPageChangeListener(Context ctx, LinearLayout indicator) {
            this.context = ctx;
            this.mIndicator = indicator;
            mImgList = new ArrayList<>();
        }

        private void setIndicator(int size) {
            this.mPageCount = size;
            //***小点数量变化之后，一定要先清除之前加进去的View，不然会出现重复*************************//
            mImgList.clear();
            mIndicator.removeAllViews();
            //****************************//
            for (int i = 0; i < size; i++) {
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
                    imageView.setBackgroundResource(img_unSelect);
                } else {
                    imageView.setBackgroundResource(img_select);
                }
                //为LinearLayout添加ImageView
                mIndicator.addView(imageView, params);
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
                    mImgList.get(i).setBackgroundResource(img_unSelect);
                } else {
                    mImgList.get(i).setBackgroundResource(img_select);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int position) {

        }
    }
}
