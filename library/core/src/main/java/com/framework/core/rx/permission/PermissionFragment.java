package com.framework.core.rx.permission;

import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.framework.core.ui.abs.UIFragment;

/**
 * @Author create by Zhengzelong on 2022-12-06
 * @Email : 171905184@qq.com
 * @Description :
 */
public class PermissionFragment extends UIFragment {
    private static final int PERMISSIONS_CODE = 0x99;
    /*
     * 需待申请权限
     * */
    @NonNull
    private final RxPermissionHandler handler = new RxPermissionHandler();

    @Override
    public int onUILayoutId(@Nullable Bundle savedInstanceState) {
        return 0;
    }

    @Override
    @CallSuper
    public void onUICreated(@Nullable Bundle savedInstanceState) {
        // no-op
    }

    @Override
    @CallSuper
    public void onUIRefresh(@Nullable Bundle savedInstanceState) {
        final String[] unRequestedPermissions;
        unRequestedPermissions = this.handler.getUnRequestedPermissions();
        if (unRequestedPermissions.length > 0) {
            this.requestPermissions(unRequestedPermissions, PERMISSIONS_CODE);
        } else {
            this.onRequestPermissionsCompleted(unRequestedPermissions);
        }
    }

    @Override
    @CallSuper
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != PERMISSIONS_CODE) {
            return;
        }
        final boolean[] shouldShowRationale = new boolean[permissions.length];
        for (int index = 0; index < permissions.length; index++) {
            shouldShowRationale[index] = this.shouldShowRequestPermissionRationale(permissions[index]);
        }
        this.handler.handled(permissions, grantResults, shouldShowRationale);
        this.onRequestPermissionsCompleted(permissions);
    }

    @CallSuper
    public void onRequestPermissionsCompleted(@NonNull String[] permissions) {
        // no-op
    }

    @NonNull
    public final RxPermissionHandler getHandler() {
        return this.handler;
    }

    final void requestPermissions() {
        if (this.getUIPageController().isRefresh()) {
            this.getUIPageController().notifyDataSetRefresh();
        }
    }
}
