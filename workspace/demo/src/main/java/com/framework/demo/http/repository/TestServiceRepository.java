package com.framework.demo.http.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.common.http.interceptor.HeaderInterceptor;
import com.framework.core.compat.UILog;
import com.framework.core.http.HttpServiceRepository;
import com.framework.core.http.Http;
import com.framework.core.rx.UIObservable;
import com.framework.core.rx.function.UIObservables;
import com.framework.demo.http.bean.ReqTestCommModel;
import com.framework.demo.http.service.CommService;
import com.framework.demo.http.service.UserService;
import com.google.common.collect.ImmutableMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * @Author create by Zhengzelong on 2022/7/22
 * @Email : 171905184@qq.com
 * @Description :
 */
public class TestServiceRepository implements HttpServiceRepository {

    @NonNull
    public static TestServiceRepository get() {
        return HttpServiceRepository.get(TestServiceRepository.class);
    }

    @Autowired
    private CommService commService;
    @Autowired
    private UserService userService;

    @Override
    public void accept(@NonNull String tag,
                       @NonNull Retrofit.Builder builder) {
        builder.client(Http.getClient().newBuilder()
                        .addInterceptor(HeaderInterceptor.create())
                        .build())
                .addConverterFactory(new Converter.Factory() {
                    @Override
                    public Converter<?, RequestBody> requestBodyConverter(@NonNull Type type,
                                                                          @NonNull Annotation[] parameterAnnotations,
                                                                          @NonNull Annotation[] methodAnnotations,
                                                                          @NonNull Retrofit retrofit) {
                        UILog.e("requestBodyConverter: " + type
                                + " => " + Arrays.toString(parameterAnnotations)
                                + " => " + Arrays.toString(methodAnnotations));
                        return super.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
                    }

                    @Nullable
                    @Override
                    public Converter<ResponseBody, ?> responseBodyConverter(@NonNull Type type,
                                                                            @NonNull Annotation[] annotations,
                                                                            @NonNull Retrofit retrofit) {
                        UILog.e("responseBodyConverter: " + type + " => " + Arrays.toString(annotations));
                        return super.responseBodyConverter(type, annotations, retrofit);
                    }
                })
        ;
    }

    @NonNull
    public UIObservable<String> test() {
        return this.userService
                .queryUserById(ImmutableMap.of("A", "a", "B", "b"))
                .to(UIObservables.asObservable());
    }

    @NonNull
    public UIObservable<String> test2() {
        return this.commService
                .queryCommById(new ReqTestCommModel("1651324492"))
                .to(UIObservables.asObservable());
    }

    @NonNull
    public UIObservable<String> testZip() {
        return Observable.zip(this.test(), this.test2(), (s, s2) -> {
                    return String.format("%s - %s", s, s2);
                })
                .to(UIObservables.asObservable());
    }

    @NonNull
    public UIObservable<String> testMerge() {
        return Observable.merge(this.test(), this.test2())
                .to(UIObservables.asObservable());
    }
}