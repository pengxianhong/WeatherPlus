package com.pengxh.app.weatherplus.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;

import com.gyf.immersionbar.ImmersionBar;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.utils.SaveKeyValues;
import com.pengxh.app.multilib.widget.EasyToast;
import com.pengxh.app.multilib.widget.dialog.PermissionDialog;
import com.pengxh.app.weatherplus.R;
import com.pengxh.app.weatherplus.service.CityService;
import com.pengxh.app.weatherplus.test.TestActivity;
import com.pengxh.app.weatherplus.utils.OtherUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Administrator on 2019/6/23.
 */

public class WelcomeActivity extends BaseNormalActivity implements
        EasyPermissions.PermissionCallbacks, View.OnClickListener {

    private static final String TAG = "WelcomeActivity";
    private static final int PERMISSIONS_CODE = 999;
    private static final String[] USER_PERMISSIONS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private boolean isFirstLaunch = true;
    private boolean isChecked = (boolean) SaveKeyValues.getValue("isChecked", true);

    @BindView(R.id.checkBoxView)
    CheckBox checkBoxView;

    @Override
    public int initLayoutView() {
        return R.layout.activity_welcome;
    }

    @Override
    public void initData() {
        ImmersionBar.with(this).statusBarColor(R.color.white).fitsSystemWindows(true).init();
        checkBoxView.setChecked(isChecked);
    }

    @Override
    public void initEvent() {
        //判断是否有权限，如果版本大于5.1才需要判断（即6.0以上），其他则不需要判断。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission(this)) {
                startMainActivity(0);//测试改为1
            } else {
                new PermissionDialog.Builder()
                        .setContext(this)
                        .setPermission(USER_PERMISSIONS)
                        .setOnDialogClickListener(new PermissionDialog.onDialogClickListener() {
                            @Override
                            public void onButtonClick() {
                                EasyPermissions.requestPermissions(WelcomeActivity.this, "需要获取相关权限", PERMISSIONS_CODE, USER_PERMISSIONS);
                            }

                            @Override
                            public void onCancelClick() {
                                EasyToast.showToast("用户取消授权", EasyToast.WARING);
                            }
                        }).build().show();
            }
        } else {
            startMainActivity(0);
        }
    }

    private boolean checkPermission(Activity mActivity) {
        return EasyPermissions.hasPermissions(mActivity, USER_PERMISSIONS);
    }

    private void startMainActivity(int tag) {
        Intent intent;
        if (tag == 0) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, TestActivity.class);
        }
        if (isChecked) {
            startActivity(intent);
            finish();
        }
    }

    @OnClick({R.id.checkBoxView, R.id.serviceAgreementView, R.id.privacyPolicyView})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkBoxView:
                if (isChecked) {
                    checkBoxView.setChecked(false);
                    SaveKeyValues.putValue("isChecked", false);
                } else {
                    checkBoxView.setChecked(true);
                    SaveKeyValues.putValue("isChecked", true);
                }
                break;
            case R.id.serviceAgreementView:
            case R.id.privacyPolicyView:
                OtherUtil.showAlertDialog(this);
                break;
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NotNull List<String> perms) {
        //开启后台服务将本地数据存到数据库里面，提高查询效率。不能用网络请求，数据量太大，网络请求会卡死
        if (isFirstLaunch) {
            Log.d(TAG, "onPermissionsGranted: 首次启动，启动CityService保存城市信息");
            startService(new Intent(this, CityService.class));
            isFirstLaunch = false;
        } else {
            Log.d(TAG, "onPermissionsGranted: 非首次启动，不做任何处理");
        }

        //延时加载
        new CountDownTimer(1500, 500) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                startMainActivity(0);
            }
        }.start();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NotNull List<String> perms) {
        Log.e(TAG, "onPermissionsDenied: " + perms);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //授权回调，必须，不然不走回调
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
