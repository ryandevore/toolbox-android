package uu.toolbox.core;

import uu.toolbox.logging.UULog;

/**
 * Useful set of methods for manipulating Floats
 *
 */
public class UUFloat
{
    public static float safeParse(final String s, final long defaultVal)
    {
        try
        {
            return Float.parseFloat(s);
        }
        catch (Exception ex)
        {
            UULog.debug(UUFloat.class, "safeParse", ex);
            return defaultVal;
        }
    }

    public static boolean areEqual(final Float lhs, final Float rhs)
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
            return (lhs.floatValue() == rhs.floatValue());
        }
    }
}
