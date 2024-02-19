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
        Swatch.RGB,
        Swatch.BODY_TEXT_COLOR,
        Swatch.TITLE_TEXT_COLOR})
@Retention(RetentionPolicy.SOURCE)
public @interface Swatch {
    int RGB = 0;
    int BODY_TEXT_COLOR = 1;
    int TITLE_TEXT_COLOR = 2;
}
