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

public class HourlyRecyclerViewAdapter extends RecyclerView.Adapter {

    private List<WeatherBean.ResultBeanX.ResultBean.HourlyBean> hourlyBeanList;
    private LayoutInflater inflater;

    public HourlyRecyclerViewAdapter(Context mContext, List<WeatherBean.ResultBeanX.ResultBean.HourlyBean> hourlyBeanList) {
        this.hourlyBeanList = hourlyBeanList;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getItemCount() {
        return hourlyBeanList == null ? 0 : hourlyBeanList.size();
    }

    @NonNull
    @Override
    public HourlyRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HourlyRecyclerViewHolder(inflater.inflate(R.layout.item_hourly_recyclerview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        HourlyRecyclerViewHolder itemHolder = (HourlyRecyclerViewHolder) holder;
        itemHolder.bindHolder(hourlyBeanList.get(position));
    }

    static class HourlyRecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView mRecyclerView_hourly_time;
        private TextView mRecyclerView_hourly_weather;
        private ImageView mRecyclerView_hourly_img;
        private TextView mRecyclerView_hourly_temp;

        private HourlyRecyclerViewHolder(View itemView) {
            super(itemView);
            mRecyclerView_hourly_time = itemView.findViewById(R.id.mRecyclerView_hourly_time);
            mRecyclerView_hourly_weather = itemView.findViewById(R.id.mRecyclerView_hourly_weather);
            mRecyclerView_hourly_img = itemView.findViewById(R.id.mRecyclerView_hourly_img);
            mRecyclerView_hourly_temp = itemView.findViewById(R.id.mRecyclerView_hourly_temp);
        }

        void bindHolder(WeatherBean.ResultBeanX.ResultBean.HourlyBean hourlyBean) {
            //time : 16:00
            String time = hourlyBean.getTime();
            mRecyclerView_hourly_time.setText(time);
            mRecyclerView_hourly_weather.setText(hourlyBean.getWeather());
            //分割时间
            int hour = Integer.parseInt(time.split(":")[0]);
            mRecyclerView_hourly_img.setImageResource(OtherUtil.getImageResource(hour, hourlyBean.getImg()));
            mRecyclerView_hourly_temp.setText(hourlyBean.getTemp() + "°");
        }
    }
}