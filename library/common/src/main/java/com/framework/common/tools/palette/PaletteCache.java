package com.framework.common.tools.palette;

import android.util.LruCache;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;

/**
 * @Author create by Zhengzelong on 2023-06-02
 * @Email : 171905184@qq.com
 * @Description :
 */
final class PaletteCache {
    @NonNull
    private static final LruCache<Object, Palette> lc;

    static {
        lc = new LruCache<>(40);
    }

    @Nullable
    public static Palette get(@NonNull Object url) {
        return lc.get(url);
    }

    public static void set(@NonNull Object url,
                           @NonNull Palette palette) {
        lc.put(url, palette);
    }
}
