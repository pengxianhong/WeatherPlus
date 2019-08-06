package com.pengxh.app.weatherplus.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.weatherplus.R;
import com.pengxh.app.weatherplus.ui.fragment.WeatherFragment;
import com.pengxh.app.weatherplus.utils.SaveKeyValues;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class TestActivity extends BaseNormalActivity implements View.OnClickListener {

    private static final String TAG = "TestActivity";

    @BindView(R.id.mTextView_realtime_cityName)
    TextView mTextViewRealtimeCityName;
    @BindView(R.id.mTextView_realtime_date)
    TextView mTextViewRealtimeDate;
    @BindView(R.id.TextView_realtime_week)
    TextView mTextViewRealtimeWeek;
    @BindView(R.id.mTestViewPager)
    ViewPager mTestViewPager;
    @BindView(R.id.mLlIndicator)
    LinearLayout mLlIndicator;

    private List<Fragment> fragmentList = new ArrayList<>();

    @Override
    public void initView() {
        setContentView(R.layout.activity_test);
    }

    @Override
    public void init() {
        fragmentList.add(new WeatherFragment());
        fragmentList.add(new WeatherFragment());

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    try {
                        Thread.sleep(1000);
                        /**
                         * 首次加载sp可能会加载不出来标题，线程控制规避此问题
                         * */
                        getWeatherTitle();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void getWeatherTitle() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SaveKeyValues weatherTitle = new SaveKeyValues(TestActivity.this, "weatherTitle");
                mTextViewRealtimeCityName.setText((String) weatherTitle.getValue("city", ""));
                mTextViewRealtimeDate.setText("\r\r" + weatherTitle.getValue("date", "") + "\r\r");
                mTextViewRealtimeWeek.setText((String) weatherTitle.getValue("week", ""));
            }
        });
    }

    @Override
    public void initEvent() {
        FragmentPagerAdapter adapter = new WeatherPageAdapter(getSupportFragmentManager(), fragmentList);
        mTestViewPager.setAdapter(adapter);
        mTestViewPager.setOnPageChangeListener(new WeatherPageChangeListener(this, mLlIndicator, fragmentList.size()));
    }

    @OnClick({R.id.mImageView_realtime_add})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mImageView_realtime_add:
                startActivity(new Intent(this, CityListActivity.class));
                break;
            default:
                break;
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

            final int imgSize = 15;

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
        }

        @Override
        public void onPageScrollStateChanged(int position) {

        }
    }

    class WeatherPageAdapter extends FragmentPagerAdapter {
        private List<Fragment> pageList;

        WeatherPageAdapter(FragmentManager fm, List<Fragment> pageList) {
            super(fm);
            this.pageList = pageList;
        }

        @Override
        public Fragment getItem(int position) {
            return pageList.get(position);
        }

        @Override
        public int getCount() {
            return pageList.size();
        }
    }
}
