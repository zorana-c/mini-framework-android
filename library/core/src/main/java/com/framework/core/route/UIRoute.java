package com.framework.core.route;

import android.content.Context;

import androidx.annotation.NonNull;

/**
 * @Author create by Zhengzelong on 2023-12-27
 * @Email : 171905184@qq.com
 * @Description :
 */
public interface UIRoute {
    @NonNull
    static <T extends UIRoute> T get(@NonNull Class<T> tClass) {
        return UIRouter.get(tClass);
    }

    default void init(@NonNull Context context) {
        // nothing
    }
}
