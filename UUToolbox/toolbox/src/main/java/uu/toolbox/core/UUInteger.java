package uu.toolbox.core;

import uu.toolbox.logging.UULog;

/**
 * Useful set of methods for manipulating Integers
 *
 */
@SuppressWarnings("unused")
public class UUInteger
{
    public static int safeParse(final String s, final int defaultVal)
    {
        try
        {
            return Integer.parseInt(s);
        }
        catch (Exception ex)
        {
            UULog.debug(UUInteger.class, "safeParse", ex);
            return defaultVal;
        }
    }

    public static int safeParse(final String s, final int radix, final int defaultVal)
    {
        try
        {
            return Integer.parseInt(s, radix);
        }
        catch (Exception ex)
        {
            UULog.debug(UUInteger.class, "safeParse", ex);
            return defaultVal;
        }
    }

    public static Integer safeParseAsInteger(final String s, final Integer defaultVal)
    {
        try
        {
            return Integer.parseInt(s);
        }
        catch (Exception ex)
        {
            UULog.debug(UUInteger.class, "safeParseAsInteger", ex);
            return defaultVal;
        }
    }

    public static Integer safeParseAsInteger(final String s, final int radix, final Integer defaultVal)
    {
        try
        {
            return Integer.parseInt(s, radix);
        }
        catch (Exception ex)
        {
            UULog.debug(UUInteger.class, "safeParseAsInteger", ex);
            return defaultVal;
        }
    }

    public static boolean areEqual(final Integer lhs, final Integer rhs)
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
