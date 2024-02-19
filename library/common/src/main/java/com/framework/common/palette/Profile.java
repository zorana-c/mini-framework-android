package com.framework.common.palette;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @Author create by Zhengzelong on 2023-06-01
 * @Email : 171905184@qq.com
 * @Description :
 */
@IntDef({
        Profile.VIBRANT,
        Profile.VIBRANT_DARK,
        Profile.VIBRANT_LIGHT,
        Profile.MUTED,
        Profile.MUTED_DARK,
        Profile.MUTED_LIGHT})
@Retention(RetentionPolicy.SOURCE)
public @interface Profile {
    int VIBRANT = 0;
    int VIBRANT_DARK = 1;
    int VIBRANT_LIGHT = 2;
    int MUTED = 3;
    int MUTED_DARK = 4;
    int MUTED_LIGHT = 5;
}
