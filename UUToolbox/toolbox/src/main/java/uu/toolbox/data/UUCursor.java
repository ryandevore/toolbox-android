package uu.toolbox.data;

import android.database.Cursor;

/**
 * A set of helper methods for the Cursor class
 */
@SuppressWarnings("unused")
public final class UUCursor
{
    public static Short safeGetShort(final Cursor cursor, final Object column)
    {
        return safeGetShort(cursor, column, null);
    }

    public static Short safeGetShort(final Cursor cursor, final Object column, final Short defaultValue)
    {
        Short result = defaultValue;

        if (cursor != null && column != null)
        {
            int index = cursor.getColumnIndex(column.toString());
            if (index >= 0 && !cursor.isNull(index))
            {
                result = cursor.getShort(index);
            }
        }

        return result;
    }

    public static Integer safeGetInt(final Cursor cursor, final Object column)
    {
        return safeGetInt(cursor, column, null);
    }

    public static Integer safeGetInt(final Cursor cursor, final Object column, final Integer defaultValue)
    {
        Integer result = defaultValue;

        if (cursor != null && column != null)
        {
            int index = cursor.getColumnIndex(column.toString());
            if (index >= 0 && !cursor.isNull(index))
            {
                result = cursor.getInt(index);
            }
        }

        return result;
    }

    public static Long safeGetLong(final Cursor cursor, final Object column)
    {
        return safeGetLong(cursor, column, null);
    }

    public static Long safeGetLong(final Cursor cursor, final Object column, final Long defaultValue)
    {
        Long result = defaultValue;

        if (cursor != null && column != null)
        {
            int index = cursor.getColumnIndex(column.toString());
            if (index >= 0 && !cursor.isNull(index))
            {
                result = cursor.getLong(index);
            }
        }

        return result;
    }

    public static Boolean safeGetBoolean(final Cursor cursor, final Object column)
    {
        return safeGetBoolean(cursor, column, null);
    }

    public static Boolean safeGetBoolean(final Cursor cursor, final Object column, final Boolean defaultValue)
    {
        Boolean result = defaultValue;

        if (cursor != null && column != null)
        {
            int index = cursor.getColumnIndex(column.toString());
            if (index >= 0 && !cursor.isNull(index))
            {
                result = (cursor.getInt(index) == 1);
            }
        }

        return result;
    }

    public static Float safeGetFloat(final Cursor cursor, final Object column)
    {
        return safeGetFloat(cursor, column, null);
    }

    public static Float safeGetFloat(final Cursor cursor, final Object column, final Float defaultValue)
    {
        Float result = defaultValue;

        if (cursor != null && column != null)
        {
            int index = cursor.getColumnIndex(column.toString());
            if (index >= 0 && !cursor.isNull(index))
            {
                result = cursor.getFloat(index);
            }
        }

        return result;
    }

    public static Double safeGetDouble(final Cursor cursor, final Object column)
    {
        return safeGetDouble(cursor, column, null);
    }

    public static Double safeGetDouble(final Cursor cursor, final Object column, final Double defaultValue)
    {
        Double result = defaultValue;

        if (cursor != null && column != null)
        {
            int index = cursor.getColumnIndex(column.toString());
            if (index >= 0 && !cursor.isNull(index))
            {
                result = cursor.getDouble(index);
            }
        }

        return result;
    }

    public static byte[] safeGetBlob(final Cursor cursor, final Object column)
    {
        return safeGetBlob(cursor, column, null);
    }

    public static byte[] safeGetBlob(final Cursor cursor, final Object column, final byte[] defaultValue)
    {
        byte[] result = defaultValue;

        if (cursor != null && column != null)
        {
            int index = cursor.getColumnIndex(column.toString());
            if (index >= 0 && !cursor.isNull(index))
            {
                result = cursor.getBlob(index);
            }
        }

        return result;
    }

    public static String safeGetString(final Cursor cursor, final Object column)
    {
        return safeGetString(cursor, column, null);
    }

    public static String safeGetString(final Cursor cursor, final Object column, final String defaultValue)
    {
        String result = defaultValue;

        if (cursor != null && column != null)
        {
            int index = cursor.getColumnIndex(column.toString());
            if (index >= 0 && !cursor.isNull(index))
            {
                result = cursor.getString(index);
            }
        }

        return result;
    }
}
