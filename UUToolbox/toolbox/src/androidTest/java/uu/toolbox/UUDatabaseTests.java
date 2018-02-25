package uu.toolbox;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.test.ApplicationTestCase;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Locale;

import uu.toolbox.data.UUComplexDataModel;
import uu.toolbox.data.UUCursor;
import uu.toolbox.data.UUDataModelWithCompoundKey;
import uu.toolbox.data.UUTestDataModel;
import uu.toolbox.data.UUTestDatabase;
import uu.toolbox.logging.UULog;

public class UUDatabaseTests extends ApplicationTestCase<Application>
{
    public UUDatabaseTests()
    {
        super(Application.class);
    }

    static class UUColumnDefinition
    {
        int cid;
        String name;
        String type;
        boolean notNull;
        Object defaultValue;
        int pkIndex;

        void fillFromCursor(@NonNull Cursor cursor)
        {
            cid = UUCursor.safeGetInt(cursor, "cid");
            name = UUCursor.safeGetString(cursor, "name");
            type = UUCursor.safeGetString(cursor, "type");
            notNull = UUCursor.safeGetBoolean(cursor, "notnull");
            defaultValue = UUCursor.safeGetString(cursor, "dflt_value");
            pkIndex = UUCursor.safeGetInt(cursor, "pk");
        }
    }

    private ArrayList<UUColumnDefinition> getTableSchema(
            @NonNull final SQLiteDatabase db,
            @NonNull final String tableName)
    {
        String sql = String.format(Locale.US, "PRAGMA table_info(%s);", tableName);

        ArrayList<UUColumnDefinition> results = new ArrayList<>();

        Cursor c = null;

        try
        {
            c = db.rawQuery(sql, null);

            while (c.moveToNext())
            {
                UUColumnDefinition obj = new UUColumnDefinition();
                obj.fillFromCursor(c);
                results.add(obj);
            }
        }
        catch (Exception ex)
        {
            Assert.fail("Caught an exception, ex: " + ex.toString());
        }
        finally
        {
            try
            {
                if (c != null)
                {
                    c.close();
                }
            }
            catch (Exception ex2)
            {
                Assert.fail("Caught an exception, ex2: " + ex2.toString());
            }
        }

        return results;
    }

    public void test_0000_initialCreate()
    {
        Context ctx = getContext();
        UUTestDatabase.CURRENT_VERSION = UUTestDatabase.VERSION_ONE;
        ctx.deleteDatabase(UUTestDatabase.NAME);


        UUTestDatabase db = new UUTestDatabase(ctx);

        ArrayList<String> tables = db.listTableNames();
        Assert.assertNotNull("Expect table names list to be non null", tables);

        // At this point our data model only has one table, but Android internally adds
        // 'android_metadata' and if any auto increment tables are used, a 'sqlite_sequence' table.
        Assert.assertTrue("Expect one table", tables.size() >= 1);

        for (String table: tables)
        {
            if ("android_metadata".equals(table) ||
                "sqlite_sequence".equals(table))
            {
                continue;
            }

            Assert.assertEquals("Expect one table name to be correct", UUTestDataModel.TableName, table);

            getTableSchema(db.getReadOnlyDatabase(), table);
        }
    }

    public void test_0001_upgradeToVersionTwo()
    {
        Context ctx = getContext();
        UUTestDatabase.CURRENT_VERSION = UUTestDatabase.VERSION_TWO;

        UUTestDatabase db = new UUTestDatabase(ctx);

        ArrayList<String> tables = db.listTableNames();
        Assert.assertNotNull("Expect table names list to be non null", tables);

        // At this point our data model only has one table, but Android internally adds
        // 'android_metadata' and if any auto increment tables are used, a 'sqlite_sequence' table.
        Assert.assertTrue("Expect more than one table", tables.size() >= 1);

        Assert.assertTrue("Expect tables to contain test data model", tables.contains(UUTestDataModel.TableName));
        Assert.assertTrue("Expect tables to contain compound primary key test data model", tables.contains(UUDataModelWithCompoundKey.TABLE_NAME));

        getTableSchema(db.getReadOnlyDatabase(), UUTestDataModel.TableName);
        getTableSchema(db.getReadOnlyDatabase(), UUDataModelWithCompoundKey.TABLE_NAME);
    }

    public void test_0002_downgradeToVersionOne()
    {
        Context ctx = getContext();
        UUTestDatabase.CURRENT_VERSION = UUTestDatabase.VERSION_ONE;

        UUTestDatabase db = new UUTestDatabase(ctx);

        ArrayList<String> tables = db.listTableNames();
        Assert.assertNotNull("Expect table names list to be non null", tables);

        // At this point our data model only has one table, but Android internally adds
        // 'android_metadata' and if any auto increment tables are used, a 'sqlite_sequence' table.
        Assert.assertTrue("Expect more than one table", tables.size() >= 1);

        Assert.assertTrue("Expect tables to contain test data model", tables.contains(UUTestDataModel.TableName));
    }

    public void test_0003_addObjectTwice()
    {
        Context ctx = getContext();
        UUTestDatabase.CURRENT_VERSION = UUTestDatabase.VERSION_TWO;
        ctx.deleteDatabase(UUTestDatabase.NAME);

        UUTestDatabase db = new UUTestDatabase(ctx);

        UUDataModelWithCompoundKey m = new UUDataModelWithCompoundKey();
        m.c1 = "one";
        m.c2 = "two";
        m.data = "This is some data";

        UUDataModelWithCompoundKey added = db.addObject(UUDataModelWithCompoundKey.class, m);
        Assert.assertNotNull("Expect add to succeed when object does not exist");
        Assert.assertEquals(m.c1, added.c1);
        Assert.assertEquals(m.c2, added.c2);
        Assert.assertEquals(m.data, added.data);

        UUDataModelWithCompoundKey added2 = db.addObject(UUDataModelWithCompoundKey.class, m);
        Assert.assertNull("Expect add to fail when object exists", added2);
    }

    public void test_0004_createWithComplexDataModels()
    {
        Context ctx = getContext();
        UUTestDatabase.CURRENT_VERSION = UUTestDatabase.VERSION_THREE;
        ctx.deleteDatabase(UUTestDatabase.NAME);

        UUTestDatabase db = new UUTestDatabase(ctx);

        ArrayList<UUColumnDefinition> schema = getTableSchema(db.getReadOnlyDatabase(), UUComplexDataModel.TableName);
        for (UUColumnDefinition cd : schema)
        {
            UULog.debug(getClass(), "test_0004_createWithComplexDataModels",
                    "cid: " + cd.cid + ", name: " + cd.name + ", type: " + cd.type +
            ", notnull: " + cd.notNull + ", defaultValue: " + (cd.defaultValue != null ? cd.defaultValue.toString() : "<null>") +
            ", pkIndex: " + cd.pkIndex);
        }
    }
}


