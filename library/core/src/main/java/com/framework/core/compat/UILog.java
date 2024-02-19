package com.framework.core.compat;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.framework.core.BuildConfig;

/**
 * @Author create by Zhengzelong on 2021/12/1
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UILog {
    private static final String tag = "UIProject";
    private static boolean debug = BuildConfig.BUILD_DEBUG;

    @NonNull
    public static String tag() {
        return tag;
    }

    public static boolean debug() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        UILog.debug = debug;
    }

    public static void i(@Nullable String i) {
        i(i, null);
    }

    public static void i(@NonNull Throwable tr) {
        i(tr.getMessage(), tr);
    }

    public static void i(@Nullable String i, @Nullable Throwable tr) {
        if (debug()) {
            Log.i(tag(), i, tr);
        }
    }

    public static void d(@Nullable String d) {
        d(d, null);
    }

    public static void d(@NonNull Throwable tr) {
        d(tr.getMessage(), tr);
    }

    public static void d(@Nullable String d, @Nullable Throwable tr) {
        if (debug()) {
            Log.d(tag(), d, tr);
        }
    }

    public static void e(@Nullable String e) {
        e(e, null);
    }

    public static void e(@NonNull Throwable tr) {
        e(tr.getMessage(), tr);
    }

    public static void e(@Nullable String e, @Nullable Throwable tr) {
        if (debug()) {
            Log.e(tag(), e, tr);
        }
    }

    public static void v(@Nullable String e) {
        v(e, null);
    }

    public static void v(@NonNull Throwable tr) {
        v(tr.getMessage(), tr);
    }

    public static void v(@Nullable String e, @Nullable Throwable tr) {
        if (debug()) {
            Log.v(tag(), e, tr);
        }
    }
}
