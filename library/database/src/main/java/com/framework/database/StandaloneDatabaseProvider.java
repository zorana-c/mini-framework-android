package com.framework.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * @Author create by Zhengzelong on 2023-05-16
 * @Email : 171905184@qq.com
 * @Description :
 */
public class StandaloneDatabaseProvider extends SQLiteOpenHelper implements DatabaseProvider {
    private static final String DATABASE_NAME = "mini_framework_internal.db";
    private static final int DATABASE_VERSION = 1;

    public StandaloneDatabaseProvider(@NonNull Context context) {
        this(context, /* factory= */ (SQLiteDatabase.CursorFactory) null);
    }

    public StandaloneDatabaseProvider(@NonNull Context context,
                                      @Nullable SQLiteDatabase.CursorFactory factory) {
        this(context, factory, /* errorHandler= */ null);
    }

    public StandaloneDatabaseProvider(@NonNull Context context,
                                      @Nullable DatabaseErrorHandler errorHandler) {
        this(context, /* factory= */ null, errorHandler);
    }

    public StandaloneDatabaseProvider(@NonNull Context context,
                                      @Nullable SQLiteDatabase.CursorFactory factory,
                                      @Nullable DatabaseErrorHandler errorHandler) {
        super(context.getApplicationContext(), DATABASE_NAME, factory, DATABASE_VERSION, errorHandler);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public StandaloneDatabaseProvider(@NonNull Context context,
                                      @NonNull SQLiteDatabase.OpenParams openParams) {
        super(context.getApplicationContext(), DATABASE_NAME, DATABASE_VERSION, openParams);
    }

    // 数据库配置选项
    @Override
    public void onConfigure(@NonNull SQLiteDatabase db) {
        // no-op
    }

    // 数据库已经创建
    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        // no-op
    }

    // 数据库版本升级
    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        // no-op
    }

    // 数据库版本降级
    @Override
    public void onDowngrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        // no-op
    }

    // 数据库已经打开
    @Override
    public void onOpen(@NonNull SQLiteDatabase db) {
        // no-op
    }
}
