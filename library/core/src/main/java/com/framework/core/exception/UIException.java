package com.framework.core.exception;

import androidx.annotation.Nullable;

/**
 * @Author create by Zhengzelong on 2021/11/25
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UIException extends RuntimeException {

    public UIException() {
        this((String) null);
    }

    public UIException(@Nullable Throwable cause) {
        super(cause);
    }

    public UIException(@Nullable String message) {
        super(message);
    }

    public UIException(@Nullable String message,
                       @Nullable Throwable cause) {
        super(message, cause);
    }

    @Nullable
    @Override
    public String getMessage() {
        return super.getMessage();
    }

    @Nullable
    @Override
    public String getLocalizedMessage() {
        return super.getLocalizedMessage();
    }

    @Nullable
    @Override
    public synchronized Throwable getCause() {
        return super.getCause();
    }
}
