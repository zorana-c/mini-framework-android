package com.framework.core.rx.permission;

import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.reactivex.rxjava3.subjects.PublishSubject;

/**
 * @Author create by Zhengzelong on 2022-12-06
 * @Email : 171905184@qq.com
 * @Description :
 */
public final class RxPermissionHandler {
    @NonNull
    private final Map<String, PublishSubject<Permission>> maps = new HashMap<>();

    public int size() {
        return this.maps.size();
    }

    public boolean isEmpty() {
        return this.maps.isEmpty();
    }

    public boolean containsKey(@NonNull String permission) {
        return this.maps.containsKey(permission);
    }

    @NonNull
    public String[] getUnRequestedPermissions() {
        final Set<String> permissions = this.maps.keySet();
        String[] unrequestedPermissions;
        unrequestedPermissions = new String[permissions.size()];
        unrequestedPermissions = permissions.toArray(unrequestedPermissions);
        return unrequestedPermissions;
    }

    @Nullable
    public PublishSubject<Permission> get(@NonNull String permission) {
        return this.maps.get(permission);
    }

    @Nullable
    public PublishSubject<Permission> remove(@NonNull String permission) {
        return this.maps.remove(permission);
    }

    public void put(@NonNull String permission,
                    @NonNull PublishSubject<Permission> publishSubject) {
        this.maps.put(permission, publishSubject);
    }

    public void handled(@NonNull String[] permissions,
                        @NonNull int[] grantResults,
                        @NonNull boolean[] shouldShowRequestPermissionRationale) {
        PublishSubject<Permission> publishSubject;
        for (int index = 0; index < permissions.length; index++) {
            final String permissionStr = permissions[index];
            if ((publishSubject = this.remove(permissionStr)) == null) {
                continue;
            }
            final boolean isGranted = isGranted(grantResults[index]);
            final boolean shouldShowRationale;
            shouldShowRationale = shouldShowRequestPermissionRationale[index];
            final Permission permission;
            permission = new Permission(permissionStr, isGranted, shouldShowRationale);
            publishSubject.onNext(permission);
            publishSubject.onComplete();
        }
    }

    private static boolean isGranted(int grantResult) {
        return grantResult == PackageManager.PERMISSION_GRANTED;
    }
}
