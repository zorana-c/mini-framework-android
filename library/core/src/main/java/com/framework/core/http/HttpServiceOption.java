package com.framework.core.http;

import androidx.annotation.NonNull;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @Author create by Zhengzelong on 2022/1/25
 * @Email : 171905184@qq.com
 * @Description :
 */
@Inherited
@Documented
@Target({TYPE})
@Retention(RUNTIME)
public @interface HttpServiceOption {

    @NonNull
    String host() default "http://127.0.0.1/";
}
