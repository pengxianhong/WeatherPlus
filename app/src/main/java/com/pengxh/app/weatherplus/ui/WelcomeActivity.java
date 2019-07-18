package com.pengxh.app.weatherplus.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.weatherplus.R;
import com.pengxh.app.weatherplus.service.CityService;

import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Administrator on 2019/6/23.
 */

public class WelcomeActivity extends BaseNormalActivity implements EasyPermissions.PermissionCallbacks {

    private static final String TAG = "WelcomeActivity";
    private static final int permissionCode = 999;
    private static final String[] perms = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    public void initView() {
        setContentView(R.layout.activity_welcome);
    }

    @Override
    public void init() {

    }

    @Override
    public void initEvent() {
        SharedPreferences sp = this.getSharedPreferences("fisrtConfig", Context.MODE_PRIVATE);
        boolean isFirstRun = sp.getBoolean("isFirstRun", true);
        SharedPreferences.Editor editor = sp.edit();
        Log.d(TAG, "APP: isFirstRun =====> " + isFirstRun);
        if (isFirstRun) {
            editor.putBoolean("isFirstRun", false);
            editor.apply();

            requirePermissions();

            //开启后台服务将本地数据存到数据库里面，提高查询效率。不能用网络请求，数据量太大，网络请求会卡死
            startService(new Intent(this, CityService.class));
        } else {
            startMainActivity();
        }
    }

    private void startMainActivity() {
//        Intent intent = new Intent(this, TestActivity.class);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void requirePermissions() {
        EasyPermissions.requestPermissions(this, "", permissionCode, perms);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        startMainActivity();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.e(TAG, "onPermissionsDenied: " + perms);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //授权回调，必须，不然不走回调
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        Log.d(TAG, "onRequestPermissionsResult: " + Arrays.toString(permissions));
    }
}
