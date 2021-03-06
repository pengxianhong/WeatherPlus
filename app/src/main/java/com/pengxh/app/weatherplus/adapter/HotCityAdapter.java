package com.pengxh.app.weatherplus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pengxh.app.multilib.utils.ColorUtil;
import com.pengxh.app.weatherplus.R;
import com.pengxh.app.weatherplus.bean.HotCityBean;

import java.util.List;

public class HotCityAdapter extends RecyclerView.Adapter {
    private List<HotCityBean> cityList;
    private LayoutInflater inflater;
    private OnItemClickListener mOnItemClickListener;

    public HotCityAdapter(Context mContext, List<HotCityBean> cityList) {
        this.cityList = cityList;
        inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new HotCityViewHolder(inflater.inflate(R.layout.item_hotcity_recyclerview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        HotCityViewHolder itemHolder = (HotCityViewHolder) viewHolder;
        itemHolder.bindHolder(cityList.get(i));
        if (mOnItemClickListener != null) {
            itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(i);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return cityList == null ? 0 : cityList.size();
    }

    static class HotCityViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView_hot_city;

        private HotCityViewHolder(View itemView) {
            super(itemView);
            mTextView_hot_city = itemView.findViewById(R.id.mTextView_hot_city);
        }

        void bindHolder(HotCityBean hotCityNameBean) {
            mTextView_hot_city.setText(hotCityNameBean.getCity());
            mTextView_hot_city.setBackgroundColor(ColorUtil.getRandomColor());
        }
    }

    /**
     * RecyclerView item 无内置点击事件，自定义一个接口实现点击事件
     */
    public interface OnItemClickListener {
        void onClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
