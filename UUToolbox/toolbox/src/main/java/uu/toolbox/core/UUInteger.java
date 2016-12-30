package uu.toolbox.core;

import uu.toolbox.logging.UULog;

/**
 * Useful set of methods for manipulating Integers
 *
 */
@SuppressWarnings("unused")
public class UUInteger
{
    /**
     * Safely parses a string into an integer
     *
     * @param s the string to parse
     * @param defaultVal the default value is an exception is thrown
     * @return the result of Integer.parseInt, or the default value
     */
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

    /**
     * Safely parses a string into an integer
     *
     * @param s the string to parse
     * @param defaultVal the default value is an exception is thrown
     * @param radix the number radix to use
     * @return the result of Integer.parseInt, or the default value
     */
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

    /**
     * Safely parses a string into an Integer
     *
     * @param s the string to parse
     * @param defaultVal the default value is an exception is thrown
     * @return the result of Integer.parseInt, or the default value
     */
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

    /**
     * Safely parses a string into an integer
     *
     * @param s the string to parse
     * @param defaultVal the default value is an exception is thrown
     * @param radix the number radix to use
     * @return the result of Integer.parseInt, or the default value
     */
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

    /**
     * Safely checks two Integer's for equality
     *
     * @param lhs the left hand side to check
     * @param rhs the right hand side to check
     * @return true if they are equal, false if not
     */
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

    /**
     * Safely compares two Integer's
     *
     * @param lhs the left hand side to check
     * @param rhs the right hand side to check
     * @return comparison result, -1, 0, or 1
     */
    public static int compare(final Integer lhs, final Integer rhs)
    {
        int left = lhs;
        int right = rhs;

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
