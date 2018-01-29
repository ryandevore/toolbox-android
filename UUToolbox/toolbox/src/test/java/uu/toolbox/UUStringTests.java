package uu.toolbox;

import junit.framework.Assert;

import org.junit.Test;

import uu.toolbox.core.UUString;

public class UUStringTests
{
    @Test
    public void byteToAscii() throws Exception
    {
        String input = "The quick brown fox jumps over the lazy dog.";

        byte[] output = UUString.stringToAsciiBytes(input);

        String check = UUString.byteToAsciiString(output);

        Assert.assertEquals("Expect conversion to and from bytes yields the same input", input, check);

        byte[] buffer = new byte[output.length + 5];
        System.arraycopy(output, 0, buffer, 0, output.length);

        String check2 = UUString.byteToAsciiString(buffer).trim();// .replace("\0", "");

        Assert.assertEquals("Expect conversion to and from bytes with extra zeros yields the same input", input, check2);
    }
}
