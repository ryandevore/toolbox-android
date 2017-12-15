package uu.toolbox;

import android.app.Application;
import android.content.Context;
import android.test.ApplicationTestCase;

import junit.framework.Assert;

import java.util.ArrayList;

import uu.toolbox.data.UUColumnDefinition;
import uu.toolbox.data.UUComplexDataModel;
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

            Assert.assertEquals("Expect one table name to be correct", UUTestDataModel.TABLE_NAME, table);

            db.getTableSchema(table);
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

        Assert.assertTrue("Expect tables to contain test data model", tables.contains(UUTestDataModel.TABLE_NAME));
        Assert.assertTrue("Expect tables to contain compound primary key test data model", tables.contains(UUDataModelWithCompoundKey.TABLE_NAME));

        db.getTableSchema(UUTestDataModel.TABLE_NAME);
        db.getTableSchema(UUDataModelWithCompoundKey.TABLE_NAME);
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

        Assert.assertTrue("Expect tables to contain test data model", tables.contains(UUTestDataModel.TABLE_NAME));
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

        ArrayList<UUColumnDefinition> schema = db.getTableSchema(UUComplexDataModel.TABLE_NAME);
        for (UUColumnDefinition cd : schema)
        {
            UULog.debug(getClass(), "test_0004_createWithComplexDataModels",
                    "cid: " + cd.cid + ", name: " + cd.name + ", type: " + cd.type +
            ", notnull: " + cd.notNull + ", defaultValue: " + (cd.defaultValue != null ? cd.defaultValue.toString() : "<null>") +
            ", pkIndex: " + cd.pkIndex);
        }
    }
}


