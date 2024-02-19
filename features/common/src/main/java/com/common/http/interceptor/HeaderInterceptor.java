package com.common.http.interceptor;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.Set;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Author create by Zhengzelong on 2022-11-16
 * @Email : 171905184@qq.com
 * @Description :
 */
public class HeaderInterceptor implements Interceptor {
    @NonNull
    public static Interceptor create() {
        return new HeaderInterceptor();
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        final Request request = chain.request();
        final Headers headers = request.headers();
        final Request.Builder builder = request.newBuilder()
                // 请求来源: 1-Android 2-IOS 3-Web
                .header("X-Oc-App-Type", String.valueOf(1))
                // 用户定位: 经度
                .header("X-Oc-User-Lon", String.valueOf(0.0f))
                // 用户定位: 维度
                .header("X-Oc-User-Lat", String.valueOf(0.0f));
        // 授权信息(已填写)
        final Set<String> names = headers.names();
        if (names.contains("Authorization")) {
            return chain.proceed(builder.build());
        }
        // 忽略授权
        if (names.contains("X-Oc-User-Ignore")) {
            builder.removeHeader("X-Oc-User-Ignore");
            return chain.proceed(builder.build());
        }
//        // 授权信息(未填写)
//        final Person.Authorization authorization;
//        authorization = UIModelInterface.get(Person.Authorization.class);
//        if (authorization != null) {
//            builder.header("Authorization", authorization.toString());
//        }
        return chain.proceed(builder.build());
    }
}
