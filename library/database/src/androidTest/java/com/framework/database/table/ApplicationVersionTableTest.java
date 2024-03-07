package com.framework.database.table;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.platform.app.InstrumentationRegistry;

import com.framework.database.DatabaseProvider;
import com.framework.database.StandaloneDatabaseProvider;
import com.framework.database.io.DatabaseIOException;

import junit.framework.TestCase;

/**
 * @Author create by Zhengzelong on 2024-03-07
 * @Email : 171905184@qq.com
 * @Description :
 */
public class ApplicationVersionTableTest extends TestCase {
    private DatabaseProvider databaseProvider;

    public void setUp() throws Exception {
        final Context context;
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        this.databaseProvider = new StandaloneDatabaseProvider(context);
    }

    public void testSet() throws DatabaseIOException {
        final SQLiteDatabase db = this.databaseProvider.getWritableDatabase();
        ApplicationVersionTable.set(db, 100, "100");
        assertEquals(ApplicationVersionTable.getVersionCode(db), 100);
    }

    public void testPrintLog() throws DatabaseIOException {
        final SQLiteDatabase db = this.databaseProvider.getWritableDatabase();
        ApplicationVersionTable.printLog(db);
    }
}