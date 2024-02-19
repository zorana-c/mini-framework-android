package com.framework.core.http.interceptor;

import android.content.pm.PackageInfo;
import android.os.Build;

import androidx.annotation.NonNull;

import com.framework.core.UIFramework;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Author create by Zhengzelong on 2022/1/25
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UIHttpHeaderInterceptor implements Interceptor {

    @NonNull
    public static Interceptor create() {
        return new UIHttpHeaderInterceptor();
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        final Request request = chain.request();
        final PackageInfo packageInfo = UIFramework.getPackageInfo();
        return chain.proceed(request.newBuilder()
                .header("X-Oc-TimeStamp", String.valueOf(System.currentTimeMillis()))
                .header("X-Oc-Os-Model", String.format("Android %s", Build.VERSION.RELEASE))
                .header("X-Oc-Device-Brand", Build.BRAND)
                .header("X-Oc-Device-Model", Build.MODEL)
                .header("X-Oc-Device-Manufacturer", Build.MANUFACTURER)
                .header("X-Oc-App-Bundle", packageInfo.packageName)
                .header("X-Oc-App-Version", packageInfo.versionName)
                .build());
    }
}
