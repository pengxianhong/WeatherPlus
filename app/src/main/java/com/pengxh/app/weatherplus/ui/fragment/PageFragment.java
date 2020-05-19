package com.pengxh.app.weatherplus.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pengxh.app.weatherplus.R;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/5/18 9:59
 */
public class PageFragment extends BasePageFragment {

    private static final String TAG = "PageFragment";
    private TextView testView;
    private String title;

    public static PageFragment newInstance(String title) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putString("fragment_title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, null);
        title = getArguments().getString("fragment_title");
        testView = view.findViewById(R.id.testView);
        return view;
    }

    @Override
    public void fetchData() {
        Log.d(TAG, "onCreate: " + title);
        /**
         * 在这里请求网络。
         * */
        testView.setText(title);
    }
}
