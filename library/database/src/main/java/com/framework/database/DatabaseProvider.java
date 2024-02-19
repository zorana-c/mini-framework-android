package com.framework.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import androidx.annotation.Nullable;

/**
 * @Author create by Zhengzelong on 2023-05-16
 * @Email : 171905184@qq.com
 * @Description : Provides {@link SQLiteDatabase} instances to media library components, which may read and write
 * tables prefixed with {@link #TABLE_PREFIX}.
 */
public interface DatabaseProvider {
    /**
     * Prefix for tables that can be read and written by media library components.
     */
    String TABLE_PREFIX = "MiniFramework";

    /**
     * Creates and/or opens a database that will be used for reading and writing.
     *
     * <p>Once opened successfully, the database is cached, so you can call this method every time you
     * need to write to the database. Errors such as bad permissions or a full disk may cause this
     * method to fail, but future attempts may succeed if the problem is fixed.
     *
     * @return A read/write database object.
     * @throws SQLiteException If the database cannot be opened for writing.
     */
    @Nullable
    SQLiteDatabase getWritableDatabase();

    /**
     * Creates and/or opens a database. This will be the same object returned by {@link
     * #getWritableDatabase()} unless some problem, such as a full disk, requires the database to be
     * opened read-only. In that case, a read-only database object will be returned. If the problem is
     * fixed, a future call to {@link #getWritableDatabase()} may succeed, in which case the read-only
     * database object will be closed and the read/write object will be returned in the future.
     *
     * <p>Once opened successfully, the database is cached, so you can call this method every time you
     * need to read from the database.
     *
     * @return A database object valid until {@link #getWritableDatabase()} is called.
     * @throws SQLiteException If the database cannot be opened.
     */
    @Nullable
    SQLiteDatabase getReadableDatabase();
}
