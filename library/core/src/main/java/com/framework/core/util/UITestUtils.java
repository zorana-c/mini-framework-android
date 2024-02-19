package com.framework.core.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author create by Zhengzelong on 2022/7/18
 * @Email : 171905184@qq.com
 * @Description :
 */
public final class UITestUtils {
    private UITestUtils() {
        throw new IllegalStateException("No instances!");
    }

    @NonNull
    public static <T> List<T> obtain(int page, int limit) {
        return obtain(page, limit, null);
    }

    @NonNull
    public static <T> List<T> obtain(int page, int limit, @Nullable T defOfT) {
        final List<T> data = new ArrayList<>();
        for (int index = (page - 1) * limit; index < (page * limit); index++) {
            data.add(defOfT);
        }
        return data;
    }
}
