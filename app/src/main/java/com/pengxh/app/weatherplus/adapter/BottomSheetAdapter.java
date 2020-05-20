package com.pengxh.app.weatherplus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.pengxh.app.multilib.utils.SaveKeyValues;
import com.pengxh.app.weatherplus.R;

import java.util.HashMap;

/**
 * @description: TODO
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/5/20 22:12
 */
public class BottomSheetAdapter extends BaseAdapter {

    private String[] periodArray;
    private LayoutInflater inflater;
    private HashMap<String, Boolean> states = new HashMap<>();//记录所有radiobutton被点击的状态

    public BottomSheetAdapter(Context mContext, String[] array) {
        this.periodArray = array;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return periodArray == null ? 0 : periodArray.length;
    }

    @Override
    public Object getItem(int position) {
        return periodArray[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BottomSheetViewHolder itemHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_bottom_recyclerview, null);
            itemHolder = new BottomSheetViewHolder();
            itemHolder.periodTextView = convertView.findViewById(R.id.periodTextView);
            itemHolder.radioButton = convertView.findViewById(R.id.radioButton);
            convertView.setTag(itemHolder);
        } else {
            itemHolder = (BottomSheetViewHolder) convertView.getTag();
        }
        itemHolder.periodTextView.setText(periodArray[position]);

        //选中上次选中的条目
        String hours = (String) SaveKeyValues.getValue("hours", "");
        if (!hours.equals("")) {
            int index = getPeriodIndex(hours);
            states.put(String.valueOf(index), true);
        }

        boolean res;
        if (getStates(position) == null || !getStates(position)) {
            res = false;
            setStates(position);
        } else {
            res = true;
        }
        itemHolder.radioButton.setChecked(res);
        return convertView;
    }

    private int getPeriodIndex(String hours) {
        for (int i = 0; i < periodArray.length; i++) {
            if (periodArray[i].equals(hours)) {
                return i;
            }
        }
        return 99;
    }

    //用于在activity中重置所有的radiobutton的状态
    public void clearStates(int position) {
        // 重置，确保最多只有一项被选中
        for (String key : states.keySet()) {
            states.put(key, false);
        }
        states.put(String.valueOf(position), true);
    }

    //用于获取状态值
    public Boolean getStates(int position) {
        return states.get(String.valueOf(position));
    }

    //设置状态值
    private void setStates(int position) {
        states.put(String.valueOf(position), false);
    }

    private static class BottomSheetViewHolder {
        private TextView periodTextView;
        private RadioButton radioButton;
    }
}
