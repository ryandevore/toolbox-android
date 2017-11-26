package uu.toolbox;

import junit.framework.Assert;

/**
 * Unit test helpers
 */
public class UUAssert
{
    public static void assertSameArray(String message, byte[] expected, byte[] actual)
    {
        if (expected == null && actual != null)
        {
            Assert.fail(message);
        }

        if (expected != null && actual == null)
        {
            Assert.fail(message);
        }

        Assert.assertNotNull(message, expected);
        Assert.assertNotNull(message, actual);

        Assert.assertEquals(message, expected.length, actual.length);

        for (int i = 0; i < expected.length; i++)
        {
            Assert.assertEquals(message, expected[i], actual[i]);
        }
    }
}
