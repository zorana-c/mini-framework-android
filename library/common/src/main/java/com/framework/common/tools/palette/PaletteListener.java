package com.framework.common.tools.palette;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;

/**
 * @Author create by Zhengzelong on 2023-06-02
 * @Email : 171905184@qq.com
 * @Description :
 */
public interface PaletteListener {

    @Nullable
    default Palette.Builder onIntercept(
            @NonNull Palette.Builder builder) {
        return builder;
    }

    default void onPaletteLoaded(@Nullable Palette palette) {
        // no-op
    }
}
