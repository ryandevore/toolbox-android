package uu.toolbox.core;

import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * Created by ryandevore on 2/24/18.
 */

public class UUJsonTests extends ApplicationTestCase<Application>
{
    public UUJsonTests()
    {
        super(Application.class);
    }



    /*public void test_0000_objectToJson() throws Exception
    {
        TestObject o = new TestObject();
        o.stringField = "UnitTest";
        o.rawIntField = 57;
        o.objectIntField = 22;
        o.anotherString = null;
        o.stringArray = new String[] { "fee", "fy" ,"fo", "fum" };
        o.intArray = null;

        JSONObject json = UUJson.objectToJson(o);
        Assert.assertNotNull(json);
        String jsonString = json.toString();
        UULog.debug(getClass(), "objectToJson", "JSON: " + jsonString);
    }*/
}

class TestObject
{
    String stringField;
    int rawIntField;
    Integer objectIntField;
    String anotherString;
    String[] stringArray;
    int[] intArray;
}
