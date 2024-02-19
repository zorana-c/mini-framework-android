package com.metamedia;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.database.StandaloneDatabaseProvider;
import androidx.media3.datasource.cache.Cache;
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor;
import androidx.media3.datasource.cache.SimpleCache;

import com.framework.core.compat.UIFile;

import java.io.File;

/**
 * @Author create by Zhengzelong on 2024-01-25
 * @Email : 171905184@qq.com
 * @Description :
 */
public final class MetaMediaInit {
    private MetaMediaInit() {
        throw new IllegalStateException("No instances!");
    }

    private static final long CACHE_MAX = 512 * 1024 * 1024;
    @Nullable
    private static volatile Cache sCache;

    @SuppressLint("UnsafeOptInUsageError")
    public static void init(@NonNull Context context) {
        if (sCache != null) {
            return;
        }
        sCache = new SimpleCache(
                new File(UIFile.getVideoPath()),
                new LeastRecentlyUsedCacheEvictor(CACHE_MAX),
                new StandaloneDatabaseProvider(context));
    }

    @SuppressLint("UnsafeOptInUsageError")
    @NonNull
    public static Cache cache() {
        if (sCache == null) {
            throw new NullPointerException("Module not init");
        }
        return sCache;
    }
}
