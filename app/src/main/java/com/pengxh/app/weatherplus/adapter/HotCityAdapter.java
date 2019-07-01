package com.pengxh.app.weatherplus.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pengxh.app.weatherplus.R;
import com.pengxh.app.weatherplus.bean.City;

import java.util.List;

public class HotCityAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<City> cityList;
    private LayoutInflater inflater;

    public HotCityAdapter(Context mContext, List<City> cityList) {
        this.mContext = mContext;
        this.cityList = cityList;
        inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new HotCityViewHolder(inflater.inflate(R.layout.item_hotcity_recyclerview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        HotCityViewHolder itemHolder = (HotCityViewHolder) viewHolder;
        itemHolder.bindHolder(cityList.get(i));
    }

    @Override
    public int getItemCount() {
        return cityList == null ? 0 : cityList.size();
    }

    class HotCityViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView_hot_city;

        private HotCityViewHolder(View itemView) {
            super(itemView);
            mTextView_hot_city = itemView.findViewById(R.id.mTextView_hot_city);
        }

        void bindHolder(City city) {
            mTextView_hot_city.setText(city.getCity());
        }
    }
}
