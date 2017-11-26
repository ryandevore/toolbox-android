package uu.toolbox;

import android.app.Application;
import android.test.ApplicationTestCase;

import junit.framework.Assert;

import java.io.File;

import uu.toolbox.core.UUFile;
import uu.toolbox.core.UURandom;

public class UUFileTests extends ApplicationTestCase<Application>
{
    private static String TEST_FILE_NAME = "DummyFile.dat";

    public UUFileTests()
    {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    private File getTestFile()
    {
        File file = new File(getContext().getCacheDir(), TEST_FILE_NAME);
        Assert.assertNotNull("Expect file object to not be null", file);
        return file;
    }

    public void test_0000_ReadFileThatDoesntExist() throws Exception
    {
        File file = getTestFile();
        if (file.exists())
        {
            boolean result = file.delete();
            Assert.assertTrue(result);
        }

        Assert.assertFalse("Expect file not to exist", file.exists());

        byte[] contents = UUFile.readFile(file);
        Assert.assertNull("Expect null data when file doesn't exist", contents);
    }

    public void test_0001_DeleteFileThatDoesNotExist() throws Exception
    {
        File file = getTestFile();
        if (file.exists())
        {
            boolean result = file.delete();
            Assert.assertTrue(result);
        }

        Assert.assertFalse("Expect file not to exist", file.exists());

        boolean result = UUFile.deleteFile(file);
        Assert.assertFalse("Expect delete file on non existent file to return false", result);
    }

    public void test_0002_tWriteFileThatDoesntExist() throws Exception
    {
        File file = getTestFile();
        Assert.assertFalse("Expect file not to exist", file.exists());

        byte[] data = UURandom.randomBytes(256);
        boolean result = UUFile.writeFile(file, data);
        Assert.assertTrue("Expect write file to cache to work", result);
        Assert.assertTrue("Expect file to exist after writing", file.exists());
        Assert.assertTrue("Expect file to be readable after writing", file.canRead());
        Assert.assertTrue("Expect file to be writable after writing", file.canWrite());

        byte[] contents = UUFile.readFile(file);
        Assert.assertNotNull("Expect actual data when file does exist", contents);

        Assert.assertNotSame("Expect readFile to return same bytes that were written", contents, data);
    }

    public void test_0003_DeleteFileThatDoesExist() throws Exception
    {
        File file = getTestFile();
        Assert.assertTrue("Expect file to exist", file.exists());

        boolean result = UUFile.deleteFile(file);
        Assert.assertTrue("Expect delete file to return true", result);
    }
}

