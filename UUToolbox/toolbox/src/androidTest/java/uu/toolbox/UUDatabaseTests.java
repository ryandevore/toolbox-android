package uu.toolbox;

import android.app.Application;
import android.test.ApplicationTestCase;

import junit.framework.Assert;

import uu.toolbox.data.UUDataModelWithCompoundKey;
import uu.toolbox.data.UUTestDatabase;

public class UUDatabaseTests extends ApplicationTestCase<Application>
{
    public UUDatabaseTests()
    {
        super(Application.class);
    }

    public void testAddObjectTwice()
    {
        UUTestDatabase db = new UUTestDatabase(getContext());
        db.destroy();

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
}


