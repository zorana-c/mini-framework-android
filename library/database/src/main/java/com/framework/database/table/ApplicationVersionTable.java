package com.framework.database.table;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import com.framework.core.compat.UILog;
import com.framework.database.DatabaseProvider;
import com.framework.database.io.DatabaseIOException;

import java.nio.charset.StandardCharsets;

/**
 * @Author create by Zhengzelong on 2024-03-07
 * @Email : 171905184@qq.com
 * @Description :
 * @deprecated 仅供测试
 */
public final class ApplicationVersionTable {
    @NonNull
    private static final String TABLE_NAME =
            DatabaseProvider.TABLE_PREFIX + "ApplicationVersion";

    @NonNull
    private static final String COLUMN_VERSION_ID = "version_id";
    @NonNull
    private static final String COLUMN_VERSION_CODE = "version_code";
    @NonNull
    private static final String COLUMN_VERSION_NAME = "version_name";
    @NonNull
    private static final String COLUMN_CREATE_TIME = "create_time";
    @NonNull
    private static final String COLUMN_UPDATE_TIME = "update_time";

    @NonNull
    private static final String PRIMARY_KEY =
            "PRIMARY KEY (" + COLUMN_VERSION_ID + ")";

    @NonNull
    private static final String SQL_CREATE_TABLE_IF_NOT_EXISTS =
            "CREATE TABLE IF NOT EXISTS "
                    + TABLE_NAME
                    + " ("
                    + COLUMN_VERSION_ID
                    + " INTEGER AUTO_INCREMENT NOT NULL,"
                    + COLUMN_VERSION_CODE
                    + " INTEGER NOT NULL,"
                    + COLUMN_VERSION_NAME
                    + " TEXT NOT NULL,"
                    + COLUMN_CREATE_TIME
                    + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                    + COLUMN_UPDATE_TIME
                    + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                    + PRIMARY_KEY
                    + ")";

    public static void set(@NonNull SQLiteDatabase db, int versionCode,
                           @NonNull String versionName) throws DatabaseIOException {
        try {
            db.execSQL(SQL_CREATE_TABLE_IF_NOT_EXISTS);
            final ContentValues cv = new ContentValues();
            cv.put(COLUMN_VERSION_CODE, versionCode);
            cv.put(COLUMN_VERSION_NAME, versionName);
            db.replaceOrThrow(TABLE_NAME, /* nullColumnHack= */ null, cv);
        } catch (@NonNull SQLException e) {
            throw new DatabaseIOException(e);
        }
    }

    public static int getVersionCode(@NonNull SQLiteDatabase db) throws DatabaseIOException {
        try {
            try (final Cursor cursor = db.query(TABLE_NAME,
                    /* columns= */ new String[]{COLUMN_VERSION_CODE},
                    /* selection= */ null,
                    /* selectionArgs= */ null,
                    /* groupBy= */ null,
                    /* having= */ null,
                    /* orderBy= */ null)) {
                if (cursor.getCount() == 0) {
                    return ~0;
                }
                cursor.moveToNext();
                return cursor.getInt(/* COLUMN_VERSION_CODE index */ 0);
            }
        } catch (@NonNull SQLException e) {
            throw new DatabaseIOException(e);
        }
    }

    public static void printLog(@NonNull SQLiteDatabase db) throws DatabaseIOException {
        try {
            try (final Cursor cursor = db.query(TABLE_NAME,
                    /* columns= */ new String[]{
                            COLUMN_VERSION_ID,
                            COLUMN_VERSION_CODE,
                            COLUMN_VERSION_NAME,
                            COLUMN_CREATE_TIME,
                            COLUMN_UPDATE_TIME},
                    /* selection= */ null,
                    /* selectionArgs= */ null,
                    /* groupBy= */ null,
                    /* having= */ null,
                    /* orderBy= */ null)) {
                if (cursor.getCount() == 0) {
                    return;
                }
                UILog.e("Count: " + cursor.getCount());
                while (cursor.moveToNext()) {
                    final int N = cursor.getColumnCount();
                    UILog.e("========================== " + cursor.getPosition());
                    for (int index = 0; index < N; index++) {
                        final int columnType = cursor.getType(index);
                        if (columnType == Cursor.FIELD_TYPE_NULL) {
                            UILog.e("NULL");
                        } else if (columnType == Cursor.FIELD_TYPE_INTEGER) {
                            UILog.e(cursor.getColumnName(index) + ": " +
                                    +cursor.getInt(index));
                        } else if (columnType == Cursor.FIELD_TYPE_FLOAT) {
                            UILog.e(cursor.getColumnName(index) + ": "
                                    + cursor.getFloat(index));
                        } else if (columnType == Cursor.FIELD_TYPE_STRING) {
                            UILog.e(cursor.getColumnName(index) + ": "
                                    + cursor.getString(index));
                        } else if (columnType == Cursor.FIELD_TYPE_BLOB) {
                            UILog.e(cursor.getColumnName(index) + ": "
                                    + new String(cursor.getBlob(index), StandardCharsets.UTF_8));
                        } else {
                            UILog.e("Other: " + columnType);
                        }
                    }
                }
            }
        } catch (@NonNull SQLException e) {
            throw new DatabaseIOException(e);
        }
    }
}
