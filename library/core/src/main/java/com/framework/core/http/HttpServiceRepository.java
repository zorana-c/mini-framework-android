package com.framework.core.http;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import retrofit2.Retrofit;

/**
 * @Author create by Zhengzelong on 2023-07-11
 * @Email : 171905184@qq.com
 * @Description :
 */
public interface HttpServiceRepository {

    interface Factory {

        @NonNull
        <T extends HttpServiceRepository> T create(@NonNull Class<T> tClass);
    }

    @Documented
    @Target({FIELD})
    @Retention(RUNTIME)
    @interface Autowired {

        @NonNull
        String tag() default "";
    }

    /**
     * @param builder 配置选项
     */
    default void accept(@NonNull String tag,
                        @NonNull Retrofit.Builder builder) {
        // no-op
    }

    @NonNull
    static <T extends HttpServiceRepository> T get(@NonNull Class<T> tClass) {
        return HttpController.get(tClass);
    }

    @NonNull
    static <T extends HttpServiceRepository> T get(@NonNull Class<T> tClass,
                                                   @NonNull Factory factory) {
        return HttpController.get(tClass, factory);
    }

    @Nullable
    static <T extends HttpServiceRepository> T remove(@NonNull Class<T> tClass) {
        return HttpController.remove(tClass);
    }
}
