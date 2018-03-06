package uu.toolbox.data;

import android.content.ContentValues;

import uu.toolbox.logging.UULog;

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
    public static void putIfNotNull(final ContentValues cv, final Object key, final String value)
    {
        if (cv != null && key != null && value != null)
        {
            cv.put(key.toString(), value);
        }
    }

    /**
     * Sets a value if both the key and value are not null
     *
     * @param cv destination content values
     * @param key the key
     * @param value the value
     */
    public static void putIfNotNull(final ContentValues cv, final Object key, final Long value)
    {
        if (cv != null && key != null && value != null)
        {
            cv.put(key.toString(), value);
        }
    }

    /**
     * Sets a value if both the key and value are not null
     *
     * @param cv destination content values
     * @param key the key
     * @param value the value
     */
    public static void putIfNotNull(final ContentValues cv, final Object key, final Integer value)
    {
        if (cv != null && key != null && value != null)
        {
            cv.put(key.toString(), value);
        }
    }

    /**
     * Sets a value if both the key and value are not null
     *
     * @param cv destination content values
     * @param key the key
     * @param value the value
     */
    public static void putIfNotNull(final ContentValues cv, final Object key, final Short value)
    {
        if (cv != null && key != null && value != null)
        {
            cv.put(key.toString(), value);
        }
    }

    /**
     * Sets a value if both the key and value are not null
     *
     * @param cv destination content values
     * @param key the key
     * @param value the value
     */
    public static void putIfNotNull(final ContentValues cv, final Object key, final Byte value)
    {
        if (cv != null && key != null && value != null)
        {
            cv.put(key.toString(), value);
        }
    }

    /**
     * Sets a value if both the key and value are not null
     *
     * @param cv destination content values
     * @param key the key
     * @param value the value
     */
    public static void putIfNotNull(final ContentValues cv, final Object key, final Boolean value)
    {
        if (cv != null && key != null && value != null)
        {
            cv.put(key.toString(), value);
        }
    }

    /**
     * Sets a value if both the key and value are not null
     *
     * @param cv destination content values
     * @param key the key
     * @param value the value
     */
    public static void putIfNotNull(final ContentValues cv, final Object key, final byte[] value)
    {
        if (cv != null && key != null && value != null)
        {
            cv.put(key.toString(), value);
        }
    }

    /**
     * Sets a value if both the key and value are not null
     *
     * @param cv destination content values
     * @param key the key
     * @param value the value
     */
    public static void putIfNotNull(final ContentValues cv, final Object key, final Float value)
    {
        if (cv != null && key != null && value != null)
        {
            cv.put(key.toString(), value);
        }
    }

    /**
     * Sets a value if both the key and value are not null
     *
     * @param cv destination content values
     * @param key the key
     * @param value the value
     */
    public static void putIfNotNull(final ContentValues cv, final Object key, final Double value)
    {
        if (cv != null && key != null && value != null)
        {
            cv.put(key.toString(), value);
        }
    }

    /**
     * Sets a value if both the key and value are not null
     *
     * @param cv destination content values
     * @param key the key
     * @param value the value
     */
    public static void putIfNotNull(final ContentValues cv, final Object key, final Character value)
    {
        if (cv != null && key != null && value != null)
        {
            cv.put(key.toString(), (byte)value.charValue());
        }
    }

    /**
     * Sets a value if both the key and value are not null
     *
     * @param cv destination content values
     * @param key the key
     * @param value the value
     */
    public static void putObjectIfNotNull(final ContentValues cv, final Object key, final Object value)
    {
        if (cv != null && key != null && value != null)
        {
            Class fieldType = value.getClass();

            if (fieldType == String.class)
            {
                UUContentValues.putIfNotNull(cv, key, (String)value);
            }
            else if (fieldType == Long.class || fieldType == long.class)
            {
                UUContentValues.putIfNotNull(cv, key, (Long)value);
            }
            else if (fieldType == Integer.class || fieldType == int.class)
            {
                UUContentValues.putIfNotNull(cv, key, (Integer)value);
            }
            else if (fieldType == Short.class || fieldType == short.class)
            {
                UUContentValues.putIfNotNull(cv, key, (Short)value);
            }
            else if (fieldType == Byte.class || fieldType == byte.class)
            {
                UUContentValues.putIfNotNull(cv, key, (Byte)value);
            }
            else if (fieldType == Float.class || fieldType == float.class)
            {
                UUContentValues.putIfNotNull(cv, key, (Float)value);
            }
            else if (fieldType == Double.class || fieldType == Double.class)
            {
                UUContentValues.putIfNotNull(cv, key, (Double)value);
            }
            else if (fieldType == Boolean.class || fieldType == boolean.class)
            {
                UUContentValues.putIfNotNull(cv, key, (Boolean)value);
            }
            else if (fieldType == byte[].class)
            {
                UUContentValues.putIfNotNull(cv, key, (byte[])value);
            }
            else if (fieldType == Byte[].class)
            {
                Byte[] val = (Byte[])value;
                byte[] tmp = new byte[val.length];
                for (int i = 0; i < tmp.length; i++)
                {
                    tmp[i] = val[i];
                }

                UUContentValues.putIfNotNull(cv, key, tmp);
            }
            else if (fieldType == Character.class || fieldType == char.class)
            {
                UUContentValues.putIfNotNull(cv, key, (Character)value);
            }
            else
            {
                UULog.debug(UUDataModel.class, "putObjectIfNotNull", "Field Type " + fieldType + " cannot be coerced into a type to put into ContentValues");
            }
        }
    }

    /*
    public static void putFieldByReflection(@NonNull final ContentValues cv, @NonNull final String name, @NonNull final Object object, @NonNull final Field field)
    {
        try
        {
            Class fieldType = field.getType();

            Object val = field.get(object);

            if (fieldType == String.class)
            {
                UUContentValues.putIfNotNull(cv, name, (String)field.get(object));
            }
            else if (fieldType == Long.class)
            {
                UUContentValues.putIfNotNull(cv, name, (Long)field.get(object));
            }
            else if (fieldType == Integer.class)
            {
                UUContentValues.putIfNotNull(cv, name, (Integer)field.get(object));
            }
            else if (fieldType == Short.class)
            {
                UUContentValues.putIfNotNull(cv, name, (Short)field.get(object));
            }
            else if (fieldType == Byte.class)
            {
                UUContentValues.putIfNotNull(cv, name, (Byte)field.get(object));
            }
            else if (fieldType == Float.class)
            {
                UUContentValues.putIfNotNull(cv, name, (float)val);
            }
            else if (fieldType == Double.class)
            {
                UUContentValues.putIfNotNull(cv, name, (Double)field.get(object));
            }
            else if (fieldType == Boolean.class)
            {
                UUContentValues.putIfNotNull(cv, name, (Boolean)field.get(object));
            }
            else if (fieldType == Byte[].class)
            {
                UUContentValues.putIfNotNull(cv, name, (byte[])field.get(object));
            }
            else if (fieldType == long.class)
            {
                UUContentValues.putIfNotNull(cv, name, field.getLong(object));
            }
            else if (fieldType == int.class)
            {
                UUContentValues.putIfNotNull(cv, name, field.getInt(object));
            }
            else if (fieldType == short.class)
            {
                UUContentValues.putIfNotNull(cv, name, field.getShort(object));
            }
            else if (fieldType == byte.class)
            {
                UUContentValues.putIfNotNull(cv, name, field.getByte(object));
            }
            else if (fieldType == float.class)
            {
                UUContentValues.putIfNotNull(cv, name, (float)val);//field.getFloat(object));
            }
            else if (fieldType == Double.class)
            {
                UUContentValues.putIfNotNull(cv, name, field.getFloat(object));
            }
            else if (fieldType == boolean.class)
            {
                UUContentValues.putIfNotNull(cv, name, field.getBoolean(object));
            }
            else if (fieldType == byte[].class)
            {
                UUContentValues.putIfNotNull(cv, name, (byte[])field.get(object));
            }
            else
            {
                UULog.debug(UUDataModel.class, "putField", "Field Type " + fieldType + " cannot be coerced into SQLite type!");
            }
        }
        catch (Exception ex)
        {
            UULog.debug(UUDataModel.class, "putField", ex);
        }
    }*/
}
