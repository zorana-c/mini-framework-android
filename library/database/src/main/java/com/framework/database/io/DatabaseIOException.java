package com.framework.database.io;

import android.database.SQLException;

import androidx.annotation.Nullable;

import java.io.IOException;

/**
 * @Author create by Zhengzelong on 2024-03-07
 * @Email : 171905184@qq.com
 * @Description :
 */
public class DatabaseIOException extends IOException {
    public DatabaseIOException(@Nullable SQLException cause) {
        super(cause);
    }

    public DatabaseIOException(@Nullable SQLException cause, @Nullable String message) {
        super(message, cause);
    }
}
