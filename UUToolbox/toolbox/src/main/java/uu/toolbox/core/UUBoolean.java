package uu.toolbox.core;

/**
 * Useful set of methods for manipulating Booleans
 *
 */
@SuppressWarnings("unused")
public class UUBoolean
{
    /**
     * Safely unboxes a Boolean object for its raw value.
     *
     * @param obj the object to check
     * @param defaultValue the default value to return if the
     * @return if obj is null returns default value, otherwise returns the value of obj.boolValue()
     */
    public static boolean safeGetValue(final Boolean obj, final boolean defaultValue)
    {
        if (obj != null)
        {
            return obj;
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * Safely checks a Boolean for a true result.
     *
     * @param bool the Boolean to check
     * @return returns true if the instance is non null and the .boolValue() is equal to true.
     */
    public static boolean isTrue(final Boolean bool)
    {
        return safeGetValue(bool, false);
    }

    /**
     * Safely checks a Boolean for a false result.
     *
     * @param bool the Boolean to check
     * @return returns true if the instance is null or non-null the .boolValue() is equal to false.
     */
    public static boolean isFalse(final Boolean bool)
    {
        return !safeGetValue(bool, true);
    }

    /**
     * Safely checks two booleans for equality
     *
     * @param lhs the left hand side to check
     * @param rhs the right hand side to check
     * @return true if they are equal, false if not
     */
    public static boolean areEqual(final Boolean lhs, final Boolean rhs)
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
            return (lhs.booleanValue() == rhs.booleanValue());
        }
    }

    /**
     * Safely compares two Boolean's
     *
     * @param lhs the left hand side to check
     * @param rhs the right hand side to check
     * @return comparison result, -1, 0, or 1
     */
    public static int compare(final Boolean lhs, final Boolean rhs)
    {
        boolean left = lhs;
        boolean right = rhs;

        if (left == right)
        {
            return 0;
        }
        else
        {
            return left ? -1 : 1;
        }
    }
}
