package com.framework.core.http;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.framework.core.http.factory.StringConverterFactory;
import com.framework.core.http.interceptor.UIHttpHeaderInterceptor;
import com.framework.core.http.interceptor.UIHttpLoggerInterceptor;
import com.framework.core.http.HttpServiceRepository.Autowired;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @Author create by Zhengzelong on 2022-11-15
 * @Email : 171905184@qq.com
 * @Description :
 */
public final class Http {
    @Nullable
    private static Retrofit sRetrofit;
    @Nullable
    private static OkHttpClient sHttpClient;

    /*
     * addConverterFactory时的顺序至关重要，优先解析先添加的
     * addCallAdapterFactory时的顺序至关重要，优先解析先添加的
     * */
    @NonNull
    public static Retrofit getRetrofit() {
        if (sRetrofit != null) {
            return sRetrofit;
        }
        final Retrofit.Builder builder;
        builder = new Retrofit.Builder()
                // 添加OkHttp支持
                .client(getClient())
                // 增加返回值为String的支持
                .addConverterFactory(GsonConverterFactory.create())
                // 增加返回值为Entry实体的支持
                .addConverterFactory(StringConverterFactory.create())
                // 增加返回值Observable的支持
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                // 添加基础Host路径
                .baseUrl("http://127.0.0.1/");
        // 构建实例
        return sRetrofit = builder.build();
    }

    /*
     * addInterceptor 和 addNetworkInterceptor 区别：
     * 1.前者作用于App和OkHttpClient之间，
     *   后者作用于OkHttpClient和Network之间;
     * 2.前者不需要考虑重定向等问题，后者根据需求操作重定向;
     * 3.前者关注原始的request，而不关心注入的headers;
     * 4.前者只会被执行一次，后者会执行两次;
     * 5.后者可以获取Connection携带的请求信息;
     * */
    @NonNull
    public static OkHttpClient getClient() {
        if (sHttpClient != null) {
            return sHttpClient;
        }
        final OkHttpClient.Builder builder;
        builder = new OkHttpClient.Builder()
                // 设置连接超时
                .connectTimeout(1, TimeUnit.MINUTES)
                // 添加本地头部拦截
                .addInterceptor(UIHttpHeaderInterceptor.create())
                // 添加远程日志拦截
                .addNetworkInterceptor(UIHttpLoggerInterceptor.create())
                // 允许重新请求连接
                .retryOnConnectionFailure(true);
        // 构建实例
        return sHttpClient = builder.build();
    }

    @NonNull
    public static <T> T invoke(@NonNull Class<T> serviceClass) {
        return invoke(getRetrofit(), serviceClass);
    }

    @NonNull
    public static <T> T invoke(@NonNull Retrofit retrofit,
                               @NonNull Class<T> serviceClass) {
        final HttpUrl httpUrl;
        if ((httpUrl = parseUrl(serviceClass)) == null) {
            return retrofit.create(serviceClass);
        }
        return retrofit.newBuilder()
                .baseUrl(httpUrl)
                .build().create(serviceClass);
    }

    @Nullable
    public static HttpUrl parseUrl(@NonNull Class<?> serviceClass) {
        final HttpServiceOption option;
        option = serviceClass.getAnnotation(HttpServiceOption.class);
        if (option == null) {
            return null;
        }
        return HttpUrl.parse(option.host());
    }

    @NonNull
    static <T extends HttpServiceRepository> T inject(@NonNull T t) {
        final List<Field> fieldLists = new ArrayList<>();
        Class<?> parentClass;
        parentClass = t.getClass();
        while (parentClass != null) {
            final Field[] fields;
            fields = parentClass.getDeclaredFields();
            fieldLists.addAll(Arrays.asList(fields));
            parentClass = parentClass.getSuperclass();
        }
        try {
            for (final Field field : fieldLists) {
                if (!field.isAnnotationPresent(Autowired.class)) {
                    continue;
                }
                final Autowired autowired;
                autowired = field.getAnnotation(Autowired.class);
                if (autowired == null) {
                    continue;
                }
                final Class<?> serviceClass = field.getType();
                final Retrofit.Builder builder;
                builder = getRetrofit().newBuilder();
                final HttpUrl httpUrl = parseUrl(serviceClass);
                if (httpUrl != null) {
                    builder.baseUrl(httpUrl);
                }
                t.accept(autowired.tag(), builder);
                final Retrofit retrofit = builder.build();
                field.setAccessible(true);
                field.set(t, retrofit.create(serviceClass));
            }
        } catch (@NonNull IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            fieldLists.clear();
        }
        return t;
    }
}
