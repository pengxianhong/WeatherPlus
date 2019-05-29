package com.pengxh.app.weatherplus.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;

import com.pengxh.app.multilib.utils.DensityUtil;
import com.pengxh.app.weatherplus.R;

public class OtherUtil {
    /**
     * 解决ScrollView嵌套另一个可滑动的View时，高度异常的问题
     */
    public static <T extends AbsListView> void measureViewHeight(Context mContext, T view) {
        ListAdapter adapter = view.getAdapter();
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (adapter == null) {
            return;
        }
        int totalHeight = 0;
        View v;
        for (int i = 0; i < adapter.getCount(); i++) {
            v = adapter.getView(i, null, view);
            int i1 = View.MeasureSpec.makeMeasureSpec(DensityUtil.getScreenWidth(mContext), View.MeasureSpec.EXACTLY);
            int i2 = View.MeasureSpec.makeMeasureSpec(i1, View.MeasureSpec.UNSPECIFIED);
            v.measure(i1, i2);
            totalHeight += v.getMeasuredHeight();
        }
        params.height = totalHeight + (view.getLayoutDirection() * (adapter.getCount() - 1));
        view.setLayoutParams(params);
    }

    public static int getImageResource(Context mContext, String imgID) {
        switch (imgID) {
            case "0":
                return R.mipmap.a0;
            case "1":
                return R.mipmap.a1;
            case "2":
                return R.mipmap.a2;
            case "3":
                return R.mipmap.a3;
            case "4":
                return R.mipmap.a4;
            case "5":
                return R.mipmap.a5;
            case "6":
                return R.mipmap.a6;
            case "7":
                return R.mipmap.a7;
            case "8":
                return R.mipmap.a8;
            case "9":
                return R.mipmap.a9;
            case "10":
                return R.mipmap.a10;
            case "11":
                return R.mipmap.a11;
            case "12":
                return R.mipmap.a12;
            case "13":
                return R.mipmap.a13;
            case "14":
                return R.mipmap.a14;
            case "15":
                return R.mipmap.a15;
            case "16":
                return R.mipmap.a16;
            case "17":
                return R.mipmap.a17;
            case "18":
                return R.mipmap.a18;
            case "19":
                return R.mipmap.a19;
            case "20":
                return R.mipmap.a20;
            case "21":
                return R.mipmap.a21;
            case "22":
                return R.mipmap.a22;
            case "23":
                return R.mipmap.a23;
            case "24":
                return R.mipmap.a24;
            case "25":
                return R.mipmap.a25;
            case "26":
                return R.mipmap.a26;
            case "27":
                return R.mipmap.a27;
            case "28":
                return R.mipmap.a28;
            case "29":
                return R.mipmap.a29;
            case "30":
                return R.mipmap.a30;
            case "31":
                return R.mipmap.a31;
            case "32":
                return R.mipmap.a32;
            case "39":
                return R.mipmap.a39;
            case "49":
                return R.mipmap.a49;
            case "53":
                return R.mipmap.a53;
            case "54":
                return R.mipmap.a54;
            case "55":
                return R.mipmap.a55;
            case "56":
                return R.mipmap.a56;
            case "57":
                return R.mipmap.a57;
            case "58":
                return R.mipmap.a58;
            case "301":
                return R.mipmap.a301;
            case "302":
                return R.mipmap.a302;
        }
        return R.mipmap.a99;
    }
}
