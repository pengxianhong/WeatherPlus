package com.pengxh.app.weatherplus.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pengxh.app.weatherplus.R;
import com.pengxh.app.weatherplus.bean.WeatherBean;
import com.pengxh.app.weatherplus.utils.OtherUtil;

import java.util.List;

public class WeeklyRecyclerViewAdapter extends RecyclerView.Adapter {

    private List<WeatherBean.ResultBeanX.ResultBean.DailyBean> dailyBeanList;
    private LayoutInflater inflater;

    public WeeklyRecyclerViewAdapter(Context mContext, List<WeatherBean.ResultBeanX.ResultBean.DailyBean> dailyBeanList) {
        this.dailyBeanList = dailyBeanList;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getItemCount() {
        return dailyBeanList == null ? 0 : dailyBeanList.size();
    }

    @NonNull
    @Override
    public WeeklyRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WeeklyRecyclerViewHolder(inflater.inflate(R.layout.item_weekly_recyclerview, parent, false));
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        WeeklyRecyclerViewHolder itemHolder = (WeeklyRecyclerViewHolder) holder;
        itemHolder.bindHolder(dailyBeanList.get(position));
    }

    static class WeeklyRecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView_weekly_week;
        private ImageView mImageView_weekly_img;
        private TextView mTextView_day_temphigh;
        private TextView mTextView_night_templow;

        private WeeklyRecyclerViewHolder(View itemView) {
            super(itemView);
            mTextView_weekly_week = itemView.findViewById(R.id.mTextView_weekly_week);
            mImageView_weekly_img = itemView.findViewById(R.id.mImageView_weekly_img);
            mTextView_day_temphigh = itemView.findViewById(R.id.mTextView_day_temphigh);
            mTextView_night_templow = itemView.findViewById(R.id.mTextView_night_templow);
        }

        @SuppressLint("SetTextI18n")
        void bindHolder(WeatherBean.ResultBeanX.ResultBean.DailyBean dailyBean) {
            mTextView_weekly_week.setText(dailyBean.getWeek());

            WeatherBean.ResultBeanX.ResultBean.DailyBean.DayBean dayBean = dailyBean.getDay();
            mImageView_weekly_img.setImageResource(OtherUtil.getImageResource(dayBean.getImg()));
            mTextView_day_temphigh.setText(dayBean.getTemphigh() + "℃");

            WeatherBean.ResultBeanX.ResultBean.DailyBean.NightBean nightBean = dailyBean.getNight();
            mTextView_night_templow.setText(nightBean.getTemplow() + "℃");
        }
    }
}