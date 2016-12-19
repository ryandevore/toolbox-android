package uu.toolbox.core;

import android.support.annotation.Nullable;

/**
 * Useful set of methods for manipulating byte arrays
 *
 */
@SuppressWarnings("unused")
public class UUData
{
    public static @Nullable byte[] subData(final @Nullable byte[] source, final int offset, final int count)
    {
        if (source != null && offset >= 0 && count >= 0 && (offset + count) <= source.length)
        {
            byte[] dest = new byte[count];
            System.arraycopy(source, offset, dest, 0, count);
            return dest;
        }
        else
        {
            return null;
        }
    }

    /**
     * Checks a byte array for null and length
     *
     * @param b the byte array to check
     * @return true if the byte array is null or the length is zero
     */
    public static boolean isEmpty(final @Nullable byte[] b)
    {
        return (b == null || b.length == 0);
    }

    /**
     * Checks a byte array for null and length
     *
     * @param b the byte array to check
     * @return true if the byte array is not null and the length is greater zero
     */
    public static boolean isNotEmpty(final @Nullable byte[] b)
    {
        return (b != null && b.length > 0);
    }
}
