package uu.toolbox.core;

import uu.toolbox.logging.UULog;

/**
 * Useful set of methods for manipulating Double
 *
 */
public class UUDouble
{
    public static double safeParse(final String s, final long defaultVal)
    {
        try
        {
            return Double.parseDouble(s);
        }
        catch (Exception ex)
        {
            UULog.debug(UUDouble.class, "safeParse", ex);
            return defaultVal;
        }
    }

    public static boolean areEqual(final Double lhs, final Double rhs)
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
            return (lhs.doubleValue() == rhs.doubleValue());
        }
    }
}
