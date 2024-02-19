package com.common.http;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.framework.core.bean.UIModelInterface;
import com.google.gson.annotations.SerializedName;

/**
 * @Author create by Zhengzelong on 2024-01-10
 * @Email : 171905184@qq.com
 * @Description :
 */
public class Resp<T> implements UIModelInterface {
    public static final int SUCCESS = 200;
    public static final int FAILURE = 400;

    @NonNull
    public static <T> Resp<T> success() {
        return success((T) null);
    }

    @NonNull
    public static <T> Resp<T> success(@Nullable T body) {
        return success(body, null);
    }

    @NonNull
    public static <T> Resp<T> success(@Nullable String message) {
        return success(null, message);
    }

    @NonNull
    public static <T> Resp<T> success(@Nullable T body,
                                      @Nullable String message) {
        return with(SUCCESS, body, message);
    }

    @NonNull
    public static <T> Resp<T> failure() {
        return failure((T) null);
    }

    @NonNull
    public static <T> Resp<T> failure(@Nullable T body) {
        return failure(body, null);
    }

    @NonNull
    public static <T> Resp<T> failure(@Nullable String message) {
        return failure(null, message);
    }

    @NonNull
    public static <T> Resp<T> failure(@Nullable T body,
                                      @Nullable String message) {
        return with(FAILURE, body, message);
    }

    @NonNull
    public static <T> Resp<T> with(int code) {
        return with(code, (T) null);
    }

    @NonNull
    public static <T> Resp<T> with(int code,
                                   @Nullable T body) {
        return with(code, body, null);
    }

    @NonNull
    public static <T> Resp<T> with(int code,
                                   @Nullable String message) {
        return with(code, null, message);
    }

    @NonNull
    public static <T> Resp<T> with(int code,
                                   @Nullable T body,
                                   @Nullable String message) {
        return new Resp<>(code, body, message);
    }

    @Nullable
    @SerializedName("data")
    private final T body;
    @SerializedName("code")
    private final int code;
    @Nullable
    @SerializedName("message")
    private final String message;

    Resp(int code, @Nullable T body, @Nullable String message) {
        this.code = code;
        this.body = body;
        this.message = message;
    }

    public int code() {
        return this.code;
    }

    public boolean successful() {
        return this.code == SUCCESS;
    }

    public final boolean successfulWithBody() {
        if (!this.successful()) {
            return false;
        }
        return this.body() != null;
    }

    @Nullable
    public String message() {
        return this.message;
    }

    @NonNull
    public final String requireMessage() {
        final String r = this.message();
        if (r == null) {
            throw new NullPointerException("ERROR");
        }
        return r;
    }

    @Nullable
    public <R extends T> R body() {
        return (R) this.body;
    }

    @NonNull
    public final <R extends T> R requireBody() {
        final R r = this.body();
        if (r == null) {
            throw new NullPointerException("ERROR");
        }
        return r;
    }

    @NonNull
    @Override
    public Resp<T> clone() {
        try {
            return (Resp<T>) super.clone();
        } catch (@NonNull CloneNotSupportedException e) {
            return failure(e.getMessage());
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "Resp{" +
                "body=" + this.body +
                ", code=" + this.code +
                ", message='" + this.message + '\'' +
                '}';
    }
}
