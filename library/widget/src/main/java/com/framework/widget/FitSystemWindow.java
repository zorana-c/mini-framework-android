package com.framework.widget;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @Author create by Zhengzelong on 2021/8/26
 * @Email : 171905184@qq.com
 * @Description :
 */
public interface FitSystemWindow {

    void setOnFitSystemWindowListener(@Nullable OnFitSystemWindowListener listener);

    interface OnFitSystemWindowListener {

        void onFitSystemWindows(@NonNull View target, @NonNull Rect insets);
    }
}
