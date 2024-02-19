package com.framework.core.http.factory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * @Author create by Zhengzelong on 2022/1/25
 * @Email : 171905184@qq.com
 * @Description :
 */
public class StringConverterFactory extends Converter.Factory {

    @NonNull
    public static Converter.Factory create() {
        return new StringConverterFactory();
    }

    @Nullable
    @Override
    public Converter<?, RequestBody> requestBodyConverter(@NonNull Type type,
                                                          @NonNull Annotation[] parameterAnnotations,
                                                          @NonNull Annotation[] methodAnnotations, @NonNull Retrofit retrofit) {
        return super.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
    }

    @Nullable
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(@NonNull Type type,
                                                            @NonNull Annotation[] annotations, @NonNull Retrofit retrofit) {
        if (String.class == type) {
            return StringConverter.INSTANCE;
        }
        return super.responseBodyConverter(type, annotations, retrofit);
    }

    private static class StringConverter implements Converter<ResponseBody, String> {
        private static final StringConverter INSTANCE = new StringConverter();

        @Override
        public String convert(@NonNull ResponseBody responseBody) throws IOException {
            return responseBody.string();
        }
    }
}

