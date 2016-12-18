package uu.toolbox.core;

import uu.toolbox.logging.UULog;

/**
 * Useful set of methods for manipulating Double
 *
 */
@SuppressWarnings("unused")
public class UUDouble
{
    public static double safeParse(final String s, final double defaultVal)
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

    public static Double safeParseAsDouble(final String s, final Double defaultVal)
    {
        try
        {
            if (UUString.isNotEmpty(s))
            {
                return Double.parseDouble(s);
            }
        }
        catch (Exception ex)
        {
            UULog.debug(UUDouble.class, "safeParseAsDouble", ex);
        }

        return defaultVal;
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
