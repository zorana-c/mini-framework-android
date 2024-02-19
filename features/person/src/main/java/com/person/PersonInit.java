package com.person;

import android.content.Context;

import io.reactivex.rxjava3.annotations.NonNull;

/**
 * @Author create by Zhengzelong on 2024-01-29
 * @Email : 171905184@qq.com
 * @Description :
 */
public final class PersonInit {
    private PersonInit() {
        throw new IllegalStateException("No instances!");
    }

    public static void init(@NonNull Context context) {
    }
}
