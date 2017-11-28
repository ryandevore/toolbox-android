package uu.toolbox;

import android.app.Application;
import android.test.ApplicationTestCase;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.HashMap;

import uu.toolbox.core.UUDate;
import uu.toolbox.core.UURandom;
import uu.toolbox.core.UUThread;
import uu.toolbox.data.UUDataCache;
import uu.toolbox.data.UUDataCacheProtocol;

public class UUDataCacheTests extends ApplicationTestCase<Application>
{
    private static final String TEST_FILE = "test_file.dat";
    private static final String TEST_FILE_SANITIZED = "test-file-dat";

    private static UUDataCache dataCache;

    public UUDataCacheTests()
    {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        if (dataCache == null)
        {
            UUDataCache.init(getContext().getApplicationContext());
            dataCache = UUDataCache.sharedInstance();
            dataCache.clearCache();
        }
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void test_0000_getFromCacheWhenNotExist() throws Exception
    {
        UUDataCacheProtocol dc = UUDataCache.sharedInstance();

        dc.removeData(TEST_FILE);

        byte[] contents = dc.getData(TEST_FILE);
        Assert.assertNull("Expect data to be null", contents);
    }

    public void test_0001_getSetFromCache() throws Exception
    {
        UUDataCacheProtocol dc = UUDataCache.sharedInstance();

        byte[] data = UURandom.randomBytes(512);
        dc.setData(data, TEST_FILE);

        byte[] contents = dc.getData(TEST_FILE);
        Assert.assertNotNull("Expect data to not be null", contents);

        UUAssert.assertSameArray("Expect to read same bytes that were written", contents, data);
    }

    public void test_0002_listKeysAfterWriting() throws Exception
    {
        UUDataCacheProtocol dc = UUDataCache.sharedInstance();

        ArrayList<String> keys = dc.listKeys();
        Assert.assertNotNull("Expect cached keys to never be null", keys);
        Assert.assertEquals("Expect cached key count to be one", 1, keys.size());

        String key = keys.get(0);
        Assert.assertEquals("Expect key to be our test file", TEST_FILE_SANITIZED, key);
    }

    public void test_0003_removeFromCache() throws Exception
    {
        UUDataCacheProtocol dc = UUDataCache.sharedInstance();

        byte[] contents = dc.getData(TEST_FILE);
        Assert.assertNotNull("Expect data to not be null", contents);

        dc.removeData(TEST_FILE);
        Assert.assertNotNull("Expect data to not be null", contents);

        byte[] contentsAfterDelete = dc.getData(TEST_FILE);
        Assert.assertNull("Expect data after deleting to be null", contentsAfterDelete);
    }

    public void test_0004_doesDataExist() throws Exception
    {
        UUDataCacheProtocol dc = UUDataCache.sharedInstance();

        boolean exists = dc.doesDataExist(TEST_FILE);
        Assert.assertFalse("Expect item not to exist in cache", exists);

        byte[] data = UURandom.randomBytes(357);
        UUDataCache.sharedInstance().setData(data, TEST_FILE);

        byte[] contents = dc.getData(TEST_FILE);
        Assert.assertNotNull("Expect data to not be null", contents);

        UUAssert.assertSameArray("Expect to read same bytes that were written", contents, data);

        exists = dc.doesDataExist(TEST_FILE);
        Assert.assertTrue("Expect item to exist in cache after writing", exists);
    }

    public void test_0005_getMetaDataAfterWriting() throws Exception
    {
        UUDataCacheProtocol dc = UUDataCache.sharedInstance();

        boolean exists = dc.doesDataExist(TEST_FILE);
        Assert.assertTrue("Expect item to exist in cache", exists);

        HashMap<String, Object> metaData = dc.getMetaData(TEST_FILE);
        Assert.assertNotNull("Expect meta data to never be null", metaData);

        Assert.assertEquals("Expect exactly one meta data key", 1, metaData.size());

        Assert.assertTrue("Expect one key to be the only key", metaData.keySet().contains(UUDataCache.MetaData.Timestamp));
    }

    public void test_0006_testIsDataExpired() throws Exception
    {
        UUDataCacheProtocol dc = UUDataCache.sharedInstance();
        dc.setDataExpirationInterval(UUDate.MILLIS_IN_ONE_WEEK);
        dc.removeData(TEST_FILE);

        byte[] data = UURandom.randomBytes(57);
        dc.setData(data, TEST_FILE);

        boolean isExpired = dc.isDataExpired(TEST_FILE);
        Assert.assertFalse("Expect data to not be expired after writing", isExpired);

        byte[] fetch = dc.getData(TEST_FILE);
        Assert.assertNotNull("Except data to exist after setting", fetch);

        data = UURandom.randomBytes(57);
        dc.setDataExpirationInterval(0);
        dc.setData(data, TEST_FILE);

        isExpired = dc.isDataExpired(TEST_FILE);
        Assert.assertTrue("Expect data to be expired", isExpired);

        fetch = dc.getData(TEST_FILE);
        Assert.assertNull("Except data not to exist when expired", fetch);
    }

    public void test_0007_testWithUnsafeFileNameKey() throws Exception
    {
        UUDataCacheProtocol dc = UUDataCache.sharedInstance();
        dc.setDataExpirationInterval(UUDate.MILLIS_IN_ONE_WEEK);

        String key = "http://thisisaurl.com&size=large&format=png";

        boolean existsBefore = dc.doesDataExist(key);
        Assert.assertFalse("Expect data to not exist before it has been added", existsBefore);

        byte[] getBefore = dc.getData(key);
        Assert.assertNull("Expect data to be null before it has been added", getBefore);

        byte[] data = UURandom.randomBytes(10);
        dc.setData(data, key);

        boolean existsAfter = dc.doesDataExist(key);
        Assert.assertTrue("Expect data to exist before it has been added", existsAfter);

        byte[] getAfter = dc.getData(key);
        Assert.assertNotNull("Expect data to be non null after it has been added", getAfter);

    }

    public void test_0008_testClearCache() throws Exception
    {
        UUDataCacheProtocol dc = UUDataCache.sharedInstance();

        int count = 10;
        for (int i = 0; i < count; i++)
        {
            byte[] data = UURandom.randomBytes(100);
            dc.setData(data, String.valueOf(i));
        }

        ArrayList<String> keys = dc.listKeys();
        Assert.assertTrue(keys.size() >= count);

        dc.clearCache();

        keys = dc.listKeys();
        Assert.assertEquals(0, keys.size());
    }

    public void test_0009_testPurgeIfExpired() throws Exception
    {
        UUDataCacheProtocol dc = UUDataCache.sharedInstance();
        dc.setDataExpirationInterval(UUDate.MILLIS_IN_ONE_WEEK);
        dc.clearCache();

        ArrayList<String> keys = dc.listKeys();
        Assert.assertEquals("Expect count after clear to be zero", 0, keys.size());

        int count = 10;
        for (int i = 0; i < count; i++)
        {
            byte[] data = UURandom.randomBytes(100);
            dc.setData(data, String.valueOf(i));
        }

        keys = dc.listKeys();
        Assert.assertEquals("Expect count after first add to be 10", count, keys.size());

        long sleepTime = 5 * 1000;

        UUThread.safeSleep("test_0009_purgeIfExpired", sleepTime);

        for (int i = 0; i < count; i++)
        {
            byte[] data = UURandom.randomBytes(100);
            dc.setData(data, String.valueOf(i + 100));
        }

        keys = dc.listKeys();
        Assert.assertEquals("Expect count after second add to be 20",count * 2, keys.size());

        dc.setDataExpirationInterval(sleepTime);
        dc.purgeExpiredData();

        keys = dc.listKeys();
        Assert.assertEquals("Expect count after purge to be 10", count, keys.size());
    }
}
