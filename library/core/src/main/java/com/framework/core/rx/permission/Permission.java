package com.framework.core.rx.permission;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.framework.core.bean.UIModelInterface;

/**
 * @Author create by Zhengzelong on 2022-12-06
 * @Email : 171905184@qq.com
 * @Description :
 */
public class Permission implements UIModelInterface {
    // 权限名称
    @NonNull
    private final String name;
    // 授权状态
    private final boolean granted;
    // 显示请求许可理由
    private final boolean shouldShowRequestPermissionRationale;

    public Permission(@NonNull String name,
                      boolean granted) {
        this(name, granted, false);
    }

    public Permission(@NonNull String name,
                      boolean granted,
                      boolean shouldShowRequestPermissionRationale) {
        this.name = name;
        this.granted = granted;
        this.shouldShowRequestPermissionRationale = shouldShowRequestPermissionRationale;
    }

    /**
     * 权限名称
     */
    @NonNull
    public final String getName() {
        return this.name;
    }

    /**
     * 授权状态
     */
    public final boolean isGranted() {
        return this.granted;
    }

    /**
     * 显示请求许可理由
     */
    public final boolean shouldShowRequestPermissionRationale() {
        return this.shouldShowRequestPermissionRationale;
    }

    @Override
    public int hashCode() {
        int result = this.name.hashCode();
        result = 31 * result + (this.granted ? 1 : 0);
        result = 31 * result + (this.shouldShowRequestPermissionRationale ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || object.getClass() != this.getClass()) {
            return false;
        }
        final Permission thatPermission = (Permission) object;
        if (thatPermission.granted != this.granted ||
                thatPermission.shouldShowRequestPermissionRationale != this.shouldShowRequestPermissionRationale) {
            return false;
        }
        return this.name.equals(thatPermission.name);
    }

    @NonNull
    @Override
    public String toString() {
        return "Permission{" +
                "name='" + this.name + '\'' +
                ", granted=" + this.granted +
                ", shouldShowRequestPermissionRationale=" + this.shouldShowRequestPermissionRationale +
                '}';
    }
}
