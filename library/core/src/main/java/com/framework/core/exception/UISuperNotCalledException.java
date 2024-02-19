package com.framework.core.exception;

import androidx.annotation.Nullable;

/**
 * @Author create by Zhengzelong on 2021/11/26
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UISuperNotCalledException extends UIException {

    public UISuperNotCalledException(@Nullable String message) {
        super(message);
    }
}
