package com.pengxh.app.weatherplus.utils;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;

import com.pengxh.app.weatherplus.R;

import java.text.SimpleDateFormat;
import java.util.Date;


public class OtherUtil {

    private static final String TAG = "OtherUtil";
    private static ProgressDialog progressDialog;

    public static void showProgressDialog(Context context, String title, String message) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public static void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static int getCurrentHour() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("HH");
        return Integer.parseInt(formatter.format(date));
    }

    public static int getImageResource(int hour, String imgID) {
        //hour>19||hour<5
        switch (imgID) {
            case "0":
                if (hour > 19 || hour < 5) {
                    return R.drawable.ic_y_a0;//夜间晴
                }
                return R.drawable.ic_a0;//晴
            case "1":
                if (hour > 19 || hour < 5) {
                    return R.drawable.ic_y_a1;//夜间多云
                }
                return R.drawable.ic_a1;//多云
            case "2":
                return R.drawable.ic_a2;//阴
            case "3":
                if (hour > 19 || hour < 5) {
                    return R.drawable.ic_y_a3;//夜间阵雨
                }
                return R.drawable.ic_a3;//阵雨
            case "4":
            case "5":
                return R.drawable.ic_a4;//雷阵雨
            case "6":
                return R.drawable.ic_a6;//雨夹雪
            case "7":
            case "301":
                return R.drawable.ic_a7;//小雨
            case "8":
                return R.drawable.ic_a8;//中雨
            case "9":
                return R.drawable.ic_a9;//大雨
            case "10":
                return R.drawable.ic_a10;//暴雨
            case "11":
                return R.drawable.ic_a11;//大暴雨
            case "12":
                return R.drawable.ic_a12;//特大暴雨
            case "13":
                if (hour > 19 || hour < 5) {
                    return R.drawable.ic_y_a13;//夜间阵雪
                }
                return R.drawable.ic_a13;//阵雪
            case "14":
            case "302":
                return R.drawable.ic_a14;//小雪
            case "15":
                return R.drawable.ic_a15;//中雪
            case "16":
                return R.drawable.ic_a16;//大雪
            case "17":
                return R.drawable.ic_a17;//暴雪
            case "18":
            case "32":
            case "49":
            case "57":
            case "58":
                return R.drawable.ic_a18;//雾
            case "19":
                return R.drawable.ic_a19;//冻雨
            case "20":
                return R.drawable.ic_a20;//沙尘暴
            case "21":
                return R.drawable.ic_a21;//小雨-中雨
            case "22":
                return R.drawable.ic_a22;//中雨-大雨
            case "23":
                return R.drawable.ic_a23;//大雨-暴雨
            case "24":
                return R.drawable.ic_a24;//暴雨-大暴雨
            case "25":
                return R.drawable.ic_a25;//大暴雨-特大暴雨
            case "26":
                return R.drawable.ic_a26;//小雪-中雪
            case "27":
                return R.drawable.ic_a27;//中雪-大雪
            case "28":
                return R.drawable.ic_a28;//大雪-暴雪
            case "29":
                return R.drawable.ic_a29;//浮尘
            case "30":
                return R.drawable.ic_a30;//扬沙
            case "31":
                return R.drawable.ic_a31;//强沙尘暴
            case "53":
            case "54":
            case "55":
            case "56":
                return R.drawable.ic_a53;//霾
        }
        return R.mipmap.a99;//无
    }
}
