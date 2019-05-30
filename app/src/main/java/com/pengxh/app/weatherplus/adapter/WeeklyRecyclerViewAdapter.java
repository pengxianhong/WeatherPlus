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
import com.pengxh.app.weatherplus.utils.OtherUtil;

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

        private TextView mTextView_weekly_date;
        private TextView mTextView_weekly_week;

        private ImageView mIncludeImageView_day_img;
        private TextView mIncludeTextView_day_weather;
        private TextView mIncludeTextView_day_temphigh;
        private TextView mIncludeTextView_day_winddirect;
        private TextView mIncludeTextView_day_windpower;

        private ImageView mIncludeImageView_night_img;
        private TextView mIncludeTextView_night_weather;
        private TextView mIncludeTextView_night_templow;
        private TextView mIncludeTextView_night_winddirect;
        private TextView mIncludeTextView_night_windpower;

        private WeeklyRecyclerViewHolder(View itemView) {
            super(itemView);
            mTextView_weekly_date = itemView.findViewById(R.id.mTextView_weekly_date);
            mTextView_weekly_week = itemView.findViewById(R.id.mTextView_weekly_week);
            mIncludeImageView_day_img = itemView.findViewById(R.id.mIncludeImageView_day_img);
            mIncludeTextView_day_weather = itemView.findViewById(R.id.mIncludeTextView_day_weather);
            mIncludeTextView_day_temphigh = itemView.findViewById(R.id.mIncludeTextView_day_temphigh);
            mIncludeTextView_day_winddirect = itemView.findViewById(R.id.mIncludeTextView_day_winddirect);
            mIncludeTextView_day_windpower = itemView.findViewById(R.id.mIncludeTextView_day_windpower);
            mIncludeImageView_night_img = itemView.findViewById(R.id.mIncludeImageView_night_img);
            mIncludeTextView_night_weather = itemView.findViewById(R.id.mIncludeTextView_night_weather);
            mIncludeTextView_night_templow = itemView.findViewById(R.id.mIncludeTextView_night_templow);
            mIncludeTextView_night_winddirect = itemView.findViewById(R.id.mIncludeTextView_night_winddirect);
            mIncludeTextView_night_windpower = itemView.findViewById(R.id.mIncludeTextView_night_windpower);
        }

        void bindHolder(WeatherBean.ResultBeanX.ResultBean.DailyBean dailyBean) {
            mTextView_weekly_date.setText(dailyBean.getDate());
            mTextView_weekly_week.setText(dailyBean.getWeek());
            float sunrise = Float.parseFloat(dailyBean.getSunrise());
            float sunset = Float.parseFloat(dailyBean.getSunset());

            WeatherBean.ResultBeanX.ResultBean.DailyBean.DayBean dayBean = dailyBean.getDay();
            mIncludeImageView_day_img.setImageResource(OtherUtil.getImageResource(mContext, dayBean.getImg()));
            mIncludeTextView_day_weather.setText(dayBean.getWeather());
            mIncludeTextView_day_temphigh.setText(dayBean.getTemphigh() + "℃");
            mIncludeTextView_day_winddirect.setText(dayBean.getWinddirect());
            mIncludeTextView_day_windpower.setText(dayBean.getWindpower());

            WeatherBean.ResultBeanX.ResultBean.DailyBean.NightBean nightBean = dailyBean.getNight();
            mIncludeImageView_night_img.setImageResource(OtherUtil.getImageResource(mContext, nightBean.getImg()));
            mIncludeTextView_night_weather.setText(nightBean.getWeather());
            mIncludeTextView_night_templow.setText(nightBean.getTemplow() + "℃");
            mIncludeTextView_night_winddirect.setText(nightBean.getWinddirect());
            mIncludeTextView_night_windpower.setText(nightBean.getWindpower());
        }
    }
}