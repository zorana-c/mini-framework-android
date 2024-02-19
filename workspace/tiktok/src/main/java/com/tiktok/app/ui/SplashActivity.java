package com.tiktok.app.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.common.route.GuideRoute;
import com.framework.core.compat.UILog;
import com.framework.core.rx.permission.RxPermission;
import com.framework.core.ui.abs.UIFragmentActivity;
import com.framework.core.util.UIUtils;
import com.tiktok.app.R;
import com.tiktok.app.constant.Constants;

import io.reactivex.rxjava3.disposables.Disposable;

/**
 * @Author create by Zhengzelong on 2024-01-30
 * @Email : 171905184@qq.com
 * @Description :
 */
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends UIFragmentActivity {
    @Nullable
    private Disposable disposable;

    @Override
    public int onUILayoutId(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_splash;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onUICreated(@Nullable Bundle savedInstanceState) {
        RxPermission.of(this)
                .request(
                        Manifest.permission.CAMERA,
                        Manifest.permission.INTERNET,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(it -> {
                    this.initApplication();
                });
    }

    @Override
    public void onUIRefresh(@Nullable Bundle savedInstanceState) {
        // nothing
    }

    private void initApplication() {
        if (this.disposable != null) {
            this.disposable.dispose();
        }
        this.disposable = UIUtils.setInterval(timeCount -> {
            final int remain;
            remain = Constants.LAUNCH_TIME - (timeCount + 1);
            if (remain == 0) {
                this.disposable.dispose();
                this.disposable = null;
                this.launchMainPage();
            } else {
                UILog.i("Launch Remain Times: " + remain);
            }
        }, Constants.LAUNCH_TIME);
    }

    private void launchMainPage() {
        if (GuideRoute.get().startGuide(this, Constants.GUIDES)) {
            return;
        }
        this.getUINavigatorController()
                .startActivity(MainActivity.class)
                .navigateUp();
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            this.launchMainPage();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.disposable != null) {
            this.disposable.dispose();
            this.disposable = null;
        }
    }
}
