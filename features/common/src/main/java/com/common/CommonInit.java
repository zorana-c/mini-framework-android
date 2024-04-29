package com.common;

import android.content.Context;

import androidx.annotation.NonNull;

import com.common.content.CommonDecorOptions;
import com.framework.core.content.UIPagePlugins;

/**
 * @Author create by Zhengzelong on 2024-01-10
 * @Email : 171905184@qq.com
 * @Description :
 */
public final class CommonInit {
    private CommonInit() {
        throw new IllegalStateException("No instances!");
    }

    public static void init(@NonNull Context context) {
        // Setup page options.
        UIPagePlugins.setUIPageOptions(CommonDecorOptions.class);
    }
}
