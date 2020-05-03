package com.pengxh.app.weatherplus.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pengxh.app.multilib.widget.swipemenu.BaseSwipeListAdapter;
import com.pengxh.app.weatherplus.R;
import com.pengxh.app.weatherplus.bean.CityWeatherBean;
import com.pengxh.app.weatherplus.bean.WeatherBean;
import com.pengxh.app.weatherplus.utils.OtherUtil;

import java.util.List;

/**
 * Created by Administrator on 2019/7/13.
 */

public class CityListAdapter extends BaseSwipeListAdapter {
    private List<CityWeatherBean> listWeatherBeans;
    private LayoutInflater inflater;

    public CityListAdapter(Context context, List<CityWeatherBean> list) {
        this.listWeatherBeans = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listWeatherBeans == null ? 0 : listWeatherBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return listWeatherBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CityListHolder itemHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_current_city, null);
            itemHolder = new CityListHolder();
            itemHolder.mCityName = convertView.findViewById(R.id.mCityName);
            itemHolder.mCityQuality = convertView.findViewById(R.id.mCityQuality);
            itemHolder.mCityImage = convertView.findViewById(R.id.mCityImage);
            itemHolder.mCityWeather = convertView.findViewById(R.id.mCityWeather);
            itemHolder.mCityLowTemp = convertView.findViewById(R.id.mCityLowTemp);
            itemHolder.mCityHighTemp = convertView.findViewById(R.id.mCityHighTemp);
            convertView.setTag(itemHolder);
        } else {
            itemHolder = (CityListHolder) convertView.getTag();
        }
        itemHolder.bindHolder(listWeatherBeans.get(position).getWeather());
        return convertView;
    }

    static class CityListHolder {
        private TextView mCityName;
        private TextView mCityQuality;
        private ImageView mCityImage;
        private TextView mCityWeather;
        private TextView mCityLowTemp;
        private TextView mCityHighTemp;

        @SuppressLint("SetTextI18n")
        void bindHolder(String weather) {
            Gson gson = new Gson();
            WeatherBean.ResultBeanX.ResultBean bean = gson.fromJson(weather, WeatherBean.class).getResult().getResult();

            mCityName.setText(bean.getCity());
            mCityQuality.setText(bean.getAqi().getQuality());
            mCityQuality.setBackgroundColor(Color.parseColor(bean.getAqi().getAqiinfo().getColor()));
            mCityImage.setImageResource(OtherUtil.getImageResource(bean.getImg()));
            mCityWeather.setText(bean.getWeather());
            mCityLowTemp.setText(bean.getTemplow() + "℃~");
            mCityHighTemp.setText(bean.getTemphigh() + "℃");
        }
    }
}
