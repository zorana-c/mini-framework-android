package com.framework.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @Author create by Zhengzelong on 2023-05-16
 * @Email : 171905184@qq.com
 * @Description :
 */
public final class DefaultDatabaseProvider implements DatabaseProvider {
    @NonNull
    private final SQLiteOpenHelper sqLiteOpenHelper;

    public DefaultDatabaseProvider(@NonNull SQLiteOpenHelper sqLiteOpenHelper) {
        this.sqLiteOpenHelper = sqLiteOpenHelper;
    }

    @Nullable
    @Override
    public SQLiteDatabase getWritableDatabase() {
        return this.sqLiteOpenHelper.getWritableDatabase();
    }

    @Nullable
    @Override
    public SQLiteDatabase getReadableDatabase() {
        return this.sqLiteOpenHelper.getReadableDatabase();
    }
}
