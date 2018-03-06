package uu.toolbox.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;

import java.util.ArrayList;

import uu.toolbox.core.UUCloseable;
import uu.toolbox.core.UUObject;
import uu.toolbox.core.UUString;
import uu.toolbox.logging.UULog;

/**
 * UUDatabase
 * 
 * Useful Utilities - Extension methods for SQLiteDatabase
 *  
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class UUDatabase
{
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Private Variables
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    private final Context applicationContext;
    private UUSQLiteDatabase database;
    private final UUDatabaseDefinition databaseDefinition;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Construction
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    protected UUDatabase(@NonNull final Context context, @NonNull final UUDatabaseDefinition databaseDefinition)
    {
        applicationContext = context.getApplicationContext();
        this.databaseDefinition = databaseDefinition;
    }

    public Context getApplicationContext()
    {
        return applicationContext;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Database Lifecyle
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void destroy()
    {
        safeClose(getReadWriteDatabase());
        safeDeleteDatabase();
        database = null;
        openDatabase();
    }

    public UUSQLiteDatabase getReadOnlyDatabase()
    {
        if (database == null)
        {
            database = openDatabase();
        }

        return database;
    }

    public UUSQLiteDatabase getReadWriteDatabase()
    {
        if (database == null)
        {
            database = openDatabase();
        }

        return database;
    }

    @NonNull
    public UUDatabaseDefinition getDatabaseDefinition()
    {
        return databaseDefinition;
    }

    @NonNull
    public String getDatabaseName()
    {
        return databaseDefinition.getDatabaseName();
    }


    @NonNull
    protected abstract UUSQLiteDatabase openDatabase();

    protected void handleCreate(@NonNull UUSQLiteDatabase db)
    {
        try
        {
            int version = db.getVersion();

            ArrayList<String> lines = new ArrayList<>();
            UUSql.appendCreateLines(lines, databaseDefinition, version);

            db.beginTransaction();

            for (String line : lines)
            {
                UULog.debug(getClass(), "handleCreate", line);
                db.execSQL(line, null);
            }

            handlePostCreate(db, version);

            db.setTransactionSuccessful();
        }
        catch (Exception ex)
        {
            logException("handleCreate", ex);
        }
        finally
        {
            safeEndTransaction(db);
        }
    }

    protected void handleOpen(@NonNull UUSQLiteDatabase db)
    {
        try
        {
            handlePostOpen(db, db.getVersion());
        }
        catch (Exception ex)
        {
            logException("handleOpen", ex);
        }
    }

    protected void handleMigrate(@NonNull final UUSQLiteDatabase db, final int oldVersion, final int newVersion)
    {
        try
        {
            ArrayList<String> lines = new ArrayList<>();
            UUSql.appendUpgradeLines(lines, databaseDefinition, oldVersion, newVersion);

            db.beginTransaction();

            for (String line : lines)
            {
                UULog.debug(getClass(), "handleMigrate", line);
                db.execSQL(line, null);
            }

            handlePostUpgrade(db, oldVersion, newVersion);

            db.setTransactionSuccessful();
        }
        catch (Exception ex)
        {
            logException("handleMigrate", ex);
        }
        finally
        {
            safeEndTransaction(db);
        }
    }

    protected void handlePostOpen(@NonNull UUSQLiteDatabase db, int version)
    {

    }

    protected void handlePostCreate(@NonNull UUSQLiteDatabase db, int version)
    {

    }

    protected void handlePostUpgrade(@NonNull UUSQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

	////////////////////////////////////////////////////////////////////////////////////////////////
    // UUDataModel Methods
	////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Queries the database for a set of objects
     *
     * @param type row object type
     * @param selection where clause
     * @param selectionArgs bound where arguments
     * @param orderBy order by clause
     * @param limit limit clause
     * @return a List of 
     */
    @NonNull
    public synchronized <T extends UUDataModel> ArrayList<T> queryMultipleObjects(
        @NonNull final Class<T> type,
        @Nullable final String selection,
        @Nullable String[] selectionArgs,
        @Nullable String orderBy,
        @Nullable String limit)
    {
    	ArrayList<T> results = new ArrayList<>();

		Cursor c = null;
		
    	try
    	{
            UUSQLiteDatabase db = getReadOnlyDatabase();

		    T dataModel = type.newInstance();

		    c = db.query(dataModel.getTableName(), UUSql.getColumnNames(dataModel, db.getVersion()), selection, selectionArgs, null, null, orderBy, limit);
		    
		    while (c.moveToNext())
		    {
		    	T obj = type.newInstance();
		    	obj.fillFromCursor(c);
		    	results.add(obj);
		    }
    	}
    	catch (Exception ex)
    	{
    		logException("queryMultipleObjects", ex);
    	}
    	finally
    	{
            UUCloseable.safeClose(c);
    	}
    	
    	return results;
    }
    
    /**
     * Queries the database for a set of objects
     *
     * @param type row object type
     * @param rawSqlQuery The sql query
     * @param selectionArgs bound where arguments
     * @return a List of objects of type T
     */
    @NonNull
    public synchronized <T extends UUDataModel> ArrayList<T> rawQueryMultipleObjects(
        @NonNull final Class<T> type,
        @NonNull final String rawSqlQuery,
        @Nullable final String[] selectionArgs)
    {
    	ArrayList<T> results = new ArrayList<>();

		Cursor c = null;
		
    	try
    	{
            UUSQLiteDatabase db = getReadOnlyDatabase();

		    c = db.rawQuery(rawSqlQuery, selectionArgs);
		    
		    while (c.moveToNext())
		    {
		    	T obj = type.newInstance();
		    	obj.fillFromCursor(c);
		    	results.add(obj);
		    }
    	}
    	catch (Exception ex)
    	{
    		logException("rawQueryMultipleObjects", ex);
    	}
    	finally
    	{
            UUCloseable.safeClose(c);
    	}
    	
    	return results;
    }
    
    /**
     * Queries the database for a single object
     *
     * @param type row object type
     * @param selection where clause
     * @param selectionArgs bound where arguments
     * @param orderBy order by clause
     * @return a List of 
     */
    @Nullable
    public synchronized <T extends UUDataModel> T querySingleObject(
        @NonNull final Class<T> type,
        @Nullable final String selection,
        @Nullable final String[] selectionArgs,
        @Nullable final String orderBy)
    {
    	ArrayList<T> list = queryMultipleObjects(type, selection, selectionArgs, orderBy, "1");
    	if (list.size() == 1)
    	{
    		return list.get(0);
    	}

    	return null;
    }

    /**
     * Inserts an object and then queries it
     *
     * @param type row object type
     * @param object the object to update
     * @return an object of type T
     */
    public synchronized <T extends UUDataModel> T insertObject(
        @NonNull final Class<T> type,
        @NonNull T object)
    {
        UUSQLiteDatabase db = getReadOnlyDatabase();
        ContentValues cv = object.getContentValues(db.getVersion());
    	long rowid = insert(object.getTableName(), cv);
    	return querySingleObject(type, "ROWID = ?", new String[] { String.valueOf(rowid) }, null);
    }

    /**
     * Inserts or Updates an object
     *
     * @param type row object type
     * @param object the object to update
     * @return an object of type T
     */
    public synchronized <T extends UUDataModel> T updateObject(
        @NonNull final Class<T> type,
        @NonNull T object)
    {
    	String whereClause = object.getPrimaryKeyWhereClause();
    	String[] whereArgs = object.getPrimaryKeyWhereArgs();


    	// TODO: Optimize this with an exists query
        T lookup = querySingleObject(type, whereClause, whereArgs, null);
        if (lookup == null)
        {
            return insertObject(type, object);
        }
        else
        {
            UUSQLiteDatabase db = getReadOnlyDatabase();
            update(object.getTableName(), object.getContentValues(db.getVersion()), whereClause, whereArgs);
            return querySingleObject(type, whereClause, whereArgs, null);
        }
    }

    /**
     * Deletes an object
     *
     * @param type row object type
     * @param object the object to update
     *
     */
    public synchronized <T extends UUDataModel> void deleteObject(
            @NonNull final Class<T> type,
            @NonNull T object)
    {
        String whereClause = object.getPrimaryKeyWhereClause();
        String[] whereArgs = object.getPrimaryKeyWhereArgs();
        delete(object.getTableName(), whereClause, whereArgs);
    }

    /**
     * Inserts a list of records
     *
     * @param type model type to insert
     * @param list records to insert
     * @param <T> model type
     */
    public synchronized <T extends UUDataModel> void bulkInsert(
            @NonNull final Class<T> type,
            @NonNull final ArrayList<T> list)
    {
        UUSQLiteDatabase db = null;

        try
        {
            db = getReadWriteDatabase();

            db.beginTransaction();

            for (T row : list)
            {
                db.insert(row.getTableName(), null, row.getContentValues(db.getVersion()));
            }

            db.setTransactionSuccessful();
        }
        catch (Exception ex)
        {
            logException("bulkInsert", ex);
        }
        finally
        {
            safeEndTransaction(db);
        }
    }

    /**
     * Truncates a table and replaces it with the contents of the list passed in
     *
     * @param type the data model type.
     * @param list the list of new data rows to insert.
     * @param <T> the data model type, a class that implements the UUDataModel interface.
     */
    public synchronized <T extends UUDataModel> void bulkReplace(
            @NonNull final Class<T> type,
            @NonNull ArrayList<T> list)
    {
        UUSQLiteDatabase db = null;

        try
        {
            db = getReadWriteDatabase();

            db.beginTransaction();

            UUDataModel dataModel = type.newInstance();
            db.delete(dataModel.getTableName(), null, null);

            for (T row : list)
            {
                db.insert(row.getTableName(), null, row.getContentValues(db.getVersion()));
            }

            db.setTransactionSuccessful();
        }
        catch (Exception ex)
        {
            logException("bulkReplace", ex);
        }
        finally
        {
            safeEndTransaction(db);
        }
    }

    /**
     * Delete's all records from a table based on the model class table name
     *
     */
    public <T extends UUDataModel> void truncateTable(
            @NonNull final Class<T> type)
    {
        truncateTable(UUDataModel.tableNameForClass(type));
    }

    /**
     * Count of all records in a table matching the specified where clause.  If
     * the where clause is null then the query will count all rows in the table
     *
     * @param type modelClass to derive table Nnme from
     * @param where SQL WHERE clause
     * @param whereArgs bound where arguments
     * @return an integer count of records matching the query
     */
    public <T extends UUDataModel> int count(
            @NonNull final Class<T> type,
            @Nullable final String where,
            @Nullable final String[] whereArgs)
    {
        return count(UUDataModel.tableNameForClass(type), where, whereArgs);
    }

    /**
     *
     * Logs all records in a table
     *
     * @param type model type to derive the table name from
     * @param <T> a class that implements UUDataModel
     */
    public <T extends UUDataModel> void logTable(@NonNull final Class<T> type)
    {
        logTable(UUDataModel.tableNameForClass(type));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // ExecSql Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Executes some raw SQL
     *
     * @param sql the sql statement
     */
    public void execSql(@NonNull final String sql)
    {
        execSql(sql, null);
    }

    /**
     * Executes some raw SQL
     *
     * @param sql the sql statement
     * @param bindArgs the sql bind arguments
     */
    public synchronized void execSql(
            @NonNull final String sql,
            @Nullable final Object[] bindArgs)
    {
        try
        {
            UUSQLiteDatabase db = getReadWriteDatabase();
            db.execSQL(sql, bindArgs);
        }
        catch (Exception ex)
        {
            logException("execSql", ex);
        }
    }

    /**
     * Executes some raw SQL lines
     *
     * @param lines the sql statements
     */
    public synchronized void execSqlLines(
            @NonNull final ArrayList<Pair<String, Object[]>> lines)
    {
        UUSQLiteDatabase db = null;

        try
        {
            db = getReadWriteDatabase();

            db.beginTransaction();

            for (Pair<String, Object[]> args : lines)
            {
                execSql(args.first, args.second);
            }

            db.setTransactionSuccessful();
        }
        catch (Exception ex)
        {
            logException("execSqlLines", ex);
        }
        finally
        {
            safeEndTransaction(db);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // SQL Modify Methods (Insert, Update, Replace)
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Inserts a new table row
     *
     * @param tableName name of the table
     * @param cv list of columns to update
     *
     * @return the row id that was updated
     */
    public synchronized long insert(
            @NonNull final String tableName,
            @NonNull final ContentValues cv)
    {
        long rowid;

        try
        {
            UUSQLiteDatabase db = getReadWriteDatabase();
            rowid = db.insert(tableName, null, cv);

            if (rowid == -1)
            {
                UULog.warn(UUDatabase.class, "insert", "DB insert returned -1, this indicates an error!");
            }
        }
        catch (Exception ex)
        {
            logException("insert", ex);
            rowid = -1;
        }

        return rowid;
    }

    /**
     * Inserts or Updates a table row
     *
     * @param tableName name of the table
     * @param cv list of columns to update
     *
     * @return the row id that was updated
     */
    public synchronized long replace(
            @NonNull final String tableName,
            @NonNull final ContentValues cv)
    {
        long rowid;

        try
        {
            UUSQLiteDatabase db = getReadWriteDatabase();
            rowid = db.replace(tableName, null, cv);

            if (rowid == -1)
            {
                UULog.warn(UUDatabase.class, "replace", "DB replace returned -1, this indicates an error!");
            }
        }
        catch (Exception ex)
        {
            logException("replace", ex);
            rowid = -1;
        }

        return rowid;
    }

    /**
     * Updates a table row
     *
     * @param tableName name of the table
     * @param cv list of columns to update
     * @param whereClause the where clause
     * @param whereArgs args
     *
     */
    public synchronized void update(
            @NonNull final String tableName,
            @NonNull final ContentValues cv,
            @Nullable final String whereClause,
            @Nullable final String[] whereArgs)
    {
        try
        {
            UUSQLiteDatabase db = getReadWriteDatabase();
            db.update(tableName, cv, whereClause, whereArgs);
        }
        catch (Exception ex)
        {
            logException("update", ex);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Query Multiple Row Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Query Single Row Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Query Single Cell Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Runs a query expecting a single cell as the result
     *
     * @param resultClass The class of the expected result.  Only valid values are String, byte[]
     *                    or primitive's
     *
     * @param sql the sql to run
     * @param selectionArgs bound where arguments
     * @param defaultValue the default value to return if the result is null
     * @return a result
     */
    @Nullable
    public synchronized <T> T querySingleCell(
            @NonNull Class<T> resultClass,
            @NonNull final String sql,
            @Nullable final String[] selectionArgs,
            @Nullable final T defaultValue)
    {
        Cursor c = null;
        T result = null;

        try
        {
            UUSQLiteDatabase db = getReadOnlyDatabase();

            logSql(sql);
            c = db.rawQuery(sql, selectionArgs);

            if (c.moveToFirst())
            {
                Object objectResult = UUCursor.safeGet(c, 0, null);
                result = UUObject.safeCast(resultClass, objectResult);
            }
        }
        catch (Exception ex)
        {
            logException("querySingleCell", ex);
            result = null;
        }
        finally
        {
            UUCloseable.safeClose(c);
        }

        if (result == null)
        {
            result = defaultValue;
        }

        return result;
    }

    /**
     * Runs a query expecting a single integer cell as the result
     *
     * @param sql the sql to run
     * @param selectionArgs bound where arguments
     * @param defaultValue the default value to return if the result is null
     * @return a result
     */
    @Nullable
    public synchronized String querySingleStringCell(
            @NonNull final String sql,
            @Nullable final String[] selectionArgs,
            @Nullable final String defaultValue)
    {
        return querySingleCell(String.class, sql, selectionArgs, defaultValue);
    }

    /**
     * Runs a query expecting a single integer cell as the result
     *
     * @param sql the sql to run
     * @param selectionArgs bound where arguments
     * @param defaultValue the default value to return if the result is null
     * @return a result
     */
    public synchronized int querySingleIntCell(
            @NonNull final String sql,
            @Nullable final String[] selectionArgs,
            final int defaultValue)
    {
        Integer result = querySingleCell(Integer.class, sql, selectionArgs, null);
        return (result != null ? result : defaultValue);
    }

    /**
     * Runs a query expecting a single long cell as the result
     *
     * @param sql the sql to run
     * @param selectionArgs bound where arguments
     * @param defaultValue the default value to return if the result is null
     * @return a result
     */
    public synchronized long querySingleLongCell(
            @NonNull final String sql,
            @Nullable final String[] selectionArgs,
            final long defaultValue)
    {
        Long result = querySingleCell(Long.class, sql, selectionArgs, null);
        return (result != null ? result : defaultValue);
    }

    /**
     * Runs a query expecting a single float cell as the result
     *
     * @param sql the sql to run
     * @param selectionArgs bound where arguments
     * @param defaultValue the default value to return if the result is null
     * @return a result
     */
    public synchronized float querySingleFloatCell(
            @NonNull final String sql,
            @Nullable final String[] selectionArgs,
            final float defaultValue)
    {
        Float result = querySingleCell(Float.class, sql, selectionArgs, null);
        return (result != null ? result : defaultValue);
    }

    /**
     * Runs a query expecting a single float cell as the result
     *
     * @param sql the sql to run
     * @param selectionArgs bound where arguments
     * @param defaultValue the default value to return if the result is null
     * @return a result
     */
    @Nullable
    public synchronized byte[] querySingleBlobCell(
            @NonNull final String sql,
            @Nullable final String[] selectionArgs,
            @Nullable final byte[] defaultValue)
    {
        return querySingleCell(byte[].class, sql, selectionArgs, null);
    }

    /**
     * Count of all records in a table matching the specified where clause.  If
     * the where clause is null then the query will count all rows in the table
     *
     * @param tableName tableName
     * @param where SQL WHERE clause
     * @param whereArgs bound where arguments
     * @return an integer count of records matching the query
     */
    public int count(
            @NonNull final String tableName,
            @Nullable final String where,
            @Nullable final String[] whereArgs)
    {
        String sql = UUSql.buildCountSql(tableName, where);
        return querySingleIntCell(sql, whereArgs, 0);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Query Single Column Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Runs a query expecting a single column of strings
     *
     * @param sql the sql to run
     * @return a result
     */
    @NonNull
    public synchronized ArrayList<String> listSingleStringColumn(
            @NonNull final String sql,
            @Nullable final String[] args)
    {
        Cursor c = null;
        ArrayList<String> results = new ArrayList<>();

        try
        {
            UUSQLiteDatabase db = getReadOnlyDatabase();

            logSql(sql);
            c = db.rawQuery(sql, args);

            while (c.moveToNext())
            {
                String val = c.getString(0);
                results.add(val);
            }
        }
        catch (Exception ex)
        {
            logException("listSingleStringColumn", ex);
        }
        finally
        {
            UUCloseable.safeClose(c);
        }

        return results;
    }

    /**
     * Lists all table names in a SQLiteDatabase
     *
     * @return a list of table names
     */
    @NonNull
    public ArrayList<String> listTableNames()
    {
        String sql = "SELECT name FROM sqlite_master WHERE type='table';";
        return listSingleStringColumn(sql, null);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Delete Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Deletes a table row
     *
     * @param tableName name of the table
     * @param whereClause WHERE clause to use when deleting
     * @param whereArgs bound arguments applied to ?'s in the whereClause
     */
    public synchronized void delete(
            @NonNull final String tableName,
            @Nullable final String whereClause,
            @Nullable final String[] whereArgs)
    {
        UUSQLiteDatabase db = null;

        try
        {
            db = getReadWriteDatabase();

            db.beginTransaction();
            db.delete(tableName, whereClause, whereArgs);
            db.setTransactionSuccessful();
        }
        catch (Exception ex)
        {
            logException("delete", ex);
        }
        finally
        {
            safeEndTransaction(db);
        }
    }

    /**
     *
     * Delete's all records from a table
     *
     * @param tableName the table to truncate
     *
     */
    public void truncateTable(@NonNull final String tableName)
    {
        delete(tableName, null, null);
    }

    /**
     * WARNING - This method will delete all rows from all tables
     *
     */
    public synchronized void resetDatabase()
    {
        try
        {
            ArrayList<String> tables = listTableNames();
            for (String table : tables)
            {
                // This is a special android table that we will leave alone.
                if ("android_metadata".equalsIgnoreCase(table))
                {
                    continue;
                }

                truncateTable(table);
            }
        }
        catch (Exception ex)
        {
            logException("resetDatabase", ex);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Table Logging Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     *
     * Logs all records in a table
     *
      * @param tableName table name to query
     */
    public void logTable(@NonNull final String tableName)
    {
        logTable(tableName, null, null, null);
    }

    /**
     *
     * Logs all records matching the given query
     *
     * @param tableName table name to query
     * @param columns the columns to select
     * @param where the SQL Where clause to use
     * @param whereArgs SQL Where bound arguments
     */
    public synchronized void logTable(
        @NonNull final String tableName,
        @Nullable final String[] columns,
        @Nullable final String where,
        @Nullable final String[] whereArgs)
    {
        Cursor c = null;

        try
        {
            UUSQLiteDatabase db = getReadOnlyDatabase();

            c = db.query(tableName, columns, where, whereArgs, null, null, null, null);
            logQueryResults(c);
        }
        catch (Exception ex)
        {
            logException("logTable", ex);
        }
        finally
        {
            UUCloseable.safeClose(c);
        }
    }

    /**
     * Logs the results of a query
     *
     * @param c a cursor obtained from a SQL command
     */
    public void logQueryResults(@NonNull final Cursor c)
    {
        try
        {
            c.moveToPosition(-1);

            boolean first = true;
            while (c.moveToNext())
            {
                int columnCount = c.getColumnCount();
                StringBuilder sb = new StringBuilder();

                if (first)
                {
                    for (int i = 0; i < columnCount; i++)
                    {
                        String colName = c.getColumnName(i);
                        sb.append(colName);
                        sb.append(", ");
                    }

                    UULog.debug(UUDatabase.class, "logQueryResults", sb.toString());
                }

                first = false;

                sb = new StringBuilder();

                for (int i = 0; i < columnCount; i++)
                {
                    Object val = UUCursor.safeGet(c, i, null);
                    if (val == null)
                    {
                        sb.append("<null>");
                    }
                    else if (val instanceof byte[])
                    {
                        sb.append(UUString.byteToHex((byte[])val));
                    }
                    else
                    {
                        sb.append(val.toString());
                    }

                    sb.append(", ");
                }

                UULog.debug(UUDataModel.class, "logQueryResults", sb.toString());

            }
        }
        catch (Exception ex)
        {
            logException("logQueryResults", ex);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Helper Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Safely ends a DB transaction
     *
     * @param db the database to end a transaction on
     */
    public void safeEndTransaction(@Nullable final UUSQLiteDatabase db)
    {
        try
        {
            if (db != null)
            {
                db.endTransaction();
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUDatabase.class,"safeEndTransaction", ex);
        }
    }

    public void safeClose(@Nullable final UUSQLiteDatabase db)
    {
        try
        {
            if (db != null)
            {
                db.close();
            }
        }
        catch (Exception ex)
        {
            UULog.debug(UUCloseable.class, "safeClose", ex);
        }
    }

    private void safeDeleteDatabase()
    {
        try
        {
            applicationContext.deleteDatabase(getDatabaseName());
        }
        catch (Exception ex)
        {
            logException("safeDeleteDatabase", ex);
        }
    }

    /**
     * Logs a line of SQL
     *
     * @param sql the sql statement to log
     */
    private void logSql(final String sql)
    {
        UULog.debug(getClass(), "logSql", sql);
    }

    /**
     * Logs an exception
     *
     * @param methodName an explanatory message to go along with the exception
     * @param exception the caught exception
     */
    private void logException(final String methodName, final Exception exception)
    {
        UULog.error(getClass(), methodName, exception);
    }
}