package uu.toolbox.core;

import uu.toolbox.logging.UULog;

/**
 * Useful set of methods for manipulating Longs
 *
 */
public class UULong
{
    public static long safeParse(final String s, final long defaultVal)
    {
        try
        {
            return Long.parseLong(s);
        }
        catch (Exception ex)
        {
            UULog.debug(UULong.class, "safeParse", ex);
            return defaultVal;
        }
    }

    public static long safeParse(final String s, final int radix, final long defaultVal)
    {
        try
        {
            return Long.parseLong(s, radix);
        }
        catch (Exception ex)
        {
            UULog.debug(UULong.class, "safeParse", ex);
            return defaultVal;
        }
    }

    public static boolean areEqual(final Long lhs, final Long rhs)
    {
        if (lhs == null && rhs == null)
        {
            return true;
        }
        else if (lhs == null || rhs == null)
        {
            return false;
        }
        else
        {
            return (lhs.longValue() == rhs.longValue());
        }
    }
}
