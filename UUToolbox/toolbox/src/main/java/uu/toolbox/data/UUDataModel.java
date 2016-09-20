package uu.toolbox.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;

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
    String getTableName();

    /**
     * Returns the mapping of column name to data type
     *
     * @return a non null hash map of column definitions. Must not be null
     */
    @NonNull
    HashMap<String, String> getColumnMap();

    /**
     * Returns the primary key column name
     *
     * @return May return null if a column definition is a primary key auto increment
     */
    @Nullable
    String getPrimaryKeyColumnName();

    /**
     * Returns a formatted string that will be used as a SQL WHERE clause when looking up
     * an object from this table by primary key.
     *
     * @return A valid Where clause.  For example, "id = ?". Must not be null.
     *
     */
    @NonNull
    String getPrimaryKeyWhereClause();

    /**
     * Returns a string array of the primary key for this object that can be used in SQL WHERE clauses
     *
     * @return A formatted string that will be passed to selection args for Android SQLite methods.
     */
    @NonNull
    String[] getPrimaryKeyWhereArgs();



    ContentValues getContentValues();
    void fillFromCursor(final Cursor cursor);
}
