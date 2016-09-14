package uu.toolbox.core;

/**
 * Useful set of methods for manipulating Booleans
 *
 */
public class UUBoolean
{
    /**
     * Safely unboxes a Boolean object for its raw boolean value.
     *
     * @param obj the Boolean object to check
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
}
