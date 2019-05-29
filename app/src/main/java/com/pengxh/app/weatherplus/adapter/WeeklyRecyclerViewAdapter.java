package com.pengxh.app.weatherplus.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pengxh.app.weatherplus.R;
import com.pengxh.app.weatherplus.bean.WeatherBean;

import java.util.List;

public class WeeklyRecyclerViewAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<WeatherBean.ResultBeanX.ResultBean.DailyBean> dailyBeanList;
    private LayoutInflater inflater;

    public WeeklyRecyclerViewAdapter(Context mContext, List<WeatherBean.ResultBeanX.ResultBean.DailyBean> dailyBeanList) {
        this.mContext = mContext;
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

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        WeeklyRecyclerViewHolder itemHolder = (WeeklyRecyclerViewHolder) holder;
        itemHolder.bindHolder(dailyBeanList.get(position));
    }

    class WeeklyRecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView mMVPRecyclerViewTime;
        private TextView mMVPRecyclerViewWeather;
        private TextView mMVPRecyclerViewTemp;
        private ImageView mMVPRecyclerViewImg;

        private WeeklyRecyclerViewHolder(View itemView) {
            super(itemView);
//            mMVPRecyclerViewTime = itemView.findViewById(R.id.mWeek);
//            mMVPRecyclerViewWeather = itemView.findViewById(R.id.mMVP_RecyclerView_weather);
//            mMVPRecyclerViewImg = itemView.findViewById(R.id.mMVP_RecyclerView_img);
//            mMVPRecyclerViewTemp = itemView.findViewById(R.id.mMVP_RecyclerView_temp);
        }

        void bindHolder(WeatherBean.ResultBeanX.ResultBean.DailyBean dailyBean) {
//            mMVPRecyclerViewTime.setText(hourlyBean.getTime());
//            mMVPRecyclerViewWeather.setText(hourlyBean.getWeather());
//            mMVPRecyclerViewImg.setImageResource(getImageResource(hourlyBean.getImg()));
//            mMVPRecyclerViewTemp.setText(hourlyBean.getTemp() + "°");
        }
    }
}