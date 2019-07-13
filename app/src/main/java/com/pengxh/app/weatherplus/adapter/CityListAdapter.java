package com.pengxh.app.weatherplus.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pengxh.app.multilib.widget.swipemenu.BaseSwipListAdapter;
import com.pengxh.app.weatherplus.R;
import com.pengxh.app.weatherplus.bean.CityManagerBean;
import com.pengxh.app.weatherplus.utils.OtherUtil;

import java.util.List;

/**
 * Created by Administrator on 2019/7/13.
 */

public class CityListAdapter extends BaseSwipListAdapter {
    private Context context;
    private List<CityManagerBean> otherCityWeather;
    private LayoutInflater inflater;

    public CityListAdapter(Context context, List<CityManagerBean> otherCityWeather) {
        this.context = context;
        this.otherCityWeather = otherCityWeather;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return otherCityWeather == null ? 0 : otherCityWeather.size();
    }

    @Override
    public Object getItem(int position) {
        return otherCityWeather.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CityListHolder itemHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_citylist, null);
            itemHolder = new CityListHolder();
            itemHolder.mTextView_citylist_city = convertView.findViewById(R.id.mTextView_citylist_city);
            itemHolder.mTextView_citylist_quality = convertView.findViewById(R.id.mTextView_citylist_quality);
            itemHolder.mImageView_citylist_img = convertView.findViewById(R.id.mImageView_citylist_img);
            itemHolder.mTextView_citylist_weather = convertView.findViewById(R.id.mTextView_citylist_weather);
            itemHolder.mTextView_citylist_templow = convertView.findViewById(R.id.mTextView_citylist_templow);
            itemHolder.mTextView_citylist_temphigh = convertView.findViewById(R.id.mTextView_citylist_temphigh);
            convertView.setTag(itemHolder);
        } else {
            itemHolder = (CityListHolder) convertView.getTag();
        }
        itemHolder.bindHolder(otherCityWeather.get(position));
        return convertView;
    }

    class CityListHolder {
        private TextView mTextView_citylist_city;
        private TextView mTextView_citylist_quality;
        private ImageView mImageView_citylist_img;
        private TextView mTextView_citylist_weather;
        private TextView mTextView_citylist_templow;
        private TextView mTextView_citylist_temphigh;

        void bindHolder(CityManagerBean bean) {
            mTextView_citylist_city.setText(bean.getCity());
            mTextView_citylist_quality.setText(bean.getQuality());
            mTextView_citylist_quality.setBackgroundColor(Color.parseColor(bean.getColor()));
            mImageView_citylist_img.setImageResource(OtherUtil.getImageResource(bean.getImg()));
            mTextView_citylist_weather.setText(bean.getWeather());
            mTextView_citylist_templow.setText(bean.getTemplow() + "℃~");
            mTextView_citylist_temphigh.setText(bean.getTemphigh() + "℃");
        }
    }
}
