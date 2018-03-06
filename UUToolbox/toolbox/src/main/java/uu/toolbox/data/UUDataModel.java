package uu.toolbox.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import uu.toolbox.core.UUString;
import uu.toolbox.logging.UULog;

/**
 * Defines a data model for a SQLite table
 */
public interface UUDataModel
{
    /**
     * The table name
     *
     * @return a valid SQLite table name.  Must not be null.
     */
    @NonNull
    default String getTableName()
    {
        return tableNameForClass(getClass());
    }

    /**
     * Returns the mapping of column name to data type
     *
     * @param version the version of the table to get
     * @return a non null hash map of column definitions. Must not be null
     */
    @NonNull
    default HashMap<Object, Object> getColumnMap(final int version)
    {
        HashMap<Object, Object> map = new HashMap<>();

        try
        {
            Field[] fields = getClass().getDeclaredFields();
            for (Field field : fields)
            {
                field.setAccessible(true);

                UUSqlColumn columnAnnotation = field.getAnnotation(UUSqlColumn.class);
                if (columnAnnotation != null)
                {
                    if (version >= columnAnnotation.existsInVersion())
                    {
                        map.put(columnNameForField(field), buildColumnCreateType(field, columnAnnotation));
                    }
                }
            }
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), "getColumnMap", ex);
        }

        return map;
    }

    /**
     * Returns the primary key column name
     *
     * @return May return null if a column definition is a primary key auto increment
     */
    @Nullable
    default String getPrimaryKeyColumnName()
    {
        StringBuilder sb = new StringBuilder();

        try
        {
            Field[] fields = getClass().getDeclaredFields();
            for (Field field : fields)
            {
                field.setAccessible(true);

                UUSqlColumn columnAnnotation = field.getAnnotation(UUSqlColumn.class);
                if (columnAnnotation != null)
                {
                    // Immediately return null if auto increment int is used.
                    if (columnAnnotation.type() == UUSqlColumn.Type.INTEGER_PRIMARY_KEY_AUTOINCREMENT)
                    {
                        return null;
                    }

                    if (columnAnnotation.primaryKey())
                    {
                        if (sb.length() > 0)
                        {
                            sb.append(" , " );
                        }

                        sb.append(columnNameForField(field));
                    }
                }
            }
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), "getPrimaryKeyColumnName", ex);
        }

        if (sb.length() == 0)
        {
            return null;
        }

        return sb.toString();
    }

    /**
     * Returns a formatted string that will be used as a SQL WHERE clause when looking up
     * an object from this table by primary key.
     *
     * @return A valid Where clause.  For example, "id = ?". Must not be null.
     *
     */
    @NonNull
    default String getPrimaryKeyWhereClause()
    {
        StringBuilder sb = new StringBuilder();

        try
        {
            Field[] fields = getClass().getDeclaredFields();
            for (Field field : fields)
            {
                field.setAccessible(true);

                UUSqlColumn columnAnnotation = field.getAnnotation(UUSqlColumn.class);
                if (columnAnnotation != null)
                {
                    if (isPrimaryKeyColumn(columnAnnotation))
                    {
                        if (sb.length() > 0)
                        {
                            sb.append(" AND " );
                        }

                        sb.append(String.format(Locale.US, "%s = ?", columnNameForField(field)));
                    }
                }
            }
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), "getPrimaryKeyWhereClause", ex);
        }

        return sb.toString();
    }

    /**
     * Returns a string array of the primary key for this object that can be used in SQL WHERE clauses
     *
     * @return A formatted string that will be passed to selection args for Android SQLite methods.
     */
    @NonNull
    default String[] getPrimaryKeyWhereArgs()
    {
        ArrayList<String> args = new ArrayList<>();

        try
        {
            Field[] fields = getClass().getDeclaredFields();
            for (Field field : fields)
            {
                field.setAccessible(true);

                UUSqlColumn columnAnnotation = field.getAnnotation(UUSqlColumn.class);
                if (columnAnnotation != null)
                {
                    if (isPrimaryKeyColumn(columnAnnotation))
                    {
                        Object val = field.get(this);
                        if (val != null)
                        {
                            args.add(val.toString());
                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), "getPrimaryKeyWhereClause", ex);
        }

        return args.toArray(new String[args.size()]);
    }

    /**
     * Creates a ContentValues object populated with data from this object
     *
     * @param version of content values to get
     * @return a ContentValues object
     */
    @NonNull
    default ContentValues getContentValues(final int version)
    {
        ContentValues cv = new ContentValues();

        try
        {
            Field[] fields = getClass().getDeclaredFields();
            for (Field field : fields)
            {
                field.setAccessible(true);

                UUSqlColumn columnAnnotation = field.getAnnotation(UUSqlColumn.class);
                if (columnAnnotation != null)
                {
                    if (version >= columnAnnotation.existsInVersion())
                    {
                        UUContentValues.putObjectIfNotNull(cv, columnNameForField(field), field.get(this));
                    }
                }
            }
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), "getContentValues", ex);
        }

        return cv;
    }

    /**
     * Fills data in this object from a SQLite cursor
     * @param cursor the cursor to fill from
     */
    default void fillFromCursor(@NonNull final Cursor cursor)
    {
        try
        {
            Field[] fields = getClass().getDeclaredFields();
            for (Field field : fields)
            {
                field.setAccessible(true);

                UUSqlColumn columnAnnotation = field.getAnnotation(UUSqlColumn.class);
                if (columnAnnotation != null)
                {
                    Object fieldValue = getField(cursor, field);
                    setField(this, field, fieldValue);
                }
            }
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), "fillFromCursor", ex);
        }
    }

    static Object getField(@NonNull final Cursor cursor, @NonNull final Field field)
    {
        try
        {
            Class<?> fieldType = field.getType();
            String column = columnNameForField(field);
            //return UUCursor.safeGet(cursor, column, null);
            return UUCursor.safeGet(fieldType, cursor, column, null);

            /*
            if (fieldType == long.class)
            {
                return UUCursor.safeGetLong(cursor, column, 0L);
            }
            else if (fieldType == int.class)
            {
                return UUCursor.safeGetInt(cursor, column,0);
            }
            else if (fieldType == short.class)
            {
                return UUCursor.safeGetShort(cursor, column,(short)0);
            }
            else if (fieldType == byte.class)
            {
                return (byte)(int)(UUCursor.safeGetInt(cursor, column,0));
            }
            else if (fieldType == float.class)
            {
                return UUCursor.safeGetFloat(cursor, column, 0.0f);
            }
            else if (fieldType == double.class)
            {
                return UUCursor.safeGetDouble(cursor, column, (double)0.0f);
            }
            else if (fieldType == boolean.class)
            {
                return UUCursor.safeGetBoolean(cursor, column, false);
            }
            else if (fieldType == char.class)
            {
                return (char)(byte)(int)UUCursor.safeGetInt(cursor, column, 0);
            }
            else if (fieldType == Long.class)
            {
                return UUCursor.safeGetLong(cursor, column);
            }
            else if (fieldType == Integer.class)
            {
                return UUCursor.safeGetInt(cursor, column);
            }
            else if (fieldType == Short.class)
            {
                return UUCursor.safeGetShort(cursor, column);
            }
            else if (fieldType == Byte.class)
            {
                return (byte)(int)(UUCursor.safeGetInt(cursor, column,0));
            }
            else if (fieldType == Float.class)
            {
                return UUCursor.safeGetFloat(cursor, column);
            }
            else if (fieldType == Double.class)
            {
                return UUCursor.safeGetDouble(cursor, column);
            }
            else if (fieldType == Boolean.class)
            {
                return UUCursor.safeGetBoolean(cursor, column);
            }
            else if (fieldType == Character.class)
            {
                return (char)(byte)(int)UUCursor.safeGetInt(cursor, column, 0);
            }
            else if (String.class == fieldType)
            {
                return UUCursor.safeGetString(cursor, column);
            }
            else if (Byte[].class == fieldType)
            {
                byte[] val = UUCursor.safeGetBlob(cursor, column);
                if (val != null)
                {

                    Byte[] tmp = new Byte[val.length];
                    for (int i = 0; i < tmp.length; i++)
                    {
                        tmp[i] = val[i];
                    }

                    return tmp;
                }
            }
            else if (byte[].class == fieldType)
            {
                return UUCursor.safeGetBlob(cursor, column);
            }*/
        }
        catch (Exception ex)
        {
            UULog.debug(UUDataModel.class, "getField", "Field: " + field.getName() + ", Column: " + columnNameForField(field), ex);
        }

        return null;
    }

    static void setField(@NonNull final Object object, @NonNull final Field field, @Nullable final Object value)
    {
        try
        {
            field.set(object, value);
        }
        catch (Exception ex)
        {
            UULog.debug(UUDataModel.class, "setField", "Field: " + field.getName() + ", Column: " + columnNameForField(field), ex);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Static Helpers
    ////////////////////////////////////////////////////////////////////////////////////////////////

    static boolean isPrimaryKeyColumn(@NonNull final UUSqlColumn columnAnnotation)
    {
        return (columnAnnotation.primaryKey() || columnAnnotation.type() == UUSqlColumn.Type.INTEGER_PRIMARY_KEY_AUTOINCREMENT);
    }

    @NonNull
    static String tableNameForClass(@NonNull Class<?> tableClass)
    {
        String tableName = null;

        UUSqlTable annotation = tableClass.getAnnotation(UUSqlTable.class);
        if (annotation != null)
        {
            tableName = annotation.tableName();
        }

        if (UUString.isEmpty(tableName))
        {
            tableName = UUString.toSnakeCase(tableClass.getSimpleName());
        }

        return tableName;
    }

    @NonNull
    static String columnNameForField(@NonNull final Field field)
    {
        String columnName = null;

        UUSqlColumn annotation = field.getAnnotation(UUSqlColumn.class);
        if (annotation != null)
        {
            columnName = annotation.name();
        }

        if (UUString.isEmpty(columnName))
        {
            columnName = UUString.toSnakeCase(field.getName());
        }

        return columnName;
    }

    @NonNull
    static String columnTypeForField(@NonNull final Field field)
    {
        String columnType = null;

        UUSqlColumn annotation = field.getAnnotation(UUSqlColumn.class);
        if (annotation != null)
        {
            columnType = annotation.type().toString();
        }

        if (UUString.isEmpty(columnType))
        {
            Class fieldType = field.getType();

            if (String.class == fieldType)
            {
                columnType = UUSqlColumn.Type.TEXT.toString();
            }
            else if (Byte[].class == fieldType || byte[].class == fieldType)
            {
                columnType = UUSqlColumn.Type.BLOB.toString();
            }
            else if (Long.class == fieldType || long.class == fieldType)
            {
                columnType = UUSqlColumn.Type.INT_64.toString();
            }
            else if (Integer.class == fieldType || int.class == fieldType)
            {
                columnType = UUSqlColumn.Type.INT_32.toString();
            }
            else if (Short.class == fieldType || short.class == fieldType)
            {
                columnType = UUSqlColumn.Type.INT_16.toString();
            }
            else if (Byte.class == fieldType || byte.class == fieldType ||
                     Boolean.class == fieldType || boolean.class == fieldType)
            {
                columnType = UUSqlColumn.Type.INT_8.toString();
            }
            else if (Double.class == fieldType || double.class == fieldType ||
                     Float.class == fieldType || float.class == fieldType)
            {
                columnType = UUSqlColumn.Type.REAL.toString();
            }
        }

        if (UUString.isEmpty(columnType))
        {
            columnType = UUSqlColumn.Type.TEXT.toString();
        }

        return columnType;
    }

    @NonNull
    static String buildColumnCreateType(@NonNull final Field field, @NonNull final UUSqlColumn columnAnnotation)
    {
        StringBuilder sb = new StringBuilder();

        try
        {
            field.setAccessible(true);

            sb.append(columnTypeForField(field));

            if (columnAnnotation.nullable())
            {
                sb.append(" NULLABLE");
            }
            else if (columnAnnotation.nonNull())
            {
                sb.append(" NOT NULL");
            }

            String defaultValue = columnAnnotation.defaultValue();
            if (defaultValue.length() > 0)
            {
                sb.append(" DEFAULT ");
                sb.append(defaultValue);
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUDataModel.class, "buildColumnCreateType", ex);
        }

        if (sb.length() == 0)
        {
            sb.append(UUSqlColumn.Type.TEXT);
        }

        return sb.toString();
    }
}
