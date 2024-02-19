package com.framework.core.exception;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import retrofit2.HttpException;

/**
 * @Author create by Zhengzelong on 2023-09-25
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UILayoutException extends UIException {
    public static final int CODE_NETWORK = 10004;

    public static void fail() throws UILayoutException {
        fail(null);
    }

    public static void fail(@Nullable String message) throws UILayoutException {
        fail(message, null);
    }

    public static void fail(@Nullable String message,
                            @Nullable Throwable cause) throws UILayoutException {
        throw new UILayoutException(message, cause);
    }

    public static void fail(int code) throws UILayoutException {
        fail(code, null);
    }

    public static void fail(int code,
                            @Nullable String message) throws UILayoutException {
        fail(code, message, null);
    }

    public static void fail(int code,
                            @Nullable String message,
                            @Nullable Throwable cause) throws UILayoutException {
        throw new UILayoutException(code, message, cause);
    }

    private final int code;

    public UILayoutException(@NonNull HttpException e) {
        this(e.code(), e.message(), e);
    }

    public UILayoutException() {
        this((String) null);
    }

    public UILayoutException(@Nullable String message) {
        this(message, null);
    }

    public UILayoutException(@Nullable String message,
                             @Nullable Throwable cause) {
        this(CODE_NETWORK, message, cause);
    }

    public UILayoutException(int code) {
        this(code, null);
    }

    public UILayoutException(int code,
                             @Nullable String message) {
        this(code, message, null);
    }

    public UILayoutException(int code,
                             @Nullable String message,
                             @Nullable Throwable cause) {
        super(String.format("Code %s %s", code, message), cause);
        this.code = code;
    }

    public final int code() {
        return this.code;
    }

    @NonNull
    public final String formatCodeString() {
        return String.format("(%s)", this.code);
    }
}
