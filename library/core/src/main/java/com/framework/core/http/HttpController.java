package com.framework.core.http;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author create by Zhengzelong on 2023-07-11
 * @Email : 171905184@qq.com
 * @Description :
 */
final class HttpController {

    @NonNull
    private static final Map<String, HttpServiceRepository> sMap = new HashMap<>();

    @NonNull
    public static <T extends HttpServiceRepository> T get(@NonNull Class<T> tClass) {
        return HttpController.get(tClass, NewInstanceFactory.DEFAULT);
    }

    @NonNull
    public static <T extends HttpServiceRepository> T get(@NonNull Class<T> tClass,
                                                          @NonNull HttpServiceRepository.Factory factory) {
        return HttpController.get(key(tClass), tClass, factory);
    }

    @NonNull
    private static <T extends HttpServiceRepository> T get(@NonNull String key,
                                                           @NonNull Class<T> tClass,
                                                           @NonNull HttpServiceRepository.Factory factory) {
        HttpServiceRepository httpServiceRepository = sMap.get(key);
        if (tClass.isInstance(httpServiceRepository)) {
            // noinspection unChecked
            return (T) httpServiceRepository;
        } else {
            // noinspection StatementWithEmptyBody
            if (httpServiceRepository != null) {
                // TODO: log a warning.
            }
        }
        httpServiceRepository = Http.inject(factory.create(tClass));
        sMap.put(key, httpServiceRepository);
        // noinspection unChecked
        return (T) httpServiceRepository;
    }

    @Nullable
    public static <T extends HttpServiceRepository> T remove(@NonNull Class<T> tClass) {
        return HttpController.remove(key(tClass));
    }

    @Nullable
    private static <T extends HttpServiceRepository> T remove(@NonNull String key) {
        return (T) sMap.remove(key);
    }

    @NonNull
    private static <T extends HttpServiceRepository> String key(@NonNull Class<T> tClass) {
        return String.format("<HttpServiceRepository-Key:%s/>", tClass.getName());
    }

    private static final class NewInstanceFactory implements HttpServiceRepository.Factory {
        @NonNull
        static final HttpServiceRepository.Factory DEFAULT = new NewInstanceFactory();

        @NonNull
        @Override
        @SuppressWarnings("ClassNewInstance")
        public <T extends HttpServiceRepository> T create(@NonNull Class<T> tClass) {
            // noinspection TryWithIdenticalCatches
            try {
                return tClass.newInstance();
            } catch (@NonNull InstantiationException e) {
                throw new RuntimeException("Cannot create an instance of " + tClass, e);
            } catch (@NonNull IllegalAccessException e) {
                throw new RuntimeException("Cannot create an instance of " + tClass, e);
            }
        }
    }
}
