package com.framework.widget;

import android.view.View;

import androidx.annotation.NonNull;

/**
 * @Author create by Zhengzelong on 2021/8/26
 * @Email : 171905184@qq.com
 * @Description :
 */
public class FitSystemWindowCompat {

    /**
     * 解决软件盘遮盖视图层
     *
     * @param view target
     */
    public static void setFitsSystemWindows(@NonNull View view) {
        view.setFitsSystemWindows(true);

        if (view instanceof FitSystemWindow) {
            final FitSystemWindow fitSystemWindow = (FitSystemWindow) view;
            fitSystemWindow.setOnFitSystemWindowListener((target, insets) -> {
                if (target.getFitsSystemWindows()) {
                    insets.top = 0;
                }
            });
        }
    }
}
