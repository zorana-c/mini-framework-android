package com.common.http.repository;

import androidx.annotation.NonNull;

import com.common.http.interceptor.HeaderInterceptor;
import com.framework.core.http.Http;
import com.framework.core.http.HttpServiceRepository;

import retrofit2.Retrofit;

/**
 * @Author create by Zhengzelong on 2024-01-10
 * @Email : 171905184@qq.com
 * @Description :
 */
public abstract class AbsServiceRepository implements HttpServiceRepository {
    @Override
    public void accept(@NonNull String tag,
                       @NonNull Retrofit.Builder builder) {
        builder.client(Http.getClient()
                // 重新构建客户端
                .newBuilder()
                // 自定义头部拦截器
                .addInterceptor(HeaderInterceptor.create())
                .build());
    }
}
