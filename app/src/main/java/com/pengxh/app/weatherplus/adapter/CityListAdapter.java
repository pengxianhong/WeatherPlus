package com.pengxh.app.weatherplus.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pengxh.app.multilib.widget.swipemenu.BaseSwipListAdapter;
import com.pengxh.app.weatherplus.R;
import com.pengxh.app.weatherplus.bean.CityListWeatherBean;
import com.pengxh.app.weatherplus.bean.NetWeatherBean;
import com.pengxh.app.weatherplus.utils.OtherUtil;

import java.util.List;

/**
 * Created by Administrator on 2019/7/13.
 */

public class CityListAdapter extends BaseSwipListAdapter {

    private static final String TAG = "CityListAdapter";

    private Context context;
    private List<CityListWeatherBean> listWeatherBeans;
    private LayoutInflater inflater;

    public CityListAdapter(Context context, List<CityListWeatherBean> list) {
        this.context = context;
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
        itemHolder.bindHolder(listWeatherBeans.get(position).getWeather());
        return convertView;
    }

    class CityListHolder {
        private TextView mTextView_citylist_city;
        private TextView mTextView_citylist_quality;
        private ImageView mImageView_citylist_img;
        private TextView mTextView_citylist_weather;
        private TextView mTextView_citylist_templow;
        private TextView mTextView_citylist_temphigh;

        void bindHolder(String weather) {
//            Log.d(TAG, "bindHolder: " + weather);
            Gson gson = new Gson();
            NetWeatherBean.ResultBeanX.ResultBean bean = gson.fromJson(weather, NetWeatherBean.class).getResult().getResult();

            mTextView_citylist_city.setText(bean.getCity());
            mTextView_citylist_quality.setText(bean.getAqi().getQuality());
            mTextView_citylist_quality.setBackgroundColor(Color.parseColor(bean.getAqi().getAqiinfo().getColor()));
            mImageView_citylist_img.setImageResource(OtherUtil.getImageResource(bean.getImg()));
            mTextView_citylist_weather.setText(bean.getWeather());
            mTextView_citylist_templow.setText(bean.getTemplow() + "℃~");
            mTextView_citylist_temphigh.setText(bean.getTemphigh() + "℃");
        }
    }
}
