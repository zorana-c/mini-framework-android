package com.framework.widget.expand;

import androidx.annotation.IntDef;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @Author create by Zhengzelong on 2023-08-25
 * @Email : 171905184@qq.com
 * @Description :
 */
@Documented
@IntDef(flag = true, value = {
        PositionType.TYPE_NONE,
        PositionType.TYPE_HEAD,
        PositionType.TYPE_TAIL,
        PositionType.TYPE_EMPTY,
        PositionType.TYPE_GROUP,
        PositionType.TYPE_CHILD})
@Retention(RetentionPolicy.SOURCE)
public @interface PositionType {
    int TYPE_NONE = 0;
    int TYPE_HEAD = 1 << 1;
    int TYPE_TAIL = 1 << 2;
    int TYPE_EMPTY = 1 << 3;
    int TYPE_GROUP = 1 << 4;
    int TYPE_CHILD = 1 << 5;
}
