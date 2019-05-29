package com.pengxh.app.weatherplus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pengxh.app.weatherplus.R;
import com.pengxh.app.weatherplus.bean.WeatherBean;

import java.util.List;

/**
 * Created by Administrator on 2019/5/26.
 */

public class GridViewAdapter extends BaseAdapter {

    private List<WeatherBean.ResultBeanX.ResultBean.IndexBean> indexBeanList;
    private Context mContext;
    private LayoutInflater mInflater;

    public GridViewAdapter(Context mContext, List<WeatherBean.ResultBeanX.ResultBean.IndexBean> indexBeanList) {
        this.indexBeanList = indexBeanList;
        this.mContext = mContext;
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
            holder.mTextView_life_tipsTitle = convertView.findViewById(R.id.mTextView_life_tipsTitle);
            holder.mTextView_life_tipsValue = convertView.findViewById(R.id.mTextView_life_tipsValue);
            convertView.setTag(holder);
        } else {
            holder = (MVPGridViewHolder) convertView.getTag();
        }
        holder.bindData(indexBeanList.get(position));
        return convertView;
    }

    private class MVPGridViewHolder {
        private TextView mTextView_life_tipsTitle;
        private TextView mTextView_life_tipsValue;

        void bindData(WeatherBean.ResultBeanX.ResultBean.IndexBean indexBean) {
            mTextView_life_tipsTitle.setText(indexBean.getIname());
            mTextView_life_tipsValue.setText(indexBean.getIvalue());
        }
    }
}