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
    public static final String DATABASE_NAME = "mini_framework_internal.db";
    public static final int VERSION = 1;

    public StandaloneDatabaseProvider(@Nullable Context context,
                                      @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, version);
    }

    public StandaloneDatabaseProvider(@Nullable Context context,
                                      @Nullable SQLiteDatabase.CursorFactory factory,
                                      @Nullable DatabaseErrorHandler errorHandler, int version) {
        super(context, DATABASE_NAME, factory, version, errorHandler);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public StandaloneDatabaseProvider(@Nullable Context context,
                                      @NonNull SQLiteDatabase.OpenParams openParams, int version) {
        super(context, DATABASE_NAME, version, openParams);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        // no-op
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        // no-op
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // no-op
    }
}
