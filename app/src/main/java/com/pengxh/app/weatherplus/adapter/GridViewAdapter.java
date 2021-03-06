package com.pengxh.app.weatherplus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pengxh.app.weatherplus.R;
import com.pengxh.app.weatherplus.bean.WeatherBean;
import com.pengxh.app.weatherplus.utils.Constant;

import java.util.List;

/**
 * Created by Administrator on 2019/5/26.
 */

public class GridViewAdapter extends BaseAdapter {

    private List<WeatherBean.ResultBeanX.ResultBean.IndexBean> indexBeanList;
    private LayoutInflater mInflater;

    public GridViewAdapter(Context mContext, List<WeatherBean.ResultBeanX.ResultBean.IndexBean> indexBeanList) {
        this.indexBeanList = indexBeanList;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return indexBeanList == null ? 0 : indexBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return indexBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MVPGridViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_life_gridview, null);
            holder = new MVPGridViewHolder();
            holder.mImageView_life_tipsIcon = convertView.findViewById(R.id.mImageView_life_tipsIcon);
            holder.mTextView_life_tipsValue = convertView.findViewById(R.id.mTextView_life_tipsValue);
            holder.mTextView_life_tipsTitle = convertView.findViewById(R.id.mTextView_life_tipsTitle);
            convertView.setTag(holder);
        } else {
            holder = (MVPGridViewHolder) convertView.getTag();
        }
        holder.bindData(indexBeanList.get(position), position);
        return convertView;
    }

    static class MVPGridViewHolder {
        private ImageView mImageView_life_tipsIcon;
        private TextView mTextView_life_tipsValue;
        private TextView mTextView_life_tipsTitle;


        void bindData(WeatherBean.ResultBeanX.ResultBean.IndexBean indexBean, int index) {
            mImageView_life_tipsIcon.setImageResource(Constant.ICONS[index]);
            mTextView_life_tipsValue.setText(indexBean.getIvalue());
            mTextView_life_tipsTitle.setText(indexBean.getIname());
        }
    }
}