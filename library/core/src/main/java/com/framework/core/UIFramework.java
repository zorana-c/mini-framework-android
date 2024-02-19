package com.framework.core;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.framework.core.compat.UIFile;
import com.framework.core.content.UIPageControllers;
import com.framework.core.route.UIRouter;

import java.lang.ref.WeakReference;

/**
 * @Author create by Zhengzelong on 2021/11/25
 * @Email : 171905184@qq.com
 * @Description :
 */
public final class UIFramework {
    @Nullable
    private static volatile WeakReference<Context> sApplicationWeR;

    public static void init(@NonNull Context context) {
        if (sApplicationWeR != null) {
            throw new IllegalStateException("UIFramework initialized");
        }
        final Context appContext;
        if (context instanceof Application) {
            appContext = context;
        } else {
            appContext = context.getApplicationContext();
        }
        sApplicationWeR = new WeakReference<>(appContext);
        // 本地文件初始化
        UIFile.init(appContext);
        // 模块化通信初始化
        UIRouter.init(appContext);
        // 视图控制器初始化
        UIPageControllers.init(appContext);
    }

    @NonNull
    public static Context getApplicationContext() {
        Context appContext = null;
        if (sApplicationWeR != null) {
            appContext = sApplicationWeR.get();
        }
        if (appContext == null) {
            throw new IllegalStateException("UIFramework not init");
        }
        return appContext;
    }

    public static long getVersionCode() {
        final PackageInfo packageInfo = getPackageInfo();
        final long versionCode;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            versionCode = packageInfo.getLongVersionCode();
        } else {
            versionCode = packageInfo.versionCode;
        }
        return versionCode;
    }

    @NonNull
    public static String getVersionName() {
        final PackageInfo packageInfo = getPackageInfo();
        String versionName = packageInfo.versionName;
        if (versionName == null) {
            versionName = "";
        }
        return versionName;
    }

    @NonNull
    public static PackageInfo getPackageInfo() {
        PackageInfo packageInfo = null;
        try {
            final Context context = getApplicationContext();
            final String packageName = context.getPackageName();
            final PackageManager packageManager = context.getPackageManager();
            packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_CONFIGURATIONS);
        } catch (@NonNull Exception e) {
            e.printStackTrace();
        }
        if (packageInfo == null) {
            throw new NullPointerException("UnKnown error");
        }
        return packageInfo;
    }

    /* package */ UIFramework() {
        throw new IllegalStateException("No instances!");
    }

    public static class ActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
            // nothing
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {
            // nothing
        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {
            // nothing
        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {
            // nothing
        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {
            // nothing
        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outInstanceState) {
            // nothing
        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
            // nothing
        }
    }

    public static class FragmentLifecycleCallbacks extends FragmentManager.FragmentLifecycleCallbacks {

        @Override
        public void onFragmentCreated(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment, @Nullable Bundle savedInstanceState) {
            // nothing
        }

        @Override
        public void onFragmentStarted(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
            // nothing
        }

        @Override
        public void onFragmentResumed(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
            // nothing
        }

        @Override
        public void onFragmentPaused(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
            // nothing
        }

        @Override
        public void onFragmentStopped(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
            // nothing
        }

        @Override
        public void onFragmentSaveInstanceState(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment, @NonNull Bundle outInstanceState) {
            // nothing
        }

        @Override
        public void onFragmentDestroyed(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
            // nothing
        }
    }
}
