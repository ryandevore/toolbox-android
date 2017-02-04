package uu.toolbox.core;

import uu.toolbox.logging.UULog;

/**
 * Useful set of methods for manipulating Longs
 *
 */
@SuppressWarnings("unused")
public class UULong
{
    /**
     * Safely parses a string into a long
     *
     * @param s the string to parse
     * @param defaultVal the default value is an exception is thrown
     * @return the result of Long.parseLong, or the default value
     */
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

    /**
     * Safely parses a string into a long
     *
     * @param s the string to parse
     * @param defaultVal the default value is an exception is thrown
     * @param radix the number radix to use
     * @return the result of Long.parseLong, or the default value
     */
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

    /**
     * Safely parses a string into a Long
     *
     * @param s the string to parse
     * @param defaultVal the default value is an exception is thrown
     * @return the result of Long.parseLong, or the default value
     */
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

    /**
     * Safely parses a string into a Long
     *
     * @param s the string to parse
     * @param defaultVal the default value is an exception is thrown
     * @param radix the number radix to use
     * @return the result of Long.parseLong, or the default value
     */
    public static Long safeParseAsLong(final String s, final int radix, final Long defaultVal)
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

    /**
     * Safely checks two Long's for equality
     *
     * @param lhs the left hand side to check
     * @param rhs the right hand side to check
     * @return true if they are equal, false if not
     */
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

    /**
     * Safely compares two Long's
     *
     * @param lhs the left hand side to check
     * @param rhs the right hand side to check
     * @return comparison result, -1, 0, or 1
     */
    public static int compare(final Long lhs, final Long rhs)
    {
        long left = lhs;
        long right = rhs;

        if (left == right)
        {
            return 0;
        }
        else
        {
            return (left < right) ? -1 : 1;
        }
    }

    /**
     * Evaluates two values to determine if one contains bits from the other
     *
     * @param value the source value to check
     * @param mask the mask of bits to check
     * @return true if the bits are found in the value, false otherwise
     */
    public static boolean isBitSet(final long value, final long mask)
    {
        return ((value & mask) == mask);
    }
}
