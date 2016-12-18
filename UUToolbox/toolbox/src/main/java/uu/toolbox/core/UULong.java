package uu.toolbox.core;

import uu.toolbox.logging.UULog;

/**
 * Useful set of methods for manipulating Longs
 *
 */
@SuppressWarnings("unused")
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

    public static Long safeParseAsLong(final String s, final Long defaultVal)
    {
        try
        {
            return Long.parseLong(s);
        }
        catch (Exception ex)
        {
            UULog.debug(UULong.class, "safeParseAsLong", ex);
            return defaultVal;
        }
    }

    public static Long safeParseAsLon(final String s, final int radix, final Long defaultVal)
    {
        try
        {
            return Long.parseLong(s, radix);
        }
        catch (Exception ex)
        {
            UULog.debug(UULong.class, "safeParseAsLong", ex);
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
