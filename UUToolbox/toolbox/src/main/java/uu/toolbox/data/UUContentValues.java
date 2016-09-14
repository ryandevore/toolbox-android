package uu.toolbox.data;

import android.content.ContentValues;

/**
 * A set of wrapper methods to work with ContentValues
 */
@SuppressWarnings("unused")
public final class UUContentValues
{
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Public Static Methods
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Sets a value if both the key and value are not null
     *
     * @param cv destination content values
     * @param key the key
     * @param value the value
     */
    public static void putIfNotNull(final ContentValues cv, final String key, final String value)
    {
        if (cv != null && key != null && value != null)
        {
            cv.put(key, value);
        }
    }

    /**
     * Sets a value if both the key and value are not null
     *
     * @param cv destination content values
     * @param key the key
     * @param value the value
     */
    public static void putIfNotNull(final ContentValues cv, final String key, final Long value)
    {
        if (cv != null && key != null && value != null)
        {
            cv.put(key, value);
        }
    }

    /**
     * Sets a value if both the key and value are not null
     *
     * @param cv destination content values
     * @param key the key
     * @param value the value
     */
    public static void putIfNotNull(final ContentValues cv, final String key, final Integer value)
    {
        if (cv != null && key != null && value != null)
        {
            cv.put(key, value);
        }
    }

    /**
     * Sets a value if both the key and value are not null
     *
     * @param cv destination content values
     * @param key the key
     * @param value the value
     */
    public static void putIfNotNull(final ContentValues cv, final String key, final Short value)
    {
        if (cv != null && key != null && value != null)
        {
            cv.put(key, value);
        }
    }

    /**
     * Sets a value if both the key and value are not null
     *
     * @param cv destination content values
     * @param key the key
     * @param value the value
     */
    public static void putIfNotNull(final ContentValues cv, final String key, final Byte value)
    {
        if (cv != null && key != null && value != null)
        {
            cv.put(key, value);
        }
    }

    /**
     * Sets a value if both the key and value are not null
     *
     * @param cv destination content values
     * @param key the key
     * @param value the value
     */
    public static void putIfNotNull(final ContentValues cv, final String key, final Boolean value)
    {
        if (cv != null && key != null && value != null)
        {
            cv.put(key, value);
        }
    }

    /**
     * Sets a value if both the key and value are not null
     *
     * @param cv destination content values
     * @param key the key
     * @param value the value
     */
    public static void putIfNotNull(final ContentValues cv, final String key, final byte[] value)
    {
        if (cv != null && key != null && value != null)
        {
            cv.put(key, value);
        }
    }

    /**
     * Sets a value if both the key and value are not null
     *
     * @param cv destination content values
     * @param key the key
     * @param value the value
     */
    public static void putIfNotNull(final ContentValues cv, final String key, final Double value)
    {
        if (cv != null && key != null && value != null)
        {
            cv.put(key, value);
        }
    }
}
