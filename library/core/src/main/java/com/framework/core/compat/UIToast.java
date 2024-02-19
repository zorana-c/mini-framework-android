package com.framework.core.compat;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.framework.core.UIFramework;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

/**
 * @Author create by Zhengzelong on 2021/12/1
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UIToast {

    public static void toast(@Nullable String t) {
        toast(UIFramework.getApplicationContext(), t);
    }

    public static void toast(@StringRes int resId) {
        toast(UIFramework.getApplicationContext(), resId);
    }

    public static void toast(@NonNull Context context, @StringRes int resId) {
        toast(context, context.getResources().getString(resId));
    }

    public static void toast(@NonNull Context context, @Nullable String t) {
        if (!TextUtils.isEmpty(t)) {
            Toast.makeText(context, String.valueOf(t), Toast.LENGTH_SHORT).show();
        }
    }

    public static void toastDebug(@Nullable String t) {
        toastDebug(UIFramework.getApplicationContext(), t);
    }

    public static void toastDebug(@StringRes int resId) {
        toastDebug(UIFramework.getApplicationContext(), resId);
    }

    public static void toastDebug(@NonNull Context context, @StringRes int resId) {
        toastDebug(context, context.getResources().getString(resId));
    }

    public static void toastDebug(@NonNull Context context, @Nullable String t) {
        if (UILog.debug() && !TextUtils.isEmpty(t)) {
            Toast.makeText(context, String.format("Debug: %s", t), Toast.LENGTH_SHORT).show();
        }
    }

    public static void asyncToast(@Nullable String t) {
        asyncToast(UIFramework.getApplicationContext(), t);
    }

    public static void asyncToast(@StringRes int resId) {
        asyncToast(UIFramework.getApplicationContext(), resId);
    }

    public static void asyncToast(@NonNull Context context, @StringRes int resId) {
        asyncToast(context, context.getResources().getString(resId));
    }

    public static void asyncToast(@NonNull Context context, @Nullable String t) {
        AndroidSchedulers.mainThread().scheduleDirect(() -> {
            toast(context, t);
        });
    }

    public static void asyncToastDebug(@Nullable String t) {
        asyncToastDebug(UIFramework.getApplicationContext(), t);
    }

    public static void asyncToastDebug(@StringRes int resId) {
        asyncToastDebug(UIFramework.getApplicationContext(), resId);
    }

    public static void asyncToastDebug(@NonNull Context context, @StringRes int resId) {
        asyncToastDebug(context, context.getResources().getString(resId));
    }

    public static void asyncToastDebug(@NonNull Context context, @Nullable String t) {
        AndroidSchedulers.mainThread().scheduleDirect(() -> {
            toastDebug(context, t);
        });
    }
}
