package uu.toolbox.core;

import uu.toolbox.logging.UULog;

/**
 * Useful set of methods for manipulating Double
 *
 */
@SuppressWarnings("unused")
public class UUDouble
{
    /**
     * Safely parses a string into a double
     *
     * @param s the string to parse
     * @param defaultVal the default value is an exception is thrown
     * @return the result of Double.parseDouble, or the default value
     */
    public static double safeParse(final String s, final double defaultVal)
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
            UULog.debug(UUDouble.class, "safeParse", ex);
        }

        return defaultVal;
    }

    /**
     * Safely parses a string into a double
     *
     * @param s the string to parse
     * @param defaultVal the default value is an exception is thrown
     * @return the result of Double.parseDouble, or the default value
     */
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

    /**
     * Safely checks two Double's for equality
     *
     * @param lhs the left hand side to check
     * @param rhs the right hand side to check
     * @return true if they are equal, false if not
     */
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

    /**
     * Safely compares two Double's
     *
     * @param lhs the left hand side to check
     * @param rhs the right hand side to check
     * @return comparison result, -1, 0, or 1
     */
    public static int compare(final Double lhs, final Double rhs)
    {
        double left = lhs;
        double right = rhs;

        if (left == right)
        {
            return 0;
        }
        else
        {
            return (left < right) ? -1 : 1;
        }
    }
}
