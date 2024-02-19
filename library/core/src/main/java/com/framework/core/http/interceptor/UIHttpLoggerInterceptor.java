package com.framework.core.http.interceptor;

import androidx.annotation.NonNull;

import okhttp3.Interceptor;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * @Author create by Zhengzelong on 2022/1/25
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UIHttpLoggerInterceptor {

    @NonNull
    public static Interceptor create() {
        return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    }
}
